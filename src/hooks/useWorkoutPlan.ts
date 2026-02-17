'use client';
import { useLocalStorage } from './useLocalStorage';
import { WorkoutPlan, StorageKeys } from '@/types';

export function useWorkoutPlan() {
  const [plans, setPlans] = useLocalStorage<WorkoutPlan[]>(StorageKeys.WORKOUT_PLANS, []);

  const currentPlan = plans.length > 0 ? plans[plans.length - 1] : null;

  const savePlan = (plan: WorkoutPlan) => {
    setPlans((prev) => [...prev, plan]);
  };

  const deletePlan = () => {
    setPlans([]);
  };

  const isIntervalComplete = () => {
    if (!currentPlan) return false;
    return new Date() > new Date(currentPlan.endDate);
  };

  const getCurrentWeek = () => {
    if (!currentPlan) return 0;
    const start = new Date(currentPlan.startDate);
    const now = new Date();
    const diffTime = now.getTime() - start.getTime();
    const diffWeeks = Math.floor(diffTime / (7 * 24 * 60 * 60 * 1000));
    return Math.min(diffWeeks + 1, currentPlan.weeks);
  };

  const getDaysRemaining = () => {
    if (!currentPlan) return 0;
    const end = new Date(currentPlan.endDate);
    const now = new Date();
    const diff = end.getTime() - now.getTime();
    return Math.max(0, Math.ceil(diff / (24 * 60 * 60 * 1000)));
  };

  return { plans, currentPlan, savePlan, deletePlan, isIntervalComplete, getCurrentWeek, getDaysRemaining };
}
