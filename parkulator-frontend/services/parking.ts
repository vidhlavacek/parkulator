import api from "./api";

export type ParkingDTO = {
  id: number;
  name: string;
  address: string;
  type: string;
  link: string;
  live: boolean;
  availableSpots: number;
  spots: number;
  parkingStatus: string;
  price: number;
  openingHour: number;
  closingHour: number;
  latitude?: number;
  longitude?: number;
  score?: number;
  occupancyStatus?: string;
};

export type ParkingMarker = {
  id: number;
  title: string;
  latitude: number;
  longitude: number;
};

export async function getParkingsByLocationRequest(params: {
  lat: number;
  lng: number;
  type?: string;
  maxDistance?: number;
}) {
  const response = await api.get("/parkings", {
    params: {
      ...params,
     // maxDistance: 0.5,
    },
  });

  return response.data;
}

/*export async function getParkingsByLocationRequest(params: {
  lat: number;
  lng: number;
  type?: string;
  maxDistance?: number;
  maxPrice?: number;
}) {
  const response = await api.get("/parkings", {
    params,
  });

  return response.data;
}*/

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