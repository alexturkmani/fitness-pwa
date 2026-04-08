import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { callGemini, jsonResponse, errorResponse, corsHeaders } from "../_shared/helpers.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const body = await req.json();

    if (body.type === "workout") {
      const summary = body.logs.map((log: any) => ({
        date: log.date,
        exercises: (log.exercises || []).map((e: any) => ({
          name: e.exerciseName,
          sets: (e.sets || []).map((s: any) => `${s.weight}kg x ${s.reps} reps`).join(", "),
        })),
      }));

      const prompt = `Analyze the following workout logs and provide an assessment:\n\n${JSON.stringify(summary, null, 2)}\n\nReturn JSON:\n{\n  "overallScore": "good/moderate/needs_improvement",\n  "consistency": "description",\n  "progressiveOverload": "analysis",\n  "weakPoints": ["list"],\n  "recommendations": ["list"],\n  "summary": "One paragraph"\n}`;
      const result = await callGemini(prompt, "You are an expert fitness coach analyzing workout performance. Provide honest, actionable assessments. Always return valid JSON.");
      return jsonResponse(JSON.parse(result));
    }

    if (body.type === "food") {
      const { productName, macros, ratio } = body;
      const prompt = `A user scanned: ${productName}\nCalories: ${macros.calories}, Protein: ${macros.protein}g, Carbs: ${macros.carbs}g, Fats: ${macros.fats}g\nProtein-to-calorie ratio: ${ratio.toFixed(1)}g per 100 cal (${ratio >= 10 ? "good" : ratio >= 5 ? "moderate" : "poor"})\n\n${ratio < 10 ? "Suggest 3 better alternatives with higher protein-to-calorie ratios." : "Briefly confirm this is a good choice."}\n\nReturn JSON:\n{\n  "assessment": "Brief assessment",\n  "alternatives": [{"name": "Product", "typicalMacros": {"calories": 100, "protein": 20, "carbs": 5, "fats": 2}, "reason": "Why better"}]\n}`;
      const result = await callGemini(prompt, "You are a sports nutrition expert. Assess food products and suggest better alternatives. Always return valid JSON.");
      return jsonResponse(JSON.parse(result));
    }

    return errorResponse("Invalid assessment type", 400);
  } catch (error: any) {
    console.error("Assessment error:", error);
    return errorResponse(error.message || "Failed to generate assessment");
  }
});
