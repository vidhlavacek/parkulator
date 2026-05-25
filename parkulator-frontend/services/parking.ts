import api from "./api";

export type ParkingDTO = {
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

export type ParkingMarker = {
  id: number;
  title: string;
  latitude: number;
  longitude: number;
};

export async function getParkingsByLocationRequest(
  latitude: number,
  longitude: number
): Promise<{ parkings: ParkingDTO[] }> {
  const response = await api.get(
    `/parkings/nearby?latitude=${latitude}&longitude=${longitude}`
  );

  return response.data;
}

export function mapParkingsToMarkers(
  parkings: ParkingDTO[]
): ParkingMarker[] {
  return parkings
    .filter(
      (parking) =>
        parking.latitude != null &&
        parking.longitude != null
    )
    .map((parking) => ({
      id: parking.id,
      title: parking.name,
      latitude: parking.latitude!,
      longitude: parking.longitude!,
    }));
}