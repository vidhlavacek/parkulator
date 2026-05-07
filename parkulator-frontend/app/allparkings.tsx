import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  FlatList,
  Linking,
  TouchableOpacity,
} from "react-native";
import { Stack } from "expo-router";
import { useColorScheme } from "react-native";
import { getAllParkingsRequest, Parking } from "../services/parking";


const AllParkings = () => {
  const [parkings, setParkings] = useState<Parking[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchParkings = async () => {
  try {
    const data = await getAllParkingsRequest();

    if (Array.isArray(data)) {
      setParkings(data);
    } else {
      setParkings([]);
    }
  } catch (error) {
    console.log("Parking fetch error:", error);
    setParkings([]);
  } finally {
    setLoading(false);
  }
};

  useEffect(() => {
    fetchParkings();
  }, []);

  if (!parkings.length) return <Text>Loading...</Text>;

  return (
    <>
      <Stack.Screen
        options={{
          title: "Find Parking",
          headerBackTitle: "Back",
        }}
      />

      <View
        style={{
          flex: 1,
          padding: 20,
          backgroundColor: "#dfe3ea",
        }}
      >
        <FlatList
          data={parkings}
          keyExtractor={(item, index) => `${item.name}-${index}`}
          renderItem={({ item }) => (
            <View
              style={{
                marginBottom: 12,
                padding: 12,
                borderRadius: 16,

                backgroundColor: "#ffffff",
                borderWidth: 1,
                borderColor: "#ddd",

                shadowColor: "#000",
                shadowOffset: { width: 0, height: 2 },
                shadowOpacity: 0.2,
                shadowRadius: 2,
                elevation: 3,
              }}
            >
              <Text
                style={{
                  fontSize: 16,
                  fontWeight: "bold",
                  color: "#000",
                }}
              >
                {item.name}
              </Text>

              <Text style={{ color: "#555" }}>
                {item.address}
              </Text>

              <Text style={{ color: "#555" }}>
                Type: {item.type}
              </Text>

              <Text style={{ color: "#555" }}>
                Free spots: {item.availableSpots}
              </Text>

              <Text style={{ color: "#555" }}>
                Status: {item.live ? "Online" : "Offline"}
              </Text>

              {item.link ? (
                <TouchableOpacity
                  onPress={() => Linking.openURL(item.link)}
                >
                  <Text
                    style={{
                      color: "blue",
                      marginTop: 5,
                    }}
                  >
                    Open in maps
                  </Text>
                </TouchableOpacity>
              ) : null}
            </View>
          )}
        />
      </View>
    </>
  );
};

export default AllParkings;