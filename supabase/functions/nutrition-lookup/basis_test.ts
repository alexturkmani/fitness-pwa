// Run: deno test supabase/functions/nutrition-lookup/basis_test.ts
import { assertEquals } from "https://deno.land/std@0.208.0/assert/mod.ts";
import { PER_100G_LABEL, reliableServingQuantity, selectNutritionBasis } from "./basis.ts";

const per100g = {
  "energy-kcal_100g": 400,
  proteins_100g: 10,
  carbohydrates_100g: 60,
  fat_100g: 12.5,
};

Deno.test("uses *_serving values with the serving_size label", () => {
  const r = selectNutritionBasis({
    serving_size: "30 g",
    nutriments: {
      ...per100g,
      "energy-kcal_serving": 120,
      proteins_serving: 3,
      carbohydrates_serving: 18,
      fat_serving: 3.75,
    },
  });
  assertEquals(r.servingSize, "30 g");
  assertEquals(r.macros, { calories: 120, protein: 3, carbs: 18, fats: 3.8 });
  assertEquals(r.proteinCalorieRatio, 2.5);
});

Deno.test("regression: serving_size without *_serving falls back to a 100 g label", () => {
  const r = selectNutritionBasis({ serving_size: "30 g", nutriments: per100g });
  assertEquals(r.servingSize, PER_100G_LABEL);
  assertEquals(r.macros, { calories: 400, protein: 10, carbs: 60, fats: 12.5 });
});

Deno.test("scales per-100 g values by a numeric serving_quantity", () => {
  const r = selectNutritionBasis({
    serving_size: "1 bar (45 g)",
    serving_quantity: 45,
    serving_quantity_unit: "g",
    nutriments: per100g,
  });
  assertEquals(r.servingSize, "1 bar (45 g)");
  assertEquals(r.macros, { calories: 180, protein: 4.5, carbs: 27, fats: 5.6 });
});

Deno.test("accepts serving_quantity sent as a numeric string", () => {
  const r = selectNutritionBasis({ serving_size: "45 g", serving_quantity: "45", nutriments: per100g });
  assertEquals(r.servingSize, "45 g");
  assertEquals(r.macros.calories, 180);
});

Deno.test("fills a missing *_serving macro from serving_quantity", () => {
  const r = selectNutritionBasis({
    serving_size: "30 g",
    serving_quantity: 30,
    nutriments: { ...per100g, "energy-kcal_serving": 120, proteins_serving: 3, carbohydrates_serving: 18 },
  });
  assertEquals(r.servingSize, "30 g");
  assertEquals(r.macros.fats, 3.8);
});

Deno.test("partial *_serving data without a reliable quantity falls back to 100 g", () => {
  const r = selectNutritionBasis({
    serving_size: "30 g",
    nutriments: { ...per100g, "energy-kcal_serving": 120, proteins_serving: 3 },
  });
  assertEquals(r.servingSize, PER_100G_LABEL);
  assertEquals(r.macros.calories, 400);
});

Deno.test("ignores serving_quantity in a non-mass unit", () => {
  const r = selectNutritionBasis({
    serving_size: "1 piece",
    serving_quantity: 1,
    serving_quantity_unit: "piece",
    nutriments: per100g,
  });
  assertEquals(r.servingSize, PER_100G_LABEL);
});

Deno.test("no serving_size uses per-100 g values", () => {
  const r = selectNutritionBasis({ nutriments: per100g });
  assertEquals(r.servingSize, PER_100G_LABEL);
  assertEquals(r.macros.calories, 400);
  assertEquals(r.proteinCalorieRatio, 2.5);
});

Deno.test("blank serving_size is treated as missing", () => {
  const r = selectNutritionBasis({ serving_size: "  ", nutriments: { ...per100g, "energy-kcal_serving": 1 } });
  assertEquals(r.servingSize, PER_100G_LABEL);
});

Deno.test("unsuffixed nutriments count only when declared per 100 g", () => {
  const raw = { "energy-kcal": 250, proteins: 20, carbohydrates: 5, fat: 15 };
  const declared100 = selectNutritionBasis({ nutrition_data_per: "100g", nutriments: raw });
  assertEquals(declared100.macros, { calories: 250, protein: 20, carbs: 5, fats: 15 });

  const declaredServing = selectNutritionBasis({ nutrition_data_per: "serving", nutriments: raw });
  assertEquals(declaredServing.macros, { calories: 0, protein: 0, carbs: 0, fats: 0 });
  assertEquals(declaredServing.proteinCalorieRatio, 0);
});

Deno.test("zero is a real value, not a missing one", () => {
  const r = selectNutritionBasis({
    serving_size: "250 ml",
    nutriments: {
      "energy-kcal_100g": 0, proteins_100g: 0, carbohydrates_100g: 0, fat_100g: 0,
      "energy-kcal_serving": 0, proteins_serving: 0, carbohydrates_serving: 0, fat_serving: 0,
    },
  });
  assertEquals(r.servingSize, "250 ml");
  assertEquals(r.macros, { calories: 0, protein: 0, carbs: 0, fats: 0 });
  assertEquals(r.proteinCalorieRatio, 0);
});

Deno.test("protein ratio matches across bases and uses unrounded values", () => {
  const product = {
    serving_size: "33 g",
    serving_quantity: 33,
    nutriments: { "energy-kcal_100g": 371, proteins_100g: 13.3, carbohydrates_100g: 60, fat_100g: 7 },
  };
  const serving = selectNutritionBasis(product);
  const hundred = selectNutritionBasis({ nutriments: product.nutriments });
  assertEquals(serving.servingSize, "33 g");
  assertEquals(serving.proteinCalorieRatio, hundred.proteinCalorieRatio);
  assertEquals(hundred.proteinCalorieRatio, 3.6);
});

Deno.test("missing product data yields zeros on a 100 g label", () => {
  const r = selectNutritionBasis({});
  assertEquals(r.servingSize, PER_100G_LABEL);
  assertEquals(r.macros, { calories: 0, protein: 0, carbs: 0, fats: 0 });
});

Deno.test("reliableServingQuantity rejects zero, negatives and junk", () => {
  assertEquals(reliableServingQuantity({ serving_quantity: 0 }), undefined);
  assertEquals(reliableServingQuantity({ serving_quantity: -5 }), undefined);
  assertEquals(reliableServingQuantity({ serving_quantity: "abc" }), undefined);
  assertEquals(reliableServingQuantity({ serving_quantity: 30, serving_quantity_unit: "ML" }), 30);
});
