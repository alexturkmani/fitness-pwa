'use client';
import { useLocalStorage } from './useLocalStorage';
import { WorkoutLog, StorageKeys } from '@/types';
import { formatDate } from '@/lib/utils';

export function useWorkoutLogs() {
  const [logs, setLogs] = useLocalStorage<WorkoutLog[]>(StorageKeys.WORKOUT_LOGS, []);

  const addLog = (log: WorkoutLog) => {
    setLogs((prev) => [...prev, log]);
  };

  const getLogsByPlan = (planId: string) => {
    return logs.filter((log) => log.planId === planId);
  };

  const getLogsByDate = (date: string) => {
    return logs.filter((log) => log.date === date);
  };

  const getLogsByWeek = (startDate: Date) => {
    const start = formatDate(startDate);
    const end = new Date(startDate);
    end.setDate(end.getDate() + 7);
    const endStr = formatDate(end);
    return logs.filter((log) => log.date >= start && log.date < endStr);
  };

  const getThisWeekCount = () => {
    const now = new Date();
    const dayOfWeek = now.getDay();
    const start = new Date(now);
    start.setDate(now.getDate() - (dayOfWeek === 0 ? 6 : dayOfWeek - 1));
    start.setHours(0, 0, 0, 0);
    return getLogsByWeek(start).length;
  };

  const getTotalVolume = (planId?: string) => {
    const targetLogs = planId ? getLogsByPlan(planId) : logs;
    return targetLogs.reduce((total, log) => {
      return total + log.exercises.reduce((exTotal, ex) => {
        return exTotal + ex.sets.reduce((setTotal, set) => {
          return setTotal + (set.completed ? set.weight * set.reps : 0);
        }, 0);
      }, 0);
    }, 0);
  };

  return { logs, addLog, getLogsByPlan, getLogsByDate, getLogsByWeek, getThisWeekCount, getTotalVolume };
}
