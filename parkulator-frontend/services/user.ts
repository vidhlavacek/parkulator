import api from "./api";

export type User = {
  id: number;
  email: string;
  username: string;
};

export async function getCurrentUserRequest(): Promise<User> {
  const response = await api.get<User>("/users/current");
  return response.data;
}

export async function updateUsernameRequest(username: string): Promise<void> {
  await api.put("/users/username", { username });
}

export async function updateEmailRequest(email: string): Promise<void> {
  await api.put("/users/email", { email });
}

export async function updatePasswordRequest(oldPassword: string, newPassword: string): Promise<void> {
  await api.put("/users/password", {
    oldPassword,
    newPassword,
  });
}