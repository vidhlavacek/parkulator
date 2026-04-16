import { View, Text, StyleSheet, Pressable } from "react-native";
import { useState } from "react";
import { useRouter } from "expo-router";
import Input from "../components/ui/Input";
import Button from "../components/ui/Button";

export default function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const router = useRouter();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Create account</Text>

      <View style={styles.card}>
        <Input placeholder="Name" value={name} onChangeText={setName} />
        <Input placeholder="Email" value={email} onChangeText={setEmail} />
        <Input placeholder="Password" secure value={password} onChangeText={setPassword} />

        <Button title="Register" onPress={() => {}} />
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