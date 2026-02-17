'use client';

/**
 * RevenueCat Web SDK initialization.
 * 
 * SETUP REQUIRED:
 * 1. Create account at https://www.revenuecat.com
 * 2. Connect your Stripe account in RevenueCat dashboard
 * 3. Create a product in Stripe ($4.99/month with 7-day trial)
 * 4. Create an Offering in RevenueCat with a "monthly" package
 * 5. Create an Entitlement called "premium"
 * 6. Add your RevenueCat Web API key to .env.local as NEXT_PUBLIC_RC_API_KEY
 */

let purchasesInstance: any = null;

export async function initRevenueCat(appUserId: string) {
  if (typeof window === 'undefined') return null;

  const apiKey = process.env.NEXT_PUBLIC_RC_API_KEY;
  if (!apiKey) {
    console.warn('RevenueCat API key not configured. Set NEXT_PUBLIC_RC_API_KEY in .env.local');
    return null;
  }

  try {
    const { Purchases } = await import('@revenuecat/purchases-js');
    purchasesInstance = Purchases.configure(apiKey, appUserId);
    return purchasesInstance;
  } catch (error) {
    console.error('Failed to initialize RevenueCat:', error);
    return null;
  }
}

export async function getOfferings() {
  if (!purchasesInstance) return null;
  try {
    return await purchasesInstance.getOfferings();
  } catch (error) {
    console.error('Failed to get offerings:', error);
    return null;
  }
}

export async function purchasePackage(rcPackage: any) {
  if (!purchasesInstance) throw new Error('RevenueCat not initialized');
  return await purchasesInstance.purchase({ rcPackage });
}

export async function checkEntitlement(): Promise<boolean> {
  if (!purchasesInstance) return false;
  try {
    const customerInfo = await purchasesInstance.getCustomerInfo();
    return customerInfo.entitlements.active['premium'] !== undefined;
  } catch (error) {
    console.error('Failed to check entitlement:', error);
    return false;
  }
}
