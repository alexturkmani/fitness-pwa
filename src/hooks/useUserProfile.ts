'use client';
import { useLocalStorage } from './useLocalStorage';
import { UserProfile, StorageKeys } from '@/types';

const defaultProfile: UserProfile = {
  id: '',
  weight: 0,
  height: 0,
  age: 0,
  gender: 'male',
  activityLevel: 'moderately_active',
  fitnessGoals: ['general_fitness'],
  targetWeight: 0,
  intervalWeeks: 6,
  onboardingCompleted: false,
  createdAt: '',
  updatedAt: '',
};

export function useUserProfile() {
  const [profile, setProfile] = useLocalStorage<UserProfile>(StorageKeys.USER_PROFILE, defaultProfile);

  const updateProfile = (updates: Partial<UserProfile>) => {
    setProfile((prev) => ({
      ...prev,
      ...updates,
      updatedAt: new Date().toISOString(),
    }));
  };

  const isOnboarded = profile.onboardingCompleted;

  return { profile, setProfile, updateProfile, isOnboarded };
}
