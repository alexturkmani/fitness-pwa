import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import {
  jsonResponse, errorResponse, corsHeaders, requirePremium,
} from "../_shared/helpers.ts";
import { selectNutritionBasis } from "./basis.ts";

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: corsHeaders() });

  try {
    const premiumError = await requirePremium(req);
    if (premiumError) return premiumError;

    const url = new URL(req.url);
    let barcode = url.searchParams.get("barcode");

    // Android client POSTs JSON body { barcode }; also accept query for older callers
    if (!barcode && req.method !== "GET") {
      try {
        const body = await req.json();
        barcode = body?.barcode ? String(body.barcode) : null;
      } catch {
        // ignore empty/invalid body
      }
    }

    if (!barcode) return errorResponse("Barcode parameter is required", 400);

    const response = await fetch(
      `https://world.openfoodfacts.org/api/v2/product/${encodeURIComponent(barcode)}.json`,
    );
    if (!response.ok) return errorResponse("Product not found", 404);

    const data = await response.json();
    if (data.status === 0 || !data.product) return errorResponse("Product not found in database", 404);

    const product = data.product;
    const { servingSize, macros, proteinCalorieRatio } = selectNutritionBasis(product);

    return jsonResponse({
      barcode,
      name: product.product_name || "Unknown Product",
      brand: product.brands || undefined,
      servingSize,
      macros,
      proteinCalorieRatio,
      imageUrl: product.image_url || undefined,
    });
  } catch (error: any) {
    console.error("Nutrition lookup error:", error);
    return errorResponse(error.message || "Failed to look up product");
  }
});
