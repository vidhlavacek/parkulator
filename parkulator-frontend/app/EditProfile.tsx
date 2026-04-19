import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Alert } from "react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { useRouter } from "expo-router";
import { useAuth } from "../context/AuthContext";

const API_URL = "http://192.168.1.4:8080";

export default function EditProfile() {
  const router = useRouter();
  const { signOut } = useAuth();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const getToken = async () => await AsyncStorage.getItem("auth_token");

  const updateUsername = async () => {
    if (!username.trim()) {
      Alert.alert("Greška", "Unesi novi username");
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
        Alert.alert(
          "Uspjeh",
          "Username je ažuriran. Prijavi se ponovno da vidiš promjene.",
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
        Alert.alert("Greška", msg || "Neuspješno ažuriranje");
      }
    } catch (e) {
      Alert.alert("Greška", "Problem s vezom na server");
    } finally {
      setLoading(false);
    }
  };

  const updateEmail = async () => {
    if (!email.trim() || !email.includes("@")) {
      Alert.alert("Greška", "Unesi ispravan email");
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
        Alert.alert(
          "Email ažuriran",
          "Molimo prijavi se ponovno s novim emailom.",
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
        Alert.alert("Greška", msg || "Neuspješno ažuriranje");
      }
    } catch (e) {
      Alert.alert("Greška", "Problem s vezom na server");
    } finally {
      setLoading(false);
    }
  };

  const updatePassword = async () => {
    if (!oldPassword || !newPassword) {
      Alert.alert("Greška", "Popuni obje lozinke");
      return;
    }
    if (newPassword.length < 6) {
      Alert.alert("Greška", "Nova lozinka mora imati najmanje 6 znakova");
      return;
    }
    if (oldPassword === newPassword) {
      Alert.alert("Greška", "Nova lozinka mora biti različita od stare");
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
        Alert.alert("Uspjeh", "Lozinka je promijenjena");
        setOldPassword("");
        setNewPassword("");
        router.back();
      } else {
        const msg = await res.text();
        Alert.alert("Greška", msg || "Pogrešna stara lozinka");
      }
    } catch (e) {
      Alert.alert("Greška", "Problem s vezom na server");
    } finally {
      setLoading(false);
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Uredi profil</Text>

      <TextInput
        placeholder="Novi username"
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
        <Text style={styles.buttonText}>Promijeni username</Text>
      </TouchableOpacity>

      <TextInput
        placeholder="Novi email"
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
        <Text style={styles.buttonText}>Promijeni email</Text>
      </TouchableOpacity>

      <TextInput
        placeholder="Stara lozinka"
        secureTextEntry
        value={oldPassword}
        onChangeText={setOldPassword}
        style={styles.input}
        editable={!loading}
      />
      <TextInput
        placeholder="Nova lozinka"
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
        <Text style={styles.buttonText}>Promijeni lozinku</Text>
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