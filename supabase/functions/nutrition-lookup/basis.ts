// Picks one consistent nutrition basis for an Open Food Facts product.
//
// The returned macros and `servingSize` label must always describe the same
// amount of food: the Android app shows "Per <servingSize>" next to the macros
// and logs a scan as quantity = 1 of that serving.
//
// Order of preference:
//  1. Per serving, when the product has a `serving_size` label and OFF supplies
//     `*_serving` nutriments (gaps may be filled by scaling the per-100 g value
//     with a numeric `serving_quantity` in g/ml).
//  2. Per serving computed from per-100 g values, when `serving_quantity` is a
//     reliable number (positive, unit g/ml or unspecified).
//  3. Per 100 g, labelled "100 g". We never parse the free-text `serving_size`.

export interface LookupNutrition {
  servingSize: string;
  macros: { calories: number; protein: number; carbs: number; fats: number };
  proteinCalorieRatio: number;
}

type Nutriments = Record<string, unknown>;

const NUTRIENTS = {
  calories: "energy-kcal",
  protein: "proteins",
  carbs: "carbohydrates",
  fats: "fat",
} as const;
type Key = keyof typeof NUTRIENTS;
const KEYS = Object.keys(NUTRIENTS) as Key[];

export const PER_100G_LABEL = "100 g";

function num(value: unknown): number | undefined {
  if (typeof value === "number") return Number.isFinite(value) && value >= 0 ? value : undefined;
  if (typeof value === "string" && value.trim() !== "") {
    const parsed = Number(value);
    return Number.isFinite(parsed) && parsed >= 0 ? parsed : undefined;
  }
  return undefined;
}

/** Grams (or ml) in one serving, only when OFF gives a trustworthy number. */
export function reliableServingQuantity(product: Record<string, unknown>): number | undefined {
  const qty = num(product.serving_quantity);
  if (qty === undefined || qty <= 0) return undefined;
  const unit = product.serving_quantity_unit;
  if (unit !== undefined && unit !== null && unit !== "") {
    const u = String(unit).trim().toLowerCase();
    if (u !== "g" && u !== "ml") return undefined;
  }
  return qty;
}

/** Per-100 g value; unsuffixed fields count only when OFF says they are per 100 g. */
function per100(n: Nutriments, product: Record<string, unknown>, key: Key): number | undefined {
  const base = NUTRIENTS[key];
  const v = num(n[`${base}_100g`]);
  if (v !== undefined) return v;
  return product.nutrition_data_per === "100g" ? num(n[base]) : undefined;
}

function round1(v: number): number {
  return Math.round(v * 10) / 10;
}

function ratio(calories: number | undefined, protein: number | undefined): number {
  if (!calories || calories <= 0) return 0;
  return round1(((protein ?? 0) / calories) * 100);
}

function build(label: string, raw: Record<Key, number | undefined>): LookupNutrition {
  return {
    servingSize: label,
    macros: {
      calories: Math.round(raw.calories ?? 0),
      protein: round1(raw.protein ?? 0),
      carbs: round1(raw.carbs ?? 0),
      fats: round1(raw.fats ?? 0),
    },
    // From unrounded values, so the ratio doesn't drift with the basis.
    proteinCalorieRatio: ratio(raw.calories, raw.protein),
  };
}

export function selectNutritionBasis(product: Record<string, unknown>): LookupNutrition {
  const n: Nutriments = (product.nutriments as Nutriments) || {};
  const hundred = {} as Record<Key, number | undefined>;
  for (const k of KEYS) hundred[k] = per100(n, product, k);

  const label = typeof product.serving_size === "string" ? product.serving_size.trim() : "";
  if (label) {
    const qty = reliableServingQuantity(product);
    const serving = {} as Record<Key, number | undefined>;
    let usable = true;
    for (const k of KEYS) {
      const direct = num(n[`${NUTRIENTS[k]}_serving`]);
      const scaled = qty !== undefined && hundred[k] !== undefined ? (hundred[k]! * qty) / 100 : undefined;
      serving[k] = direct ?? scaled;
      // A nutrient known per 100 g but not per serving would silently become 0.
      if (serving[k] === undefined && hundred[k] !== undefined) usable = false;
    }
    if (serving.calories === undefined) usable = false;
    if (usable) return build(label, serving);
  }

  return build(PER_100G_LABEL, hundred);
}
