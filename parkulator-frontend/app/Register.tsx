import { View, Text, StyleSheet, Pressable, Alert } from "react-native";
import { useState } from "react";
import { useRouter } from "expo-router";
import Input from "../components/ui/Input";
import Button from "../components/ui/Button";
import { useAuth } from "../context/AuthContext";
import { registerRequest } from "../services/auth";
import { getApiErrorMessage } from "../services/api";

export default function Register() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  const { signIn } = useAuth();

  const handleRegister = async () => {
  setLoading(true);

  try {
    const data = await registerRequest({
      email: email.trim(),
      username: username.trim(),
      password,
    });

    await signIn(data);
    router.replace("/(tabs)");
  } catch (e) {
    Alert.alert("Registration failed", getApiErrorMessage(e, "Registration failed"));
  } finally {
    setLoading(false);
  }
};
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Create account</Text>

      <View style={styles.card}>
        <Input placeholder="Username" value={username} onChangeText={setUsername} />
        <Input placeholder="Email" value={email} onChangeText={setEmail} />
        <Input placeholder="Password" secure value={password} onChangeText={setPassword} />

        <Button title="Register" onPress={handleRegister} />
      </View>

      <Pressable
        onPress={() => router.navigate("/login")}
        style={({ pressed }) => [styles.linkButton, pressed && styles.linkPressed]}
      >
        <Text style={styles.link}>
          Already have an account? <Text style={styles.linkBold}>Log In</Text>
        </Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    padding: 20,
    backgroundColor: "#f7f7f7",
  },
  title: {
    textAlign: 'center',
    fontSize: 28,
    fontWeight: "600",
    marginBottom: 20,
  },
  card: {
    backgroundColor: "white",
    padding: 20,
    borderRadius: 16,
    elevation: 3,
  },
  link: {
    marginTop: 20,
    textAlign: "center",
    color: "#007AFF",
  },
  linkButton: {
    marginTop: 20,
    alignSelf: "center",
    paddingHorizontal: 8,
    paddingVertical: 6,
    borderRadius: 8,
  },
  linkPressed: {
    backgroundColor: "#e9edf3",
    transform: [{ scale: 0.98 }],
  },
  linkBold: {
    fontWeight: "700",
  },
});