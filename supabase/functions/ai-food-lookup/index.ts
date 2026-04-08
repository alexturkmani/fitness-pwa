import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { callGemini, jsonResponse, errorResponse, corsHeaders } from "../_shared/helpers.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const { foodName, servingSize } = await req.json();
    if (!foodName) return errorResponse("Food name is required", 400);

    const prompt = `Estimate macronutrients for:\n- Food: ${foodName}\n- Serving Size: ${servingSize || "standard serving"}\n\nReturn JSON:\n{ "calories": <number>, "protein": <number g>, "carbs": <number g>, "fats": <number g> }`;
    const systemPrompt = "You are a nutrition database. Return accurate estimated macronutrient values using standard USDA data. Always return valid JSON only.";
    const result = await callGemini(prompt, systemPrompt, 1);
    const parsed = JSON.parse(result);

    return jsonResponse({
      calories: Math.round(parsed.calories || 0),
      protein: Math.round(parsed.protein || 0),
      carbs: Math.round(parsed.carbs || 0),
      fats: Math.round(parsed.fats || 0),
    });
  } catch (error: any) {
    console.error("Food lookup error:", error);
    return errorResponse(error.message || "Failed to estimate nutrition");
  }
});
