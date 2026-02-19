'use client';
import { useLocalStorage } from './useLocalStorage';
import { CardioLogEntry, StorageKeys } from '@/types';
import { formatDate, generateId } from '@/lib/utils';

export function useCardioLog() {
  const [logs, setLogs] = useLocalStorage<CardioLogEntry[]>(StorageKeys.CARDIO_LOGS, []);

  const addEntry = (entry: Omit<CardioLogEntry, 'id' | 'createdAt'>) => {
    const newEntry: CardioLogEntry = {
      ...entry,
      id: generateId(),
      createdAt: new Date().toISOString(),
    };
    setLogs((prev) => [...prev, newEntry]);
  };

  const removeEntry = (id: string) => {
    setLogs((prev) => prev.filter((e) => e.id !== id));
  };

  const getDayEntries = (date: string) => {
    return logs.filter((e) => e.date === date);
  };

  const getDayCaloriesBurnt = (date: string): number => {
    return getDayEntries(date).reduce((total, entry) => total + entry.estimatedCaloriesBurnt, 0);
  };

  const getTodayCaloriesBurnt = (): number => {
    return getDayCaloriesBurnt(formatDate(new Date()));
  };

  const getWeekData = () => {
    const today = new Date();
    const days: { date: string; entries: CardioLogEntry[]; totalCalories: number; totalMinutes: number }[] = [];
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(today.getDate() - i);
      const dateStr = formatDate(date);
      const dayEntries = getDayEntries(dateStr);
      days.push({
        date: dateStr,
        entries: dayEntries,
        totalCalories: dayEntries.reduce((t, e) => t + e.estimatedCaloriesBurnt, 0),
        totalMinutes: dayEntries.reduce((t, e) => t + e.durationMinutes, 0),
      });
    }
    return days;
  };

  return { logs, addEntry, removeEntry, getDayEntries, getDayCaloriesBurnt, getTodayCaloriesBurnt, getWeekData };
}
