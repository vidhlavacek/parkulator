import * as Location from 'expo-location';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { LOCATION_TASK_NAME } from './locationConstants';
import api from '../api';

const PENDING_LOCATION_KEY = 'pending_telemetry_location';
const MAX_ACCEPTABLE_ACCURACY_METERS = 50;

type StoredLocationSample = {
  latitude: number;
  longitude: number;
  timestamp: string;
  accuracy: number | null;
};

function toIsoTimestamp(timestamp: number) {
  return new Date(timestamp).toISOString();
}

function isAccurateEnough(accuracy?: number | null) {
  if (accuracy == null) return true;
  return accuracy <= MAX_ACCEPTABLE_ACCURACY_METERS;
}

function resolveAccuracy(
  accuracy1?: number | null,
  accuracy2?: number | null
) {
  if (accuracy1 == null && accuracy2 == null) return null;
  if (accuracy1 == null) return accuracy2 ?? null;
  if (accuracy2 == null) return accuracy1;
  return Math.max(accuracy1, accuracy2);
}

export async function ensureLocationPermissions() {
  const foreground = await Location.requestForegroundPermissionsAsync();
  if (!foreground.granted) return false;

  const background = await Location.requestBackgroundPermissionsAsync();
  return background.granted;
}

async function getSingleLocationSample(): Promise<StoredLocationSample> {
  const location = await Location.getCurrentPositionAsync({
    accuracy: Location.Accuracy.Balanced,
  });

  if (!isAccurateEnough(location.coords.accuracy)) {
    throw new Error('Location accuracy too low');
  }

  return {
    latitude: location.coords.latitude,
    longitude: location.coords.longitude,
    timestamp: toIsoTimestamp(location.timestamp),
    accuracy: location.coords.accuracy ?? null,
  };
}

async function getPendingLocation(): Promise<StoredLocationSample | null> {
  const raw = await AsyncStorage.getItem(PENDING_LOCATION_KEY);
  if (!raw) return null;
  return JSON.parse(raw);
}

async function savePendingLocation(location: StoredLocationSample) {
  await AsyncStorage.setItem(PENDING_LOCATION_KEY, JSON.stringify(location));
}

async function clearPendingLocation() {
  await AsyncStorage.removeItem(PENDING_LOCATION_KEY);
}

export async function collectAndSendTelemetryPair() {
  const currentLocation = await getSingleLocationSample();
  const pendingLocation = await getPendingLocation();

  if (!pendingLocation) {
    await savePendingLocation(currentLocation);
    return { status: 'stored_first_location' };
  }

  const payload = {
    latitude1: pendingLocation.latitude,
    longitude1: pendingLocation.longitude,
    timestamp1: pendingLocation.timestamp,
    latitude2: currentLocation.latitude,
    longitude2: currentLocation.longitude,
    timestamp2: currentLocation.timestamp,
    accuracy: resolveAccuracy(
      pendingLocation.accuracy,
      currentLocation.accuracy
    ),
  };

  const response = await api.post('/location', payload);
  await clearPendingLocation();

  return {
    status: 'sent',
    data: response.data,
  };
}

export async function startBackgroundTracking() {
  const hasPermission = await ensureLocationPermissions();
  if (!hasPermission) {
    throw new Error('Location permissions not granted');
  }

  const alreadyStarted =
    await Location.hasStartedLocationUpdatesAsync(LOCATION_TASK_NAME);

  if (alreadyStarted) return;

  await Location.startLocationUpdatesAsync(LOCATION_TASK_NAME, {
    accuracy: Location.Accuracy.Balanced,
    timeInterval: 30 * 60 * 1000,
    distanceInterval: 0,
    pausesUpdatesAutomatically: true,
    foregroundService: {
      notificationTitle: 'Parkulator koristi lokaciju',
      notificationBody:
        'Aplikacija povremeno prikuplja lokaciju za procjenu zauzetosti parkinga.',
    },
  });
}

export async function stopBackgroundTracking() {
  const started =
    await Location.hasStartedLocationUpdatesAsync(LOCATION_TASK_NAME);

  if (!started) return;

  await Location.stopLocationUpdatesAsync(LOCATION_TASK_NAME);
}