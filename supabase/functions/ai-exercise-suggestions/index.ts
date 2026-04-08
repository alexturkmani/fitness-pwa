import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { callGemini, jsonResponse, errorResponse, corsHeaders } from "../_shared/helpers.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const { exercises, goals } = await req.json();
    if (!exercises || exercises.length === 0) return errorResponse("No exercises provided", 400);

    const exerciseSummary = exercises.map((ex: any) => ({
      name: ex.name,
      muscleGroup: ex.muscleGroup,
      sets: (ex.sets || []).map((s: any) => `${s.weight}kg x ${s.reps} reps`),
    }));

    const prompt = `Analyze each exercise and provide improvement suggestions:\n\nUser goals: ${(goals || ["general_fitness"]).map((g: string) => g.replace("_", " ")).join(", ")}\n\nExercises:\n${JSON.stringify(exerciseSummary, null, 2)}\n\nFor EACH exercise provide assessment, 2-3 tips, and 2-3 alternatives.\n\nReturn JSON:\n{\n  "suggestions": [\n    {\n      "exerciseName": "Name",\n      "assessment": "Brief assessment",\n      "improvementTips": ["tip 1", "tip 2"],\n      "alternatives": [{"name": "Alt Exercise", "reason": "Why good alternative"}]\n    }\n  ]\n}`;
    const systemPrompt = "You are an expert strength coach. Analyze exercises and provide actionable tips and alternative suggestions. Always return valid JSON only.";
    const result = await callGemini(prompt, systemPrompt);
    return jsonResponse(JSON.parse(result));
  } catch (error: any) {
    console.error("Exercise suggestion error:", error);
    return errorResponse(error.message || "Failed to generate suggestions");
  }
});
