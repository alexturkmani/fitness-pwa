import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import {
  callGemini, generateId, jsonResponse, errorResponse, corsHeaders,
  calculateTDEE, calculateMacroTargets, calculateDailyWaterIntake,
} from "../_shared/helpers.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const { profile, previousLogs, assessment, currentInterval, workoutStyle } = await req.json();
    const gymDays = profile.gymDaysPerWeek || 5;
    const restDays = 7 - gymDays;
    const trainingLocation = (profile.trainingLocation || "gym").toLowerCase();
    const liftingExperience = (profile.liftingExperience || "beginner").toLowerCase();
    const isSingleMuscle = workoutStyle === "single_muscle";

    let prompt = `Generate a structured ${profile.intervalWeeks || 6}-week workout plan for the following user:

- Weight: ${profile.weight}kg
- Height: ${profile.height}cm
- Age: ${profile.age}
- Gender: ${profile.gender}
- Activity Level: ${(profile.activityLevel || "moderately_active").replace("_", " ")}
- Goal: ${(profile.fitnessGoals || ["general_fitness"]).map((g: string) => g.replace("_", " ")).join(", ")}
- Target Weight: ${profile.targetWeight}kg
- Training days per week: ${gymDays} (${restDays} rest day${restDays !== 1 ? "s" : ""})
- Lifting Experience: ${liftingExperience}
- Training Location: ${trainingLocation === "home" ? "HOME (limited equipment)" : "Full Gym"}
`;

    if (assessment) prompt += `\nPrevious interval assessment: ${assessment}\nPlease design the next interval to address the identified weak points.\n`;

    const homeNote = trainingLocation === "home" ? "\n- CRITICAL: This user trains at HOME with NO gym equipment. ONLY use bodyweight exercises, resistance band exercises, or exercises requiring basic household items (chair, towel, water bottles). Do NOT include barbell exercises, cable machines, smith machines, or any gym-specific equipment. Examples of acceptable exercises: push-ups, squats, lunges, planks, burpees, mountain climbers, glute bridges, dumbbell rows (if user has dumbbells), resistance band pulls, wall sits, step-ups on a chair." : "";
    const beginnerNote = liftingExperience === "beginner" ? "\n- IMPORTANT: User is a BEGINNER. Use simpler exercises, lighter rep schemes (3 sets, 10-15 reps)." : "";
    const expertNote = liftingExperience === "expert" ? "\n- User is EXPERT level. Include advanced techniques like drop sets, supersets." : "";

    if (isSingleMuscle) {
      prompt += `\nCRITICAL — SINGLE MUSCLE SPLIT: Each day focuses on ONE muscle. Combine Biceps+Triceps as "Arm Day".${homeNote}${beginnerNote}${expertNote}`;
    } else {
      prompt += `\nWORKOUT STYLE: Muscle Group Split (Push/Pull/Legs or similar).${homeNote}${beginnerNote}${expertNote}`;
    }

    prompt += `\n\nRequirements:
- 7 days (exactly ${restDays} rest, ${gymDays} training)
- 4-6 exercises per day, sets 3-5, reps as range string like "8-12", restSeconds 60-180

Return JSON:
{
  "days": [{"dayNumber": 1, "dayLabel": "Push Day", "isRestDay": false, "exercises": [{"name": "Bench Press", "muscleGroup": "Chest", "sets": 4, "reps": "8-12", "restSeconds": 90, "notes": ""}]}],
  "aiNotes": "Brief explanation"
}`;

    const systemPrompt = "You are an expert fitness coach. Generate structured, evidence-based workout plans. Always return valid JSON. No text outside JSON.";
    const result = await callGemini(prompt, systemPrompt);
    const parsed = JSON.parse(result);

    const startDate = new Date();
    const endDate = new Date();
    endDate.setDate(endDate.getDate() + (profile.intervalWeeks || 6) * 7);

    const plan = {
      id: generateId(),
      intervalNumber: (currentInterval || 0) + 1,
      startDate: startDate.toISOString(),
      endDate: endDate.toISOString(),
      weeks: profile.intervalWeeks || 6,
      days: parsed.days.map((day: any) => ({
        ...day,
        id: generateId(),
        exercises: (day.exercises || []).map((ex: any) => ({ ...ex, id: generateId() })),
      })),
      aiNotes: parsed.aiNotes || "",
      assessmentSummary: assessment,
      createdAt: new Date().toISOString(),
    };

    return jsonResponse(plan);
  } catch (error: any) {
    console.error("Workout generation error:", error);
    return errorResponse(error.message || "Failed to generate workout plan");
  }
});
