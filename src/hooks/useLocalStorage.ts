'use client';
import { useState, useCallback } from 'react';
import { getStorageItem, setStorageItem } from '@/lib/storage';

export function useLocalStorage<T>(key: string, initialValue: T): [T, (value: T | ((val: T) => T)) => void] {
  const [storedValue, setStoredValue] = useState<T>(() => {
    if (typeof window === 'undefined') return initialValue;
    return getStorageItem(key, initialValue);
  });

  const setValue = useCallback((value: T | ((val: T) => T)) => {
    setStoredValue((prev) => {
      const valueToStore = value instanceof Function ? value(prev) : value;
      setStorageItem(key, valueToStore);
      return valueToStore;
    });
  }, [key]);

  return [storedValue, setValue];
}
