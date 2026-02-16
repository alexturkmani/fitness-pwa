'use client';
import { useLocalStorage } from './useLocalStorage';
import { FoodLogEntry, MacroNutrients, StorageKeys } from '@/types';
import { formatDate } from '@/lib/utils';

export function useFoodLog() {
  const [logs, setLogs] = useLocalStorage<FoodLogEntry[]>(StorageKeys.FOOD_LOGS, []);

  const addEntry = (entry: FoodLogEntry) => {
    setLogs((prev) => [...prev, entry]);
  };

  const removeEntry = (id: string) => {
    setLogs((prev) => prev.filter((e) => e.id !== id));
  };

  const getDayEntries = (date: string) => {
    return logs.filter((e) => e.date === date);
  };

  const getTodayEntries = () => {
    return getDayEntries(formatDate(new Date()));
  };

  const getDayTotals = (date: string): MacroNutrients => {
    const entries = getDayEntries(date);
    return entries.reduce(
      (totals, entry) => ({
        calories: totals.calories + entry.macros.calories * entry.quantity,
        protein: totals.protein + entry.macros.protein * entry.quantity,
        carbs: totals.carbs + entry.macros.carbs * entry.quantity,
        fats: totals.fats + entry.macros.fats * entry.quantity,
      }),
      { calories: 0, protein: 0, carbs: 0, fats: 0 }
    );
  };

  const getWeekEntries = () => {
    const today = new Date();
    const days: { date: string; entries: FoodLogEntry[]; totals: MacroNutrients }[] = [];
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(today.getDate() - i);
      const dateStr = formatDate(date);
      days.push({
        date: dateStr,
        entries: getDayEntries(dateStr),
        totals: getDayTotals(dateStr),
      });
    }
    return days;
  };

  return { logs, addEntry, removeEntry, getDayEntries, getTodayEntries, getDayTotals, getWeekEntries };
}
