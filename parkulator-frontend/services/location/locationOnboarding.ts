import AsyncStorage from '@react-native-async-storage/async-storage';

const LOCATION_ONBOARDING_DONE_KEY = 'location_onboarding_done';

export async function hasCompletedLocationOnboarding(): Promise<boolean> {
  const value = await AsyncStorage.getItem(LOCATION_ONBOARDING_DONE_KEY);
  return value === 'true';
}

export async function setLocationOnboardingCompleted(): Promise<void> {
  await AsyncStorage.setItem(LOCATION_ONBOARDING_DONE_KEY, 'true');
}