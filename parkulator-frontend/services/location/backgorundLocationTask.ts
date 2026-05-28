// src/services/locationTask.ts
import * as TaskManager from 'expo-task-manager';
import { collectAndSendTelemetryPair } from './backgroundLocationService';

export const LOCATION_TASK_NAME = 'parkulator-location-task';

TaskManager.defineTask(LOCATION_TASK_NAME, async ({ error }) => {
  if (error) {
    console.log('Location task error:', error);
    return;
  }

  try {
    await collectAndSendTelemetryPair();
  } catch (e) {
    console.log('Telemetry pair failed:', e);
  }
});