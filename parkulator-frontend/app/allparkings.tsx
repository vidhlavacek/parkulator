import React, { useEffect, useState } from "react";
import { View, Text, FlatList, ActivityIndicator, Linking, TouchableOpacity } from "react-native";
import { blue, green } from "react-native-reanimated/lib/typescript/Colors";
import { Stack } from "expo-router";
import { useColorScheme } from "react-native";
import { getAllParkingsRequest, Parking } from "../services/parking";


const AllParkings = () => {
  const [parkings, setParkings] = useState<Parking[]>([]);
  const [loading, setLoading] = useState(true);

  const colorScheme = useColorScheme();
  const isDark = colorScheme === "dark";  

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

  //if (loading) return <Text style={{color: "white"}}>Loading...</Text>;

  if (!parkings.length) return <Text>Parking je prazan...</Text>;

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
    <View
      style={{
        marginBottom: 12,
        padding: 12,
        borderRadius: 8,

        backgroundColor: isDark ? "#1e1e1e" : "#ffffff",
        borderWidth: 1,
        borderColor: isDark ? "#333" : "#ddd",

        
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
        elevation: 3,
      }}
    >
      <Text
        style={{
          fontSize: 16,
          fontWeight: "bold",
          color: isDark ? "#fff" : "#000",
        }}
      >
        {item.name}
      </Text>

      <Text style={{ color: isDark ? "#ccc" : "#555" }}>
        {item.address}
      </Text>

      <Text style={{ color: isDark ? "#ccc" : "#555" }}>
        Type: {item.type}
      </Text>

      <Text style={{ color: isDark ? "#ccc" : "#555" }}>
        Free spots: {item.availableSpots}
      </Text>

      <Text style={{ color: isDark ? "#ccc" : "#555" }}>
        Status: {item.isLive ? "Online" : "Offline"}
      </Text>

      {item.link ? (
        <TouchableOpacity onPress={() => Linking.openURL(item.link)}>
          <Text
            style={{
              color: isDark ? "#4da6ff" : "blue",
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
    </View></>
  );
};

export default AllParkings;