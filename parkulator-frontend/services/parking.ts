import api from "./api";

export type Parking = {
  id: number;
  name: string;
  address?: string;
  price?: number;
  availableSpots?: number;
  latitude?: number;
  longitude?: number;

  sourceKey: string;
  link: string;
  type: string;
  isLive?: boolean;
  totalSpots?: number;
  occupiedSpots?: number;
};

export async function getAllParkingsRequest(): Promise<Parking[]> {
  const response = await api.get<Parking[]>("/parkings/all");
  return response.data;
}

/*export async function getParkingByIdRequest(id: number) {
  const response = await api.get(`/parkings/${id}`);
  return response.data;
}*/