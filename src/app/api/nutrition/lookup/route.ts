import { NextRequest, NextResponse } from 'next/server';
import { ScannedProduct } from '@/types';

export const dynamic = 'force-dynamic';

export async function GET(request: NextRequest) {
  try {
    const { searchParams } = new URL(request.url);
    const barcode = searchParams.get('barcode');

    if (!barcode) {
      return NextResponse.json({ error: 'Barcode parameter is required' }, { status: 400 });
    }

    const response = await fetch(`https://world.openfoodfacts.org/api/v2/product/${barcode}.json`);

    if (!response.ok) {
      return NextResponse.json({ error: 'Product not found' }, { status: 404 });
    }

    const data = await response.json();

    if (data.status === 0 || !data.product) {
      return NextResponse.json({ error: 'Product not found in database' }, { status: 404 });
    }

    const product = data.product;
    const nutriments = product.nutriments || {};

    const calories = Math.round(nutriments['energy-kcal_100g'] || nutriments['energy-kcal'] || 0);
    const protein = Math.round((nutriments.proteins_100g || nutriments.proteins || 0) * 10) / 10;
    const carbs = Math.round((nutriments.carbohydrates_100g || nutriments.carbohydrates || 0) * 10) / 10;
    const fats = Math.round((nutriments.fat_100g || nutriments.fat || 0) * 10) / 10;

    const proteinCalorieRatio = calories > 0 ? (protein / calories) * 100 : 0;

    const scannedProduct: ScannedProduct = {
      barcode,
      name: product.product_name || 'Unknown Product',
      brand: product.brands || undefined,
      servingSize: product.serving_size || '100g',
      macros: { calories, protein, carbs, fats },
      proteinCalorieRatio: Math.round(proteinCalorieRatio * 10) / 10,
      imageUrl: product.image_url || undefined,
    };

    return NextResponse.json(scannedProduct);
  } catch (error: any) {
    console.error('Nutrition lookup error:', error);
    return NextResponse.json({ error: error.message || 'Failed to look up product' }, { status: 500 });
  }
}
