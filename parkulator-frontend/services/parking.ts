import api from "./api";

export type ParkingDTO = {
  name: string;
  address: string;
  type: string;
  link: string;
  isLive: boolean;
  availableSpots: number;
  price: number;
  openingHour: number;
  closingHour: number;
  latitude: number;
  longitude: number;
};

export type ParkingSearchResponse = {
  radiusExpanded: boolean;
  finalRadius: number;
  parkings: ParkingDTO[];
};

export async function getParkingsByLocationRequest(
  lat: number,
  lng: number
): Promise<ParkingSearchResponse> {
  const response = await api.get<ParkingSearchResponse>(`/parkings?lat=${lat}&lng=${lng}`);
  return response.data;
}