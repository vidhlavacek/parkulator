import { authFetch } from "./api";

export async function getCurrentUserRequest() {
  const response = await authFetch("/users/current", {
    method: "GET",
  });

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    throw new Error(data?.message || "Failed to load current user");
  }

  return data;
}