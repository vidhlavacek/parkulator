import AsyncStorage from "@react-native-async-storage/async-storage";

const API_URL = 'http://192.168.1.4:8080'; 
// Android emulator: 10.0.2.2
// Ako testiraš na fizičkom mobitelu, ovdje ide IP od tvog računala, npr. http://192.168.1.5:8080

const TOKEN_KEY = "auth_token";

export async function authFetch(path: string, options: RequestInit = {}) {
  const token = await AsyncStorage.getItem(TOKEN_KEY);

  const headers = {
    "Content-Type": "application/json",
    ...(options.headers || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers,
  });

  return response;
}