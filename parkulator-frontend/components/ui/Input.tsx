import { TextInput, StyleSheet } from "react-native";

export default function Input({ placeholder, secure, value, onChangeText }: any) {
  return (
    <TextInput
      placeholder={placeholder}
      secureTextEntry={secure}
      value={value}
      onChangeText={onChangeText}
      style={styles.input}
      placeholderTextColor="#999"
    />
  );
}

const styles = StyleSheet.create({
  input: {
    backgroundColor: "#f2f2f2",
    padding: 15,
    borderRadius: 12,
    marginBottom: 15,
    fontSize: 16,
  },
});