import React, { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Animated, PanResponder, Pressable, ScrollView, StyleSheet, Text, View } from "react-native";
import MapView, { Callout, Marker } from "react-native-maps";
import { SafeAreaView } from "react-native-safe-area-context";
import * as Location from "expo-location";
import { Ionicons } from "@expo/vector-icons";
import { Stack } from "expo-router";
import { ParkingMarker, mapParkingsToMarkers, ParkingDTO } from "../services/parking";

const CARD_HEIGHT = 88;
const SHEET_HEIGHT = 350;
const SWIPE_THRESHOLD = 170;

function getDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const dx = lat2 - lat1;
  const dy = lat2 - lon2;
  return Math.round(Math.sqrt(dx * dx + dy * dy) * 111000);
}

export default function ParkingMap() {
  const [markers, setMarkers] = useState<ParkingMarker[]>([]);
  const [parkings, setParkings] = useState<ParkingDTO[]>([]);
  const [userLocation, setUserLocation] = useState<{ latitude: number; longitude: number } | null>(null);
  const [loading, setLoading] = useState(true);
  const [sheetVisible, setSheetVisible] = useState(true);

  const scrollRef = useRef<ScrollView>(null);
  const mapRef = useRef<MapView>(null);
  const translateY = useRef(new Animated.Value(0)).current;

  const panResponder = useRef(
    PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onPanResponderMove: (_, gestureState) => {
        if (gestureState.dy > 0) {
          translateY.setValue(gestureState.dy);
        }
      },
      onPanResponderRelease: (_, gestureState) => {
        if (gestureState.dy > SWIPE_THRESHOLD) {
          Animated.timing(translateY, {
            toValue: SHEET_HEIGHT,
            duration: 300,
            useNativeDriver: true,
          }).start(() => setSheetVisible(false));
        } else {
          Animated.spring(translateY, {
            toValue: 0,
            useNativeDriver: true,
          }).start();
        }
      },
    })
  ).current;

  const showSheet = () => {
    setSheetVisible(true);
    translateY.setValue(SHEET_HEIGHT);
    Animated.spring(translateY, {
      toValue: 0,
      useNativeDriver: true,
    }).start();
  };

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

        const data: ParkingDTO[] = [];

        

        setParkings(data);
        setMarkers(mapParkingsToMarkers(data));
      } catch (error) {
        console.log("Parking map error:", error);
      } finally {
        setLoading(false);
      }
    };

    init();
  }, []);

  const handleMarkerPress = (index: number) => {
    if (!sheetVisible) showSheet();
    scrollRef.current?.scrollTo({ y: index * CARD_HEIGHT, animated: true });
  };

  const handleCardPress = (parking: ParkingDTO) => {
    if (parking.latitude && parking.longitude) {
      mapRef.current?.animateToRegion({
        latitude: parking.latitude - 0.0006,
        longitude: parking.longitude,
        latitudeDelta: 0.005,
        longitudeDelta: 0.005,
      }, 500);
    }
  };

  if (loading) {
    return (
      <SafeAreaView style={styles.center}>
        <ActivityIndicator size="large" color="#58cc3a" />
        <Text style={styles.loadingText}>Loading parking map...</Text>
      </SafeAreaView>
    );
  }

  return (
    <>
      <Stack.Screen options={{
        title: "Find Parking",
        headerBackTitle: "Home",
      }} />

      <SafeAreaView style={styles.container}>
        <MapView
          ref={mapRef}
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
          {markers.map((marker: ParkingMarker, index: number) => {
            const parking = parkings[index];

            return (
              <Marker
                key={marker.id}
                coordinate={{ latitude: marker.latitude, longitude: marker.longitude }}
                pinColor="blue"
                onPress={() => handleMarkerPress(index)}
              >
                <Callout tooltip>
                  <View style={styles.callout}>
                    <Text style={styles.calloutName}>{parking?.name}</Text>
                  </View>
                </Callout>
              </Marker>
            );
          })}
        </MapView>

        {!sheetVisible && (
          <Pressable style={styles.showButton} onPress={showSheet}>
            <Ionicons name="list" size={20} color="#fff" />
            <Text style={styles.showButtonText}>Show Parking</Text>
          </Pressable>
        )}

        <Animated.View style={[styles.bottomSheet, { transform: [{ translateY }] }]}>
          <View {...panResponder.panHandlers} style={styles.handleArea}>
            <View style={styles.handle} />
          </View>

          <Text style={styles.sheetTitle}>
            Nearby <Text style={styles.sheetTitleBold}>Parking</Text>
          </Text>

          <ScrollView ref={scrollRef} showsVerticalScrollIndicator={false}>
            {parkings.map((parking, index) => {
              return (
                <Pressable
                  key={parking.id}
                  style={({ pressed }) => [
                    styles.card,
                    { height: CARD_HEIGHT },
                    pressed && styles.cardPressed,
                  ]}
                  onPress={() => handleCardPress(parking)}
                >
                  <View style={styles.cardInfo}>
                    <Text style={styles.cardName}>{parking.name}</Text>
                  </View>
                </Pressable>
              );
            })}
          </ScrollView>
        </Animated.View>
      </SafeAreaView>
    </>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#dfe3ea" },
  center: { flex: 1, backgroundColor: "#dfe3ea", alignItems: "center", justifyContent: "center" },
  loadingText: { marginTop: 12, color: "#465a79", fontSize: 15 },
  map: { flex: 1 },

  showButton: {
    position: "absolute",
    bottom: 24,
    alignSelf: "center",
    backgroundColor: "#2fa51f",
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 20,
    paddingVertical: 12,
    borderRadius: 24,
    gap: 8,
  },
  showButtonText: { color: "#fff", fontWeight: "700", fontSize: 15 },

  bottomSheet: {
    position: "absolute",
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "#ffffff",
    borderTopLeftRadius: 30,
    borderTopRightRadius: 30,
    maxHeight: SHEET_HEIGHT,
    paddingHorizontal: 20,
    paddingBottom: 20,
  },

  handleArea: { alignItems: "center", paddingVertical: 10 },
  handle: { width: 40, height: 4, backgroundColor: "#d9dee7", borderRadius: 2 },

  sheetTitle: { fontSize: 18, color: "#33496b", fontWeight: "400", marginBottom: 10 },
  sheetTitleBold: { fontWeight: "800" },

  card: {
    flexDirection: "row",
    alignItems: "center",
    borderBottomWidth: 1,
    borderBottomColor: "#edf1f5",
    paddingVertical: 8,
  },
  cardPressed: { backgroundColor: "#f4f7fb" },
  cardInfo: { flex: 1 },
  cardName: { fontSize: 15, fontWeight: "700", color: "#3a4e6c" },

  callout: {
    backgroundColor: "#ffffff",
    borderRadius: 16,
    padding: 14,
    width: 220,
  },
  calloutName: { fontSize: 15, fontWeight: "700", color: "#3a4e6c" },
});