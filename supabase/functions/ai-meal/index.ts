import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import {
  callGemini, generateId, jsonResponse, errorResponse, corsHeaders,
  calculateMacroTargets, calculateDailyWaterIntake, calculateTDEE,
} from "../_shared/helpers.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const { profile, allergies } = await req.json();
    const tdee = calculateTDEE(profile);
    const macros = calculateMacroTargets(profile);
    const dailyWaterMl = calculateDailyWaterIntake(profile);
    const dailyWaterLiters = (dailyWaterMl / 1000).toFixed(1);

    let allergyClause = "";
    if (allergies && allergies.length > 0) {
      allergyClause = `\n- ALLERGIES: ${allergies.join(", ")} — STRICTLY avoid.\n`;
    }

    const prompt = `Generate a complete daily meal plan for:

- Weight: ${profile.weight}kg, Height: ${profile.height}cm, Age: ${profile.age}, Gender: ${profile.gender}
- Goal: ${(profile.fitnessGoals || ["general_fitness"]).map((g: string) => g.replace("_", " ")).join(", ")}
- TDEE: ${tdee} calories
- Target Macros: ${macros.protein}g protein, ${macros.carbs}g carbs, ${macros.fats}g fats, ${macros.calories} calories${allergyClause}

Requirements:
- 5 meals: Breakfast, Morning Snack, Lunch, Afternoon Snack, Dinner
- Common foods with specific portions, accurate macros
- Daily totals within 5% of targets
- Water intake: ${dailyWaterLiters}L (${dailyWaterMl}ml)

Return JSON:
{
  "meals": [{"name": "Breakfast", "foods": [{"name": "Oatmeal", "servingSize": "80g dry", "macros": {"calories": 300, "protein": 10, "carbs": 54, "fats": 5}}], "totalMacros": {"calories": 500, "protein": 30, "carbs": 60, "fats": 15}}],
  "dailyTotals": {"calories": ${macros.calories}, "protein": ${macros.protein}, "carbs": ${macros.carbs}, "fats": ${macros.fats}},
  "dailyWaterIntakeMl": ${dailyWaterMl},
  "aiNotes": "Explanation"
}`;

    const systemPrompt = "You are a certified sports nutritionist. Generate practical, balanced meal plans with accurate macro calculations. Always return valid JSON. No text outside JSON.";
    const result = await callGemini(prompt, systemPrompt);
    const parsed = JSON.parse(result);

    const roundMacros = (m: any) => ({
      calories: Math.round(m?.calories || 0),
      protein: Math.round(m?.protein || 0),
      carbs: Math.round(m?.carbs || 0),
      fats: Math.round(m?.fats || 0),
      ...(m?.fiber != null ? { fiber: Math.round(m.fiber) } : {}),
    });

    const plan = {
      id: generateId(),
      date: new Date().toISOString().split("T")[0],
      meals: (parsed.meals || []).map((meal: any) => ({
        ...meal,
        id: generateId(),
        foods: (meal.foods || []).map((food: any) => ({
          ...food, id: generateId(), macros: roundMacros(food.macros),
        })),
        totalMacros: roundMacros(meal.totalMacros),
      })),
      dailyTotals: roundMacros(parsed.dailyTotals || macros),
      dailyTargets: macros,
      dailyWaterIntakeMl: parsed.dailyWaterIntakeMl || dailyWaterMl,
      aiNotes: parsed.aiNotes || "",
      createdAt: new Date().toISOString(),
    };

    return jsonResponse(plan);
  } catch (error: any) {
    console.error("Meal plan generation error:", error);
    return errorResponse(error.message || "Failed to generate meal plan");
  }
});
