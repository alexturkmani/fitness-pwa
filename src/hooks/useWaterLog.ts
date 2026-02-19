'use client';
import { useLocalStorage } from './useLocalStorage';
import { WaterLogEntry, StorageKeys } from '@/types';
import { formatDate, generateId } from '@/lib/utils';

export function useWaterLog() {
  const [logs, setLogs] = useLocalStorage<WaterLogEntry[]>(StorageKeys.WATER_LOGS, []);

  const addEntry = (amountMl: number) => {
    const entry: WaterLogEntry = {
      id: generateId(),
      date: formatDate(new Date()),
      amount: amountMl,
      createdAt: new Date().toISOString(),
    };
    setLogs((prev) => [...prev, entry]);
  };

  const removeEntry = (id: string) => {
    setLogs((prev) => prev.filter((e) => e.id !== id));
  };

  const getDayEntries = (date: string) => {
    return logs.filter((e) => e.date === date);
  };

  const getDayTotal = (date: string): number => {
    return getDayEntries(date).reduce((total, entry) => total + entry.amount, 0);
  };

  const getTodayTotal = (): number => {
    return getDayTotal(formatDate(new Date()));
  };

  const getWeekData = () => {
    const today = new Date();
    const days: { date: string; total: number }[] = [];
    for (let i = 6; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(today.getDate() - i);
      const dateStr = formatDate(date);
      days.push({ date: dateStr, total: getDayTotal(dateStr) });
    }
    return days;
  };

  return { logs, addEntry, removeEntry, getDayEntries, getDayTotal, getTodayTotal, getWeekData };
}
