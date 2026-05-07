import api from "./api";


type LoginPayload = {
  email: string;
  password: string;
};

type RegisterPayload = {
  email: string;
  username: string;
  password: string;
};

export type AuthResponse = {
  id: number;
  email: string;
  username: string;
  token: string;
};

export async function loginRequest(payload: LoginPayload): Promise<AuthResponse> {
  const response = await api.post<AuthResponse>("/auth/login", payload);
  return response.data;
}

export async function registerRequest(payload: RegisterPayload): Promise<AuthResponse> {
  const response = await api.post<AuthResponse>("/auth/register", payload);
  return response.data;
}