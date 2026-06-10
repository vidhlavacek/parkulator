import React from "react";
import { ScrollView, StyleSheet, Text, View, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useLocalSearchParams, useRouter } from "expo-router";
import { Ionicons, MaterialCommunityIcons } from "@expo/vector-icons";
import { Stack } from "expo-router";

export default function ParkingDetail() {
   const occupancyStatusLabels: Record<string, string> = {
  LIKELY_EMPTY: "Empty",
  MODERATELY_OCCUPIED: "Crowded",
  LIKELY_FULL: "Full",
  };

  function getOccupancyLabel(status?: string | null) {
  if (!status) return false;
  return occupancyStatusLabels[status] ?? status;
  }

  const router = useRouter();
  const params = useLocalSearchParams();

  const name = params.name as string;
  const address = params.address as string;
  const type = params.type as string;
  const live = params.live === "true" ? true : params.live === "false" ? false : undefined;
  const price = parseFloat(params.price as string);
  const availableSpots = parseInt(params.availableSpots as string);
  const totalSpots = params.spots ? parseInt(params.spots as string) : 0;
  const totalSpotsBool = totalSpots ? true : false;
  const availColor =
    availableSpots > 20 ? "#2fa51f" :
    availableSpots > 5 ? "#2c8cff" : "#e21b1b";

  const availBg =
    availableSpots > 20 ? "#edfce8" :
    availableSpots > 5 ? "#e8f1fd" : "#fdeaea";

  return (
    <>
    <Stack.Screen options={{ 
        title: "Details",
        headerBackTitle: "Parking Map",}} />
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scroll} showsVerticalScrollIndicator={false}>

        <View style={styles.header}>
          <View style={styles.headerTop}>
            <Text style={styles.name}>{type === "Garaže i zatvorena parkirališta" ? name : address}</Text>
            <View style={styles.priceBlock}>
              <Text style={styles.price}>${price?.toFixed(2)}<Text style={styles.priceSub}>/hr</Text></Text>
              {!live && getOccupancyLabel(params.occupancyStatus as string | null) && (
                <View style={[styles.availBadge, { backgroundColor: "#edfce8" }]}>
                  <Text style={[styles.availText, { color: "#2fa51f" }]}>Estimated</Text>
                </View>
              )}
            </View>
          </View>
          {address ? (
            <View style={styles.addressRow}>
              <Ionicons name="location-outline" size={14} color="#8a97aa" />
              <Text style={styles.address}>{address}</Text>
            </View>
          ) : null}
        </View>

        {totalSpotsBool && 
        <View style={styles.spotsCard}>
          <View style={styles.spotsRow}>
            <Ionicons name="car" size={20} color={availColor} />
            <Text style={styles.spotsText}>{availableSpots} / {totalSpots} spots free</Text>
          </View>
          <View style={styles.spotsBar}>
            <View style={[styles.spotsBarFill, {
              width: `${Math.round((availableSpots / totalSpots) * 100)}%` as any,
              backgroundColor: availColor,
            }]} />
          </View>
        </View>}

        {!totalSpotsBool &&
        <View style={styles.spotsCard}>
          <View style={styles.spotsRow}>
            <Ionicons name="car" size={20} color={availColor} />
            <Text style={styles.spotsText}>{getOccupancyLabel(params.occupancyStatus as string | null)}</Text>
          </View>
        </View>}


        <View style={styles.pricingCard}>
          <Text style={styles.sectionTitle}>Pricing</Text>
          <View style={styles.divider} />
          <Text style={styles.pricingRow}>${price?.toFixed(2)} / hour</Text>
        </View>

        {/* 
        <View style={styles.pricingCard}>
          <Text style={styless.sectionTitle}>Statistics</Text>
          <View style={styles.divider} />
          <Text style={styles.pricingRow}>Coming soon...</Text>
        </View>
          */}

      </ScrollView>

      <View style={styles.footer}>
        <Pressable
          style={({ pressed }) => [styles.directionsButton, pressed && styles.directionsPressed]}
          onPress={() => {}}
        >
          <Ionicons name="navigate" size={20} color="#fff" />
          <Text style={styles.directionsText}>Get Directions</Text>
        </Pressable>
      </View>
    </SafeAreaView></>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#dfe3ea" },
  scroll: { padding: 16, gap: 16, paddingBottom: 100 },

  header: {
    backgroundColor: "#ffffff",
    borderRadius: 24,
    padding: 20,
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  headerTop: { flexDirection: "row", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 8 },
  name: { fontSize: 22, fontWeight: "800", color: "#33496b", flex: 1, marginRight: 12 },
  priceBlock: { alignItems: "flex-end", gap: 6 },
  price: { fontSize: 20, fontWeight: "700", color: "#33496b" },
  priceSub: { fontSize: 13, fontWeight: "400", color: "#8a97aa" },
  availBadge: { borderRadius: 10, paddingHorizontal: 10, paddingVertical: 4 },
  availText: { fontSize: 12, fontWeight: "700" },
  addressRow: { flexDirection: "row", alignItems: "center", gap: 4 },
  address: { fontSize: 13, color: "#8a97aa" },

  spotsCard: {
    backgroundColor: "#ffffff",
    borderRadius: 24,
    padding: 20,
    gap: 12,
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  spotsRow: { flexDirection: "row", alignItems: "center", gap: 8 },
  spotsText: { fontSize: 15, fontWeight: "600", color: "#33496b" },
  spotsBar: { height: 8, backgroundColor: "#edf1f5", borderRadius: 4, overflow: "hidden" },
  spotsBarFill: { height: 8, borderRadius: 4 },

  facilitiesCard: {
    backgroundColor: "#ffffff",
    borderRadius: 24,
    padding: 20,
    flexDirection: "row",
    justifyContent: "space-around",
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  facilityItem: { alignItems: "center", gap: 6 },
  facilityText: { fontSize: 11, color: "#465a79", fontWeight: "500" },

  pricingCard: {
    backgroundColor: "#ffffff",
    borderRadius: 24,
    padding: 20,
    gap: 10,
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  sectionTitle: { fontSize: 16, fontWeight: "800", color: "#33496b" },
  divider: { height: 1, backgroundColor: "#edf1f5" },
  pricingRow: { fontSize: 14, color: "#465a79", fontWeight: "500" },
  pricingNote: { fontSize: 12, color: "#8a97aa", fontWeight: "400" },

  facilitiesListCard: {
    backgroundColor: "#ffffff",
    borderRadius: 24,
    padding: 20,
    gap: 12,
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  facilityListItem: { flexDirection: "row", alignItems: "center", gap: 10 },
  facilityListText: { fontSize: 14, color: "#465a79", fontWeight: "500" },

  footer: {
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    padding: 16,
    backgroundColor: "#ffffff",
    borderTopWidth: 1,
    borderTopColor: "#edf1f5",
  },
  directionsButton: {
    backgroundColor: "#33496b",
    borderRadius: 24,
    height: 56,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 10,
  },
  directionsPressed: { backgroundColor: "#2a3d5c" },
  directionsText: { color: "#fff", fontSize: 17, fontWeight: "700" },
});