import React, { useEffect, useState } from "react";
import { View, Text, FlatList, ActivityIndicator, Linking, TouchableOpacity } from "react-native";
import { blue, green } from "react-native-reanimated/lib/typescript/Colors";
import { Stack } from "expo-router";

interface Parking {
  id: number;
  sourceKey: string;
  name: string;
  address: string;
  link: string;
  type: string;
  isLive: boolean;
  spots: number;
  availableSpots: number;
  parkingPrices: any[];
}

const AllParkings = () => {
  const [parkings, setParkings] = useState<Parking[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchParkings = async () => {
    console.log("FETCHING PARKINGS...");
  
    try {
      const response = await fetch("http://10.0.2.2:8080/parkings/all");
  
      console.log("STATUS:", response.status);
  
      const text = await response.text();
      console.log("RAW RESPONSE:", text);
  
      let data = [];
  
      if (text) {
        try {
          data = JSON.parse(text);
        } catch (jsonError) {
          console.log("JSON PARSE ERROR:", jsonError);
          data = [];
        }
      }
  
      if (Array.isArray(data)) {
        setParkings(data);
      } else {
        console.log("DATA IS NOT ARRAY:", data);
        setParkings([]);
      }
  
    } catch (error) {
      console.log("FETCH ERROR:", error);
      setParkings([]);
    } finally {
      setLoading(false);
    }
  };

  

  useEffect(() => {
    fetchParkings();
  }, []);

  //if (loading) return <Text style={{color: "white"}}>Loading...</Text>;

  if (!parkings.length) return <Text>Parkng je prazan...</Text>;

  return (
    <>
    <Stack.Screen options={{ 
        title: "Find Parking",
        headerBackTitle: "Back",}} />

    <View style={{ padding: 20,
                 }}>
      <FlatList
        data={parkings}
        keyExtractor={(item, index) => `${item.name}-${index}`}
        renderItem={({ item }) => (
          <View style={{ marginBottom: 12, padding: 12, borderWidth: 1, borderRadius: 8 }}>
            
            <Text style={{ fontSize: 16, fontWeight: "bold" }}>
              {item.name}
            </Text>

            <Text>{item.address}</Text>
            <Text>Type: {item.type}</Text>

            <Text>
              Free spots: {item.availableSpots} 
            </Text>

            <Text>
              Status: {item.isLive ? "Online" : "Offline"}
            </Text>

            
            {item.link ? (
              <TouchableOpacity
                onPress={() => Linking.openURL(item.link)}
              >
                <Text style={{ color: "blue", marginTop: 5 }}>
                  Open in maps
                </Text>
              </TouchableOpacity>
            ) : null}

          </View>
        )}
      />
    </View></>
  );
};

export default AllParkings;