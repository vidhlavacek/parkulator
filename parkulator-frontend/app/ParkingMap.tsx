import React, { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import MapView, { Marker } from "react-native-maps";
import { SafeAreaView } from "react-native-safe-area-context";
import * as Location from "expo-location";
import { getParkingsByLocationRequest, ParkingDTO } from "../services/parking";

export default function ParkingMap() {
  const [parkings, setParkings] = useState<ParkingDTO[]>([]);
  const [userLocation, setUserLocation] = useState<{
    latitude: number;
    longitude: number;
  } | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const init = async () => {
      try {
        const { status } = await Location.requestForegroundPermissionsAsync();

        if (status !== "granted") {
          setLoading(false);
          return;
        }

        const loc = await Location.getCurrentPositionAsync({
          accuracy: Location.Accuracy.Balanced,
        });

        const { latitude, longitude } = loc.coords;
        setUserLocation({ latitude, longitude });

        const result = await getParkingsByLocationRequest(latitude, longitude);
        setParkings(result.parkings);
      } catch (error) {
        console.log("Parking map error:", error);
      } finally {
        setLoading(false);
      }
    };

    init();
  }, []);

  const parkingsWithMarkers = parkings.filter(
    (p) => typeof p.latitude === "number" && typeof p.longitude === "number"
  );

  if (loading) {
    return (
      <SafeAreaView style={styles.center}>
        <ActivityIndicator size="large" />
        <Text style={styles.loadingText}>Loading parking map...</Text>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <MapView
        style={styles.map}
        initialRegion={
          userLocation
            ? {
                latitude: userLocation.latitude,
                longitude: userLocation.longitude,
                latitudeDelta: 0.06,
                longitudeDelta: 0.06,
              }
            : {
                latitude: 45.3271,
                longitude: 14.4422,
                latitudeDelta: 0.06,
                longitudeDelta: 0.06,
              }
        }
        showsUserLocation
        showsMyLocationButton
      >
        {parkingsWithMarkers.map((parking, index) => (
          <Marker
            key={`${parking.name}-${index}`}
            coordinate={{
              latitude: parking.latitude,
              longitude: parking.longitude,
            }}
            title={parking.name}
            description={parking.address}
          />
        ))}
      </MapView>

      {parkingsWithMarkers.length === 0 && (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyTitle}>No parking locations available</Text>
          <Text style={styles.emptyText}>
            Parking data was loaded, but no coordinates are available for markers.
          </Text>
        </View>
      )}
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#dfe3ea",
  },
  center: {
    flex: 1,
    backgroundColor: "#dfe3ea",
    alignItems: "center",
    justifyContent: "center",
  },
  loadingText: {
    marginTop: 12,
    color: "#465a79",
  },
  map: {
    flex: 1,
  },
  emptyCard: {
    position: "absolute",
    left: 16,
    right: 16,
    bottom: 24,
    backgroundColor: "#ffffff",
    borderRadius: 16,
    padding: 16,
    shadowColor: "#000",
    shadowOpacity: 0.08,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  emptyTitle: {
    fontSize: 16,
    fontWeight: "700",
    color: "#33496b",
    marginBottom: 4,
  },
  emptyText: {
    fontSize: 14,
    color: "#72819a",
  },
});