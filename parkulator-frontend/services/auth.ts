const API_URL = 'http://192.168.1.105:8080'; 
// Android emulator: 10.0.2.2
// Ako testiraš na fizičkom mobitelu, ovdje ide IP od tvog računala, npr. http://192.168.1.5:8080

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
  const response = await fetch(`${API_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || 'Login failed');
  }

  return response.json();
}

export async function registerRequest(payload: RegisterPayload): Promise<AuthResponse> {
  const response = await fetch(`${API_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || 'Registration failed');
  }

  return response.json();
}