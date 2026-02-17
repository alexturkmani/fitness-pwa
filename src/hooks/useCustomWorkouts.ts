'use client';
import { useLocalStorage } from './useLocalStorage';
import { CustomWorkoutLog, StorageKeys } from '@/types';
import { formatDate } from '@/lib/utils';

export function useCustomWorkouts() {
  const [logs, setLogs] = useLocalStorage<CustomWorkoutLog[]>(StorageKeys.CUSTOM_WORKOUT_LOGS, []);

  const addLog = (log: CustomWorkoutLog) => {
    setLogs((prev) => [...prev, log]);
  };

  const deleteLog = (logId: string) => {
    setLogs((prev) => prev.filter((l) => l.id !== logId));
  };

  const getLogsByDate = (date: string) => {
    return logs.filter((log) => log.date === date);
  };

  const getRecentLogs = (count: number = 10) => {
    return [...logs].sort((a, b) => b.createdAt.localeCompare(a.createdAt)).slice(0, count);
  };

  const getTodayLog = () => {
    const today = formatDate(new Date());
    return logs.find((log) => log.date === today);
  };

  const getAllExerciseNames = () => {
    const names = new Set<string>();
    logs.forEach((log) => log.exercises.forEach((ex) => names.add(ex.name)));
    return Array.from(names).sort();
  };

  return { logs, addLog, deleteLog, getLogsByDate, getRecentLogs, getTodayLog, getAllExerciseNames };
}
