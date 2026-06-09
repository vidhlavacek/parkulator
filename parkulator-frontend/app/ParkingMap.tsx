import React, { useEffect, useRef, useState } from "react";
import { ActivityIndicator, Animated, Image, PanResponder, Pressable, ScrollView, StyleSheet, Text, TextInput, View, } from "react-native";
import MapView, { Callout, Marker } from "react-native-maps";
import { SafeAreaView } from "react-native-safe-area-context";
import * as Location from "expo-location";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Stack } from "expo-router";
import { ParkingMarker, mapParkingsToMarkers, ParkingDTO, getParkingsByLocationRequest} from "../services/parking";
import FilterSheet, { FilterValues } from "../components/ui/FilterSheet";

const CARD_HEIGHT = 88;
const SHEET_HEIGHT = 350;
const SWIPE_THRESHOLD = 170;

const RIJEKA = { latitude: 45.3271, longitude: 14.4422 };

type PhotonSuggestion = {
  label: string;
  latitude: number;
  longitude: number;
};

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

  const [searchQuery, setSearchQuery] = useState("");
  const [suggestions, setSuggestions] = useState<PhotonSuggestion[]>([]);
  const [searchFocused, setSearchFocused] = useState(false);
  const [selectedLocation, setSelectedLocation] = useState<PhotonSuggestion | null>(null);

  const scrollRef = useRef<ScrollView>(null);
  const mapRef = useRef<MapView>(null);
  const translateY = useRef(new Animated.Value(0)).current;
  const router = useRouter();
  const searchDebounce = useRef<ReturnType<typeof setTimeout> | null>(null);
  const [filterVisible, setFilterVisible] = useState(false);

  const [filters, setFilters] = useState({
  maxDistance: 5000,
  maxPrice: 5,
  parkingType: "ALL",
  //liveOnly: false,
  });

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

  const occupancyStatusLabels: Record<string, string> = {
  LIKELY_EMPTY: "Empty",
  MODERATELY_OCCUPIED: "Crowded",
  LIKELY_FULL: "Full",
  };

  function getOccupancyLabel(status?: string | null) {
  if (!status) return null;
  return occupancyStatusLabels[status] ?? status;
  }

  const fetchSuggestions = async (query: string) => {
    if (query.trim().length < 3) {
      setSuggestions([]);
      return;
    }
    try {
      const params = new URLSearchParams({
        q: query,
        limit: "15",
        lang: "en",
        lat: String(RIJEKA.latitude),
        lon: String(RIJEKA.longitude),
      });
      const res = await fetch(`https://photon.komoot.io/api/?${params.toString()}`);
      const data = await res.json();

      const mapped: PhotonSuggestion[] = (data.features ?? [])
        .filter((f: any) => f.properties?.countrycode === "HR")
        .filter((f: any) => {
          const p = f.properties ?? {};
          const city = (p.city ?? "").toLowerCase();
          const county = (p.county ?? "").toLowerCase();
          return city.includes("rijeka") || county.includes("rijeka") || county.includes("primorsko");
        })
        .map((f: any) => {
          const p = f.properties ?? {};
          const labelParts = [p.name, p.street, p.city].filter(Boolean);
          return {
            label: labelParts.join(", ") || "Unknown",
            longitude: f.geometry.coordinates[0],
            latitude: f.geometry.coordinates[1],
          };
        })
        .slice(0, 5);

      setSuggestions(mapped);
    } catch (error) {
      console.log("Photon search error:", error);
      setSuggestions([]);
    }
  };

  const onSearchChange = (text: string) => {
    setSearchQuery(text);
    if (searchDebounce.current) clearTimeout(searchDebounce.current);
    searchDebounce.current = setTimeout(() => {
      fetchSuggestions(text);
    }, 350);
  };

  const onSelectSuggestion = (s: PhotonSuggestion) => {
    setSearchQuery(s.label);
    setSuggestions([]);
    setSearchFocused(false);
    setSelectedLocation(s);
    mapRef.current?.animateToRegion(
      {
        latitude: s.latitude,
        longitude: s.longitude,
        latitudeDelta: 0.04,
        longitudeDelta: 0.04,
      },
      500
    );
    loadParkings(s.latitude, s.longitude);
  };

  const applyFilters = async (
  newFilters: FilterValues
) => {
  try {
    const location =
      selectedLocation ?? userLocation;

    if (!location) return;

    const data =
      await getParkingsByLocationRequest({
        lat: location.latitude,
        lng: location.longitude,
        type:
          newFilters.parkingType === "ALL"
            ? null
            : newFilters.parkingType,
        maxDistance:
          newFilters.maxDistance,
        maxPrice:
          newFilters.maxPrice,
      });

    setParkings(data.parkings);
    setMarkers(
      mapParkingsToMarkers(data.parkings)
    );

    setFilters(newFilters);
    setFilterVisible(false);
  } catch (error) {
    console.log(error);
  }
};

  const loadParkings = async (lat: number, lng: number) => {
  try {
    setLoading(true);

    const data = await getParkingsByLocationRequest({
      lat,
      lng,
    });

    setParkings(data.parkings);
    setMarkers(mapParkingsToMarkers(data.parkings));
  } catch (error) {
    console.log("Load parkings error:", error);
  } finally {
    setLoading(false);
  }
};

  useEffect(() => {
    const init = async () => {
      try {
        const { status } = await Location.requestForegroundPermissionsAsync();
        if (status !== "granted") { setLoading(false); return; }

        const loc = await Location.getCurrentPositionAsync({ accuracy: Location.Accuracy.Balanced });
        const { latitude, longitude } = loc.coords;
        setUserLocation({ latitude, longitude });

        const data = await getParkingsByLocationRequest({
        lat: latitude,
        lng: longitude,
      });

        setParkings(data.parkings);
        setMarkers(mapParkingsToMarkers(data.parkings));

        
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
        address: parking.address,
        type: parking.type,
        link: parking.link,
        live: String(parking.live),
        availableSpots: parking.availableSpots,
        spots: parking.spots,
        parkingStatus: parking.parkingStatus,
        price: parking.price,
        openingHour: parking.openingHour,
        closingHour: parking.closingHour,
        latitude: parking.latitude,
        longitude: parking.longitude,
        score: parking.score,
        occupancyStatus: parking.occupancyStatus
      },
    });
  };

  if (loading) {
    return (
      <SafeAreaView style={styles.center}>
        <Image
          source={require("../assets/images/logo.png")}
          style={styles.logo}
          resizeMode="contain"
        />
        <ActivityIndicator size="large" color="#1e40af" style={{ marginTop: 20 }} />
        <Text style={styles.loadingText}>Učitavanje parkinga...</Text>
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
            : { latitude: RIJEKA.latitude, longitude: RIJEKA.longitude, latitudeDelta: 0.06, longitudeDelta: 0.06 }
        }
        showsUserLocation
        showsMyLocationButton
      >
        {markers.map((marker: ParkingMarker, index: number) => {
          const parking = parkings[index];
          const color = index < 3 ? "#2fa51f" : index < 6 ? "#2c8cff" : "#e21b1b";
          const bgColor = index < 3 ? "#edfce8" : index < 6 ? "#e8f1fd" : "#fdeaea";
          const availLabel = getOccupancyLabel(parking.occupancyStatus);

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
                    {parking.live ? parking.availableSpots : "Estimated"}
                 </Text>
                </View>
              </Callout>
            </Marker>
          );
        })}

        {selectedLocation && (
          <Marker
            coordinate={{
              latitude: selectedLocation.latitude,
              longitude: selectedLocation.longitude,
            }}
            pinColor="blue"
            title={selectedLocation.label}
          />
        )}
      </MapView>

      <View style={styles.searchWrapper}>
        <View style={styles.searchWrapper}>
  <View style={styles.searchContainer}>
    <View style={styles.searchBar}>
      <Ionicons name="search" size={18} color="#8a97aa" />

      <TextInput
        style={styles.searchInput}
        placeholder="Search location in Rijeka..."
        placeholderTextColor="#8a97aa"
        value={searchQuery}
        onChangeText={onSearchChange}
        onFocus={() => setSearchFocused(true)}
      />

      {searchQuery.length > 0 && (
        <Pressable
          onPress={() => {
            setSearchQuery("");
            setSuggestions([]);
          }}
        >
          <Ionicons
            name="close-circle"
            size={18}
            color="#8a97aa"
          />
        </Pressable>
      )}
    </View>

    <Pressable
      style={styles.filterButton}
      onPress={() => setFilterVisible(true)}
    >
      <Ionicons
        name="options-outline"
        size={22}
        color="#33496b"
      />
    </Pressable>
  </View>

  {searchFocused && suggestions.length > 0 && (
    <View style={styles.suggestionBox}>
      {suggestions.map((s, i) => (
        <Pressable
          key={`${s.latitude}-${s.longitude}-${i}`}
          style={({ pressed }) => [
            styles.suggestionItem,
            pressed && styles.suggestionItemPressed,
          ]}
          onPress={() => onSelectSuggestion(s)}
        >
          <Ionicons
            name="location-outline"
            size={16}
            color="#2fa51f"
          />
          <Text
            style={styles.suggestionText}
            numberOfLines={1}
          >
            {s.label}
          </Text>
        </Pressable>
      ))}
    </View>
  )}
