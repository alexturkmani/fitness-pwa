import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { callGemini, jsonResponse, errorResponse, corsHeaders } from "../_shared/helpers.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const { mealName, foodName, reason, currentMacros } = await req.json();
    if (!foodName || !mealName) return errorResponse("Missing meal or food name", 400);

    const macros = currentMacros || {};
    const prompt = `The user wants to substitute "${foodName}" from their "${mealName}" meal.\nReason: ${reason || "Personal preference"}\n\nCurrent macros: ${macros.calories || 0} cal, ${macros.protein || 0}g protein, ${macros.carbs || 0}g carbs, ${macros.fats || 0}g fats\n\nSuggest 3 alternatives with similar macros, common and easy to prepare.\n\nReturn JSON:\n{\n  "substitutions": [\n    {"name": "Food Name", "servingSize": "portion", "macros": {"calories": 0, "protein": 0, "carbs": 0, "fats": 0}, "reason": "Why good substitute"}\n  ]\n}`;
    const systemPrompt = "You are a sports nutritionist helping with meal substitutions. Suggest practical alternatives with similar nutritional profiles. Always return valid JSON.";
    const result = await callGemini(prompt, systemPrompt);
    return jsonResponse(JSON.parse(result));
  } catch (error: any) {
    console.error("Meal substitution error:", error);
    return errorResponse(error.message || "Failed to find substitutions");
  }
});
