import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert } from "react-native";
//import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";
import {
  updateUsernameRequest,
  updateEmailRequest,
  updatePasswordRequest,
} from "../services/user";

import { getApiErrorMessage } from "../services/api";

export default function EditProfile() {
  const router = useRouter();
  const { updateUser, signOut } = useAuth();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [loading, setLoading] = useState(false);


  const updateUsername = async () => {
    if (!username.trim()) {
      Alert.alert("Error", "Please enter a new username");
      return;
    }

    setLoading(true);

    try {
      await updateUsernameRequest(username.trim());

      await updateUser({ username: username.trim() });

      Alert.alert("Success", "Username updated");
      setUsername("");
      router.back();
    } catch (e) {
      Alert.alert("Error", getApiErrorMessage(e, "Failed to update username"));
    } finally {
      setLoading(false);
    }
  };

  const updateEmail = async () => {
    if (!email.trim() || !email.includes("@")) {
      Alert.alert("Error", "Please enter a valid email");
      return;
    }

    setLoading(true);

    try {
      await updateEmailRequest(email.trim());

      Alert.alert(
        "Email updated",
        "Please log in again with your new email.",
        [
          {
            text: "OK",
            onPress: async () => {
              await signOut();
              router.replace("/login");
            },
          },
        ]
      );
    } catch (e) {
      Alert.alert("Error", getApiErrorMessage(e, "Failed to update email"));
    } finally {
      setLoading(false);
    }
  };

  const updatePassword = async () => {
    if (!oldPassword || !newPassword) {
      Alert.alert("Error", "Please fill in both password fields");
      return;
    }

    if (newPassword.length < 6) {
      Alert.alert("Error", "New password must be at least 6 characters");
      return;
    }

    if (oldPassword === newPassword) {
      Alert.alert("Error", "New password must be different from the old one");
      return;
    }

    setLoading(true);

    try {
      await updatePasswordRequest(oldPassword, newPassword);

      Alert.alert("Success", "Password changed");
      setOldPassword("");
      setNewPassword("");
      router.back();
    } catch (e) {
      Alert.alert("Error", getApiErrorMessage(e, "Incorrect old password"));
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Edit Profile</Text>

      <TextInput
        placeholder="New username"
        value={username}
        onChangeText={setUsername}
        style={styles.input}
        autoCapitalize="none"
        editable={!loading}
      />
      <TouchableOpacity
        style={[styles.button, loading && styles.buttonDisabled]}
        onPress={updateUsername}
        disabled={loading}
      >
        <Text style={styles.buttonText}>Change username</Text>
      </TouchableOpacity>

      <TextInput
        placeholder="New email"
        value={email}
        onChangeText={setEmail}
        style={styles.input}
        autoCapitalize="none"
        keyboardType="email-address"
        editable={!loading}
      />
      <TouchableOpacity
        style={[styles.button, loading && styles.buttonDisabled]}
        onPress={updateEmail}
        disabled={loading}
      >
        <Text style={styles.buttonText}>Change email</Text>
      </TouchableOpacity>

      <TextInput
        placeholder="Old password"
        secureTextEntry
        value={oldPassword}
        onChangeText={setOldPassword}
        style={styles.input}
        editable={!loading}
      />
      <TextInput
        placeholder="New password"
        secureTextEntry
        value={newPassword}
        onChangeText={setNewPassword}
        style={styles.input}
        editable={!loading}
      />
      <TouchableOpacity
        style={[styles.button, loading && styles.buttonDisabled]}
        onPress={updatePassword}
        disabled={loading}
      >
        <Text style={styles.buttonText}>Change password</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, paddingTop: 50, backgroundColor: "#f5f6fa" },
  title: { fontSize: 22, fontWeight: "bold", marginBottom: 20 },
  input: { backgroundColor: "white", padding: 12, borderRadius: 10, marginBottom: 10 },
  button: {
    backgroundColor: "#4b7bec",
    padding: 12,
    borderRadius: 10,
    marginBottom: 15,
    alignItems: "center",
  },
  buttonDisabled: { opacity: 0.5 },
  buttonText: { color: "white", fontWeight: "bold" },
});