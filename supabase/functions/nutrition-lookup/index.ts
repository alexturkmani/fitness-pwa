import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { jsonResponse, errorResponse, corsHeaders } from "../_shared/helpers.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const url = new URL(req.url);
    const barcode = url.searchParams.get("barcode");
    if (!barcode) return errorResponse("Barcode parameter is required", 400);

    const response = await fetch(`https://world.openfoodfacts.org/api/v2/product/${encodeURIComponent(barcode)}.json`);
    if (!response.ok) return errorResponse("Product not found", 404);

    const data = await response.json();
    if (data.status === 0 || !data.product) return errorResponse("Product not found in database", 404);

    const product = data.product;
    const n = product.nutriments || {};
    const calories = Math.round(n["energy-kcal_100g"] || n["energy-kcal"] || 0);
    const protein = Math.round((n.proteins_100g || n.proteins || 0) * 10) / 10;
    const carbs = Math.round((n.carbohydrates_100g || n.carbohydrates || 0) * 10) / 10;
    const fats = Math.round((n.fat_100g || n.fat || 0) * 10) / 10;
    const proteinCalorieRatio = calories > 0 ? Math.round(((protein / calories) * 100) * 10) / 10 : 0;

    return jsonResponse({
      barcode,
      name: product.product_name || "Unknown Product",
      brand: product.brands || undefined,
      servingSize: product.serving_size || "100g",
      macros: { calories, protein, carbs, fats },
      proteinCalorieRatio,
      imageUrl: product.image_url || undefined,
    });
  } catch (error: any) {
    console.error("Nutrition lookup error:", error);
    return errorResponse(error.message || "Failed to look up product");
  }
});