</View>
  </View>

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

            const availLabel = getOccupancyLabel(parking.occupancyStatus);

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
                    <Text style={styles.cardSub}>{parking.live ? parking.availableSpots : (parking.occupancyStatus ? "Estimated" : "Not available")}</Text>
                  </View>
                </View>

                <View style={styles.cardRight}>
                  <Text style={styles.cardPrice}>
                    ${parking.price?.toFixed(2)}<Text style={styles.cardPriceSub}>/hr</Text>
                  </Text>
                  {!parking.live && getOccupancyLabel(parking.occupancyStatus) && 
                  <View style={[styles.availBadge, { backgroundColor: bgColor }]}>
                    <Text style={[styles.availText, { color }]}>{availLabel}</Text>
                  </View>}
                </View>
              </Pressable>
            );
          })}
        </ScrollView>
      </Animated.View>
      
      <FilterSheet
      visible={filterVisible}
      onClose={() => setFilterVisible(false)}
      onApply={applyFilters}
/>
    </SafeAreaView></>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#dfe3ea" },
  center: { flex: 1, backgroundColor: "#dfe3ea", alignItems: "center", justifyContent: "center" },
  loadingText: { marginTop: 12, color: "#465a79", fontSize: 15 },
  logo: { width: 180, height: 180 },
  map: { ...StyleSheet.absoluteFillObject, },

  searchWrapper: {
    position: "absolute",
    top: 12,
    left: 16,
    right: 16,
    zIndex: 20,
  },
  searchBar: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#ffffff",
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 10,
    gap: 10,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 3 },
    elevation: 5,
  },
  searchInput: {
    flex: 1,
    fontSize: 15,
    color: "#33496b",
    padding: 0,
  },
  suggestionBox: {
    backgroundColor: "#ffffff",
    borderRadius: 14,
    marginTop: 8,
    paddingVertical: 4,
    shadowColor: "#000",
    shadowOpacity: 0.1,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 3 },
    elevation: 5,
    overflow: "hidden",
  },
  suggestionItem: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
  },
  suggestionItemPressed: { backgroundColor: "#f4f7fb" },
  suggestionText: { flex: 1, fontSize: 14, color: "#3a4e6c" },

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

  searchContainer: {
  flexDirection: "row",
  alignItems: "flex-start",
},

  filterButton: {
  marginLeft: 8,
  width: 42,
  height: 42,
  borderRadius: 12,
  backgroundColor: "#ffffff",
  justifyContent: "center",
  alignItems: "center",
  shadowColor: "#000",
  shadowOpacity: 0.1,
  shadowRadius: 8,
  shadowOffset: { width: 0, height: 3 },
  elevation: 5,
},

});