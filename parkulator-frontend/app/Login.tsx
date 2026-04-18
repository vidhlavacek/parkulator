import { View, Text, StyleSheet, Pressable, Alert,} from "react-native";
import { useState } from "react";
import { useRouter } from "expo-router";
import Input from "../components/ui/Input";
import Button from "../components/ui/Button";
import { useAuth } from "../context/AuthContext";
import { loginRequest } from "../services/auth";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();
  const { signIn } = useAuth();


  const handleLogin = async () => {
    try {
      const data = await loginRequest({
        email,
        password,
      });

      await signIn(data);
      router.replace("/");
    } catch (error: any) {
      Alert.alert("Login error", error?.message || "Neuspješna prijava");
    }
  };


  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome back!</Text>

      <View style={styles.card}>
        <Input placeholder="Email" value={email} onChangeText={setEmail} />
        <Input placeholder="Password" secure value={password} onChangeText={setPassword} />

        <Button title="Login" onPress={handleLogin} />
      </View>

      <Pressable
        onPress={() => router.navigate("/register")}
        style={({ pressed }) => [styles.linkButton, pressed && styles.linkPressed]}
      >
        <Text style={styles.link}>
          Don't have an account? <Text style={styles.linkBold}>Register</Text>
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
    shadowColor: "#000",
    shadowOpacity: 0.05,
    shadowRadius: 10,
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