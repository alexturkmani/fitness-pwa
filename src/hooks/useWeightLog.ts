'use client';
import { useLocalStorage } from './useLocalStorage';
import { WeightEntry, StorageKeys } from '@/types';
import { formatDate } from '@/lib/utils';

export function useWeightLog() {
  const [entries, setEntries] = useLocalStorage<WeightEntry[]>(StorageKeys.WEIGHT_ENTRIES, []);

  const addEntry = (weight: number) => {
    const entry: WeightEntry = { date: formatDate(new Date()), weight };
    setEntries((prev) => {
      const filtered = prev.filter((e) => e.date !== entry.date);
      return [...filtered, entry].sort((a, b) => a.date.localeCompare(b.date));
    });
  };

  const getLatestWeight = () => {
    return entries.length > 0 ? entries[entries.length - 1].weight : 0;
  };

  const getWeightChange = () => {
    if (entries.length < 2) return 0;
    return entries[entries.length - 1].weight - entries[entries.length - 2].weight;
  };

  return { entries, addEntry, getLatestWeight, getWeightChange };
}
