import { useRouter } from "expo-router";
import { useState } from "react";
import { Alert, Pressable, StyleSheet, Text, View, } from "react-native";
import Button from "../components/ui/Button";
import Input from "../components/ui/Input";
import { useAuth } from "../context/AuthContext";
import { loginRequest } from "../services/auth";
import { getApiErrorMessage } from "../services/api";


export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();
  const { signIn } = useAuth();


  const handleLogin = async () => {
  setLoading(true);

  try {
    const data = await loginRequest({
      email: email.trim(),
      password,
    });

    await signIn(data);
    router.replace("/(tabs)");
  } catch (e) {
    Alert.alert("Login failed", getApiErrorMessage(e, "Invalid email or password"));
  } finally {
    setLoading(false);
  }
};


  return (
    <View style={styles.container}>

      <Pressable
          onPress={() => router.push('/')}
          style={{
            padding: 5,
            margin: 5,
            borderRadius: 30,
            position: "absolute",
            top: 45,
            left: 20,
            zIndex: 10,
          }}
        >
          <Text style={{ fontSize: 16 }}>Back</Text>
        </Pressable>
      

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