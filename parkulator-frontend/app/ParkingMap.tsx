import React, { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import MapView, { Marker } from "react-native-maps";
import { SafeAreaView } from "react-native-safe-area-context";
import { getAllParkingsRequest, Parking } from "../services/parking";

type ParkingWithCoords = Parking & {
  latitude?: number;
  longitude?: number;
};

const PARKING_COORDINATES: Record<string, { latitude: number; longitude: number }> = {
  "Srednja Delta": { latitude: 45.3249, longitude: 14.4434 },
  "KBC Sušak": { latitude: 45.3274, longitude: 14.4668 },
  "KBC Rijeka i Podpinjol": { latitude: 45.3319, longitude: 14.4237 },
  "Parkiralište Krešimirova": { latitude: 45.3332, longitude: 14.4281 },
  "5. zona": { latitude: 45.3235, longitude: 14.4491 },
};

export default function ParkingMap() {
  const [parkings, setParkings] = useState<ParkingWithCoords[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchParkings = async () => {
      try {
        const data = await getAllParkingsRequest();

        const parkingsWithCoordinates = data.map((parking) => {
          const coordinates = PARKING_COORDINATES[parking.name];

          return {
            ...parking,
            latitude: coordinates?.latitude,
            longitude: coordinates?.longitude,
          };
        });

        setParkings(parkingsWithCoordinates);
      } catch (error) {
        console.log("Parking map fetch error:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchParkings();
  }, []);

  const parkingsWithMarkers = parkings.filter(
    (parking) =>
      typeof parking.latitude === "number" &&
      typeof parking.longitude === "number"
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
        initialRegion={{
          latitude: 45.3271,
          longitude: 14.4422,
          latitudeDelta: 0.06,
          longitudeDelta: 0.06,
        }}
      >
        {parkingsWithMarkers.map((parking, index) => (
          <Marker
            key={`${parking.name}-${parking.address}-${index}`}
            coordinate={{
              latitude: parking.latitude!,
              longitude: parking.longitude!,
            }}
            title={parking.name}
            description={parking.address || parking.type}
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