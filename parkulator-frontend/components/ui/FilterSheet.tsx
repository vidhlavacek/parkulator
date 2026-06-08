import React, { useState } from "react";
import { Modal, View, Text, Pressable, StyleSheet, } from "react-native";
import Slider from "@react-native-community/slider";
import { Ionicons } from "@expo/vector-icons";

export type FilterValues = {
  maxDistance: number;
  maxPrice: number;
  parkingType: string;
};

type Props = {
  visible: boolean;
  onClose: () => void;
  onApply: (filters: FilterValues) => void;
};

const PARKING_TYPES = [
  {
    label: "All",
    value: "ALL",
  },
  {
    label: "Open parking lots",
    value: "Otvorena parkirališta",
  },
  {
    label: "Garages & enclosed parking",
    value: "Garaže i zatvorena parkirališta",
  },
];

export default function FilterSheet({
  visible,
  onClose,
  onApply,
}: Props) {
  const [maxDistance, setMaxDistance] = useState(0.5);
  const [maxPrice, setMaxPrice] = useState(5);
  const [parkingType, setParkingType] = useState("ALL");

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
    >
      <View style={styles.overlay}>
        

        <View style={styles.sheet}>
         

          <View style={styles.header}>
            <Text style={styles.title}>Filters</Text>

            <Pressable onPress={onClose}>
              <Ionicons
                name="close"
                size={24}
                color="#33496b"
              />
            </Pressable>
          </View>

          <Text style={styles.label}>
            Distance ({maxDistance.toFixed(1)} km)
            </Text>

          <Slider
            minimumValue={0.5}
            maximumValue={20}
            step={0.1}
            value={maxDistance}
            onValueChange={setMaxDistance}
            minimumTrackTintColor="#2fa51f"
            />

          <Text style={styles.label}>
            Max Price (€{maxPrice.toFixed(1)}/h)
          </Text>

          <Slider
            minimumValue={0}
            maximumValue={10}
            step={0.1}
            value={maxPrice}
            onValueChange={setMaxPrice}
            minimumTrackTintColor="#2fa51f"
            />

          <Text style={styles.label}>
            Parking Type
          </Text>

          <View style={styles.chips}>

        {PARKING_TYPES.map((type) => {
        const selected =
                parkingType === type.value;

        return (
        <Pressable
            key={type.value}
            onPress={() =>
                    setParkingType(type.value)
            }
            style={[
            styles.chip,
            selected && styles.chipSelected,
                 ]}
                    >
        <Text
            style={[
            styles.chipText,
            selected &&
            styles.chipTextSelected,
            ]}
            >
            {type.label}
            </Text>
            </Pressable>
            );
            })}
          </View>

          <View style={styles.footer}>
            <Pressable
              style={styles.resetButton}
              onPress={() => {
                setMaxDistance(5);
                setMaxPrice(5);
                setParkingType("ALL");
              }}
            >
              <Text style={styles.resetText}>
                Reset
              </Text>
            </Pressable>

            <Pressable
              style={styles.applyButton}
              onPress={() =>
                onApply({
                  maxDistance,
                  maxPrice,
                  parkingType,
                })
              }
            >
              <Text style={styles.applyText}>
                Apply
              </Text>
            </Pressable>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    justifyContent: "flex-end",
    backgroundColor: "rgba(0,0,0,0.15)",
  },

  sheet: {
    backgroundColor: "#fff",
    borderTopLeftRadius: 28,
    borderTopRightRadius: 28,
    padding: 20,
  },

  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 20,
  },

  title: {
    fontSize: 20,
    fontWeight: "700",
    color: "#33496b",
  },

  label: {
    marginTop: 10,
    marginBottom: 8,
    fontSize: 15,
    fontWeight: "600",
    color: "#33496b",
  },

  chips: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 10,
    marginTop: 10,
  },

  chip: {
    paddingHorizontal: 14,
    paddingVertical: 10,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#dce3ec",
  },

  chipSelected: {
    backgroundColor: "#2fa51f",
    borderColor: "#2fa51f",
  },

  chipText: {
    color: "#33496b",
  },

  chipTextSelected: {
    color: "#fff",
    fontWeight: "700",
  },

  footer: {
    flexDirection: "row",
    marginTop: 24,
    gap: 12,
  },

  resetButton: {
    flex: 1,
    height: 50,
    borderRadius: 14,
    justifyContent: "center",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#dce3ec",
  },

  applyButton: {
    flex: 2,
    height: 50,
    borderRadius: 14,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#2fa51f",
  },

  resetText: {
    fontWeight: "600",
  },

  applyText: {
    color: "#fff",
    fontWeight: "700",
  },
});