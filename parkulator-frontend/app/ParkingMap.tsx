import React, { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Animated, PanResponder, Pressable, ScrollView, StyleSheet, Text, View, } from "react-native";
import MapView, { Callout, Marker } from "react-native-maps";
import { SafeAreaView } from "react-native-safe-area-context";
import * as Location from "expo-location";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Stack } from "expo-router";
import { ParkingMarker, mapParkingsToMarkers, ParkingDTO, } from "../services/parking";

const CARD_HEIGHT = 88;
const SHEET_HEIGHT = 350;
const SWIPE_THRESHOLD = 170;

function getDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const dx = lat2 - lat1;
  const dy = lon2 - lon1;
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
  const router = useRouter();

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
        if (status !== "granted") { setLoading(false); return; }

        const loc = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
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

  const handleOpenDetail = (parking: ParkingDTO) => {
    router.push({
      pathname: "/ParkingDetail",
      params: {
        id: parking.id,
        name: parking.name,
        price: parking.price,
        availableSpots: parking.availableSpots,
        totalSpots: parking.totalSpots,
        address: parking.address ?? "",
        latitude: parking.latitude,
        longitude: parking.longitude,
      },
    });
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
      title: "Parking Map",
      headerBackTitle: "Home",}} />

    <SafeAreaView style={styles.container}>
      <MapView
        ref={mapRef}
        style={styles.map}
        initialRegion={
          userLocation
            ? { latitude: userLocation.latitude, longitude: userLocation.longitude, latitudeDelta: 0.06, longitudeDelta: 0.06 }
            : { latitude: 45.3271, longitude: 14.4422, latitudeDelta: 0.06, longitudeDelta: 0.06 }
        }
        showsUserLocation
        showsMyLocationButton
      >
        {markers.map((marker: ParkingMarker, index: number) => {
          const parking = parkings[index];
          const color = index < 3 ? "#2fa51f" : index < 6 ? "#2c8cff" : "#e21b1b";
          const bgColor = index < 3 ? "#edfce8" : index < 6 ? "#e8f1fd" : "#fdeaea";
          const availLabel =
            (parking.availableSpots ?? 0) > 20 ? "Lots of spots" :
            (parking.availableSpots ?? 0) > 5 ? "Few spots" : "Almost full";

          return (
            <Marker
              key={marker.id}
              coordinate={{ latitude: marker.latitude, longitude: marker.longitude }}
              pinColor={index < 3 ? "green" : index < 6 ? "blue" : "red"}
              onPress={() => handleOpenDetail(parking)}
            >
              <Callout tooltip onPress={() => handleOpenDetail(parking)}>
                <View style={styles.callout}>
                  <Text style={styles.calloutName}>{parking.name}</Text>
                  {parking.address && (
                    <Text style={styles.calloutAddress}>{parking.address}</Text>
                  )}
                  <View style={styles.calloutRow}>
                    <Text style={styles.calloutPrice}>${parking.price?.toFixed(2)}/hr</Text>
                    <View style={[styles.calloutBadge, { backgroundColor: bgColor }]}>
                      <Text style={[styles.calloutBadgeText, { color }]}>{availLabel}</Text>
                    </View>
                  </View>
                  <Text style={styles.calloutSpots}>
                    {parking.availableSpots ?? "—"} / {parking.totalSpots ?? "—"} spots free
                  </Text>
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
            const distance =
              userLocation && parking.latitude && parking.longitude
                ? getDistance(userLocation.latitude, userLocation.longitude, parking.latitude, parking.longitude)
                : null;

            const color = index < 3 ? "#2fa51f" : index < 6 ? "#2c8cff" : "#e21b1b";
            const bgColor = index < 3 ? "#edfce8" : index < 6 ? "#e8f1fd" : "#fdeaea";

            const availLabel =
              (parking.availableSpots ?? 0) > 20 ? "Lots of spots" :
              (parking.availableSpots ?? 0) > 5 ? "Few spots" : "Almost full";

            return (
              <Pressable
                key={parking.id}
                style={({ pressed }) => [
                  styles.card,
                  { height: CARD_HEIGHT },
                  pressed && styles.cardPressed,
                ]}
                onPress={() => handleOpenDetail(parking)}
              >
                <View style={[styles.badge, { backgroundColor: bgColor }]}>
                  <Text style={[styles.badgeText, { color }]}>#{index + 1}</Text>
                </View>

                <View style={styles.cardInfo}>
                  <Text style={styles.cardName} numberOfLines={1}>{parking.name}</Text>
                  <View style={styles.cardRow}>
                    <Ionicons name="walk" size={13} color="#8a97aa" />
                    <Text style={styles.cardSub}>{distance !== null ? `${distance} m` : "—"}</Text>
                    <Text style={styles.cardDot}>·</Text>
                    <Ionicons name="car" size={13} color="#8a97aa" />
                    <Text style={styles.cardSub}>{parking.availableSpots ?? "—"}/{parking.totalSpots ?? "—"}</Text>
                  </View>
                </View>

                <View style={styles.cardRight}>
                  <Text style={styles.cardPrice}>
                    ${parking.price?.toFixed(2)}<Text style={styles.cardPriceSub}>/hr</Text>
                  </Text>
                  <View style={[styles.availBadge, { backgroundColor: bgColor }]}>
                    <Text style={[styles.availText, { color }]}>{availLabel}</Text>
                  </View>
                </View>
              </Pressable>
            );
          })}
        </ScrollView>
      </Animated.View>
    </SafeAreaView></>
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
    shadowColor: "#2fa51f",
    shadowOpacity: 0.3,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 6,
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
    shadowColor: "#000",
    shadowOpacity: 0.08,
    shadowRadius: 14,
    shadowOffset: { width: 0, height: -8 },
    elevation: 10,
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
    gap: 12,
    borderRadius: 12,
  },
  cardPressed: { backgroundColor: "#f4f7fb" },
  badge: { width: 46, height: 46, borderRadius: 14, alignItems: "center", justifyContent: "center" },
  badgeText: { fontSize: 15, fontWeight: "700" },
  cardInfo: { flex: 1 },
  cardName: { fontSize: 15, fontWeight: "700", color: "#3a4e6c" },
  cardRow: { flexDirection: "row", alignItems: "center", gap: 4, marginTop: 4 },
  cardSub: { fontSize: 12, color: "#8a97aa" },
  cardDot: { fontSize: 12, color: "#8a97aa" },
  cardRight: { alignItems: "flex-end", gap: 4 },
  cardPrice: { fontSize: 15, fontWeight: "700", color: "#33496b" },
  cardPriceSub: { fontSize: 12, fontWeight: "400", color: "#8a97aa" },
  availBadge: { borderRadius: 8, paddingHorizontal: 8, paddingVertical: 3 },
  availText: { fontSize: 11, fontWeight: "600" },

  callout: {
    backgroundColor: "#ffffff",
    borderRadius: 16,
    padding: 14,
    width: 220,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 6,
  },
  calloutName: { fontSize: 15, fontWeight: "700", color: "#3a4e6c", marginBottom: 2 },
  calloutAddress: { fontSize: 12, color: "#8a97aa", marginBottom: 8 },
  calloutRow: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", marginBottom: 6 },
  calloutPrice: { fontSize: 15, fontWeight: "700", color: "#33496b" },
  calloutBadge: { borderRadius: 8, paddingHorizontal: 8, paddingVertical: 3 },
  calloutBadgeText: { fontSize: 11, fontWeight: "600" },
  calloutSpots: { fontSize: 12, color: "#8a97aa" },
});