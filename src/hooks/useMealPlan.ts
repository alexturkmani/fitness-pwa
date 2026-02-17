'use client';
import { useLocalStorage } from './useLocalStorage';
import { MealPlan, StorageKeys } from '@/types';

export function useMealPlan() {
  const [plans, setPlans] = useLocalStorage<MealPlan[]>(StorageKeys.MEAL_PLANS, []);

  const currentPlan = plans.length > 0 ? plans[plans.length - 1] : null;

  const savePlan = (plan: MealPlan) => {
    setPlans((prev) => [...prev, plan]);
  };

  const deletePlan = () => {
    setPlans([]);
  };

  return { plans, currentPlan, savePlan, deletePlan };
}
