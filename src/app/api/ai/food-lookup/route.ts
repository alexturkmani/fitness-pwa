import { NextRequest, NextResponse } from 'next/server';
import { callAI } from '@/lib/ai';

const systemPrompt = `You are a nutrition database. Given a food name and serving size/weight, return accurate estimated macronutrient values. Use standard USDA or equivalent nutritional data. Always return valid JSON matching the requested structure exactly. Do not include any text outside the JSON.`;

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { foodName, servingSize } = body as { foodName: string; servingSize: string };

    if (!foodName) {
      return NextResponse.json({ error: 'Food name is required' }, { status: 400 });
    }

    const prompt = `Estimate the macronutrients for the following food:

- Food: ${foodName}
- Serving Size: ${servingSize || 'standard serving'}

Return JSON with this exact structure:
{
  "calories": <number>,
  "protein": <number in grams>,
  "carbs": <number in grams>,
  "fats": <number in grams>
}`;

    const result = await callAI(prompt, systemPrompt, 1);

    let parsed;
    try {
      parsed = JSON.parse(result);
    } catch {
      return NextResponse.json({ error: 'Could not estimate nutrition' }, { status: 500 });
    }

    return NextResponse.json({
      calories: Math.round(parsed.calories || 0),
      protein: Math.round(parsed.protein || 0),
      carbs: Math.round(parsed.carbs || 0),
      fats: Math.round(parsed.fats || 0),
    });
  } catch (error: any) {
    console.error('Food lookup error:', error);
    return NextResponse.json({ error: error.message || 'Failed to estimate nutrition' }, { status: 500 });
  }
}
