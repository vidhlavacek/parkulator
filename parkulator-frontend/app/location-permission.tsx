// app/location-permission.tsx
import React, { useState } from 'react';
import { View, Text, StyleSheet, Pressable, Alert, ActivityIndicator } from 'react-native';
import { router } from 'expo-router';
import * as Location from 'expo-location';

import { setLocationOnboardingCompleted } from '../services/location/locationOnboarding';
import { startBackgroundTracking } from '../services/location/backgroundLocationService';

export default function LocationPermissionScreen() {
  const [loading, setLoading] = useState(false);

  const handleEnableLocation = async () => {
    try {
      setLoading(true);

      const foreground = await Location.requestForegroundPermissionsAsync();

      if (!foreground.granted) {
        Alert.alert(
          'Lokacija nije dopuštena',
          'Bez dozvole za lokaciju aplikacija neće moći koristiti procjenu popunjenosti parkinga.'
        );
        return;
      }

      const background = await Location.requestBackgroundPermissionsAsync();

      if (!background.granted) {
        Alert.alert(
          'Pozadinska lokacija nije dopuštena',
          'Aplikacija će raditi, ali praćenje u pozadini neće biti dostupno.'
        );
      } else {
        await startBackgroundTracking();
      }

      await setLocationOnboardingCompleted();
      router.replace('/(tabs)');
    } catch (error) {
      Alert.alert('Greška', 'Dogodila se greška tijekom postavljanja lokacije.');
    } finally {
      setLoading(false);
    }
  };

  const handleSkip = async () => {
    await setLocationOnboardingCompleted();
    router.replace('/(tabs)');
  };

  return (
    <View style={styles.container}>
      <View style={styles.card}>
        <Text style={styles.title}>Dopusti pristup lokaciji</Text>

        <Text style={styles.text}>
          Parkulator koristi lokaciju za procjenu zauzetosti parkinga i za bolji odabir parkirališta
          u blizini vašeg odredišta.
        </Text>

        <Text style={styles.subtext}>
          Lokacija se koristi samo za funkcionalnosti povezane s parkingom. Dozvolu kasnije možete
          promijeniti u postavkama uređaja.
        </Text>

        <Pressable
          style={[styles.button, loading && styles.buttonDisabled]}
          onPress={handleEnableLocation}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="#ffffff" />
          ) : (
            <Text style={styles.buttonText}>Dopusti lokaciju</Text>
          )}
        </Pressable>

        <Pressable style={styles.secondaryButton} onPress={handleSkip} disabled={loading}>
          <Text style={styles.secondaryButtonText}>Preskoči za sada</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 24,
    backgroundColor: '#f5f7fb',
  },
  card: {
    backgroundColor: '#ffffff',
    borderRadius: 16,
    padding: 24,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 3,
  },
  title: {
    fontSize: 24,
    fontWeight: '700',
    marginBottom: 16,
    color: '#111827',
  },
  text: {
    fontSize: 16,
    lineHeight: 24,
    color: '#374151',
    marginBottom: 12,
  },
  subtext: {
    fontSize: 14,
    lineHeight: 22,
    color: '#6b7280',
    marginBottom: 24,
  },
  button: {
    backgroundColor: '#0f766e',
    paddingVertical: 14,
    borderRadius: 12,
    alignItems: 'center',
    marginBottom: 12,
  },
  buttonDisabled: {
    opacity: 0.7,
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
    fontWeight: '600',
  },
  secondaryButton: {
    paddingVertical: 12,
    alignItems: 'center',
  },
  secondaryButtonText: {
    color: '#374151',
    fontSize: 15,
    fontWeight: '500',
  },
});