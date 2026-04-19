import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";

const API_URL = "http://192.168.1.4:8080";

export default function EditProfile() {
  const router = useRouter();
  const { updateUser, signOut } = useAuth();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const getToken = async () => await AsyncStorage.getItem("auth_token");

  const updateUsername = async () => {
    if (!username.trim()) {
      Alert.alert("Error", "Please enter a new username");
      return;
    }

    setLoading(true);
    try {
      const token = await getToken();
      const res = await fetch(`${API_URL}/users/username`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ username: username.trim() }),
      });

      if (res.ok) {
        await updateUser({ username: username.trim() });
        Alert.alert("Success", "Username updated");
        setUsername("");
        router.back();
      } else {
        const msg = await res.text();
        Alert.alert("Error", msg || "Failed to update username");
      }
    } catch (e) {
      Alert.alert("Error", "Could not connect to server");
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
      const token = await getToken();
      const res = await fetch(`${API_URL}/users/email`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ email: email.trim() }),
      });

      if (res.ok) {
        // IMPORTANT: JWT token still contains the old email in the 'sub' claim.
        // Until the backend returns a new token, the safest option is to log the user out.
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
      } else {
        const msg = await res.text();
        Alert.alert("Error", msg || "Failed to update email");
      }
    } catch (e) {
      Alert.alert("Error", "Could not connect to server");
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
      const token = await getToken();
      const res = await fetch(`${API_URL}/users/password`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ oldPassword, newPassword }),
      });

      if (res.ok) {
        Alert.alert("Success", "Password changed");
        setOldPassword("");
        setNewPassword("");
        router.back();
      } else {
        const msg = await res.text();
        Alert.alert("Error", msg || "Incorrect old password");
      }
    } catch (e) {
      Alert.alert("Error", "Could not connect to server");
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