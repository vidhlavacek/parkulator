import React from "react";
import { Pressable, Text, StyleSheet, ViewStyle } from "react-native";
import { LinearGradient } from "expo-linear-gradient";

interface Props {
  title: string;
  onPress: () => void;
  variant?: "primary" | "secondary";
  style?: ViewStyle;
}

export default function Button({ title, onPress, variant = "primary", style }: Props) {
  return (
    <Pressable
      onPress={onPress}
      style={({ pressed }) => [
        styles.shadow,
        pressed && styles.pressed,
        style,
      ]}
    >
      {({ pressed }) => (
        <LinearGradient
          colors={
            variant === "primary"
              ? pressed
                ? ["#247ee8", "#0059c9"]
                : ["#2c8cff", "#0066e8"]
              : pressed
              ? ["#45b92e", "#238916"]
              : ["#58cc3a", "#2fa51f"]
          }
          style={styles.button}
        >
          <Text style={styles.text}>{title}</Text>
        </LinearGradient>
      )}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  shadow: {
    borderRadius: 16,
    shadowColor: "#000",
    shadowOpacity: 0.2,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 6 },
    elevation: 6,
  },
  button: {
    height: 58,
    borderRadius: 16,
    justifyContent: "center",
    alignItems: "center",
  },
  text: {
    color: "#fff",
    fontSize: 18,
    fontWeight: "700",
  },
  pressed: {
    transform: [{ scale: 0.97 }],
  },
});