import axios from "axios";
import AsyncStorage from "@react-native-async-storage/async-storage";

const PARKULATOR_URL = "http://192.168.178.184:8080";
// Android emulator: 10.0.2.2
// If testing on physical phone, use the IP of your computer, e.g., http://192.168.1.5:8080

const TOKEN_KEY = "auth_token";

const api = axios.create({
  baseURL: PARKULATOR_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(async (config) => {
  const token = await AsyncStorage.getItem(TOKEN_KEY);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export function getApiErrorMessage(error: unknown, fallback = "Something went wrong") {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data;

    if (typeof data === "string") return data;
    if (data?.message) return data.message;
  }

  return fallback;
}

export default api;