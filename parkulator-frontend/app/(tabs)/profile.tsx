import {
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  ScrollView,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useAuth } from "../../context/AuthContext";
import Button from "@/components/ui/Button"

export default function Profile() {
  const router = useRouter();
  const { user, isAuthenticated, isLoading, signOut } = useAuth();

  const handleLogout = async () => {
    await signOut();
    router.replace("/Login");
  };

  if (isLoading) return null;

  if (!isAuthenticated) {
    return (
      <View style={styles.container}>
        <Text style={styles.title}>Profile</Text>
        <View style={styles.card}>
          <Text style={styles.name}>You are not logged in</Text>
          <Text style={styles.email}>
            Please log in to access your profile.
          </Text>

          <Button
            title="Log In"
            onPress={() => router.push("/Login")}
            variant="primary"
          />
        </View>
      </View>
    );
  }

  return (
    <ScrollView style={styles.container}>
      <Text style={styles.title}>Profile</Text>

      <View style={styles.card}>
        <View style={styles.row}>
          

          <View style={{ flex: 1 }}>
            <Text style={styles.name}>{user?.username}</Text>
            <Text style={styles.email}>{user?.email}</Text>
          </View>

          <TouchableOpacity
            style={styles.editBtn}
            onPress={() => router.push("/EditProfile")}
          >
            <Text>Edit</Text>
          </TouchableOpacity>
          

        </View>
      </View>

      <View style={styles.card}>
        <MenuItem icon="help-circle-outline" text="Help & Support" />
        <MenuItem icon="warning-outline" text="Report Issue" />
        <MenuItem icon="document-text-outline" text="Legal Information" />
      </View>

      <TouchableOpacity style={styles.logout} onPress={handleLogout}>
        <Text style={styles.logoutText}>Log Out</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

function MenuItem({ icon, text }: any) {
  return (
    <TouchableOpacity style={styles.menuItem}>
      <Ionicons name={icon} size={20} color="#333" />
      <Text style={styles.menuText}>{text}</Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#dfe3ea',
    padding: 15,
    paddingTop: 40,
  },
  title: {
    fontSize: 22,
    fontWeight: "bold",
    marginBottom: 15,
  },
  card: {
    backgroundColor: "white",
    borderRadius: 15,
    padding: 15,
    marginBottom: 15,
    elevation: 3,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
  },
  name: {
    fontSize: 16,
    fontWeight: "bold",
  },
  email: {
    color: "gray",
    marginTop: 2,
    marginBottom: 5,
  },
  editBtn: {
    padding: 6,
    backgroundColor: "#eee",
    borderRadius: 8,
  },
  menuItem: {
    flexDirection: "row",
    alignItems: "center",
    paddingVertical: 10,
  },
  menuText: {
    marginLeft: 10,
    fontSize: 14,
  },
  logout: {
    backgroundColor: "#CA0B00",
    padding: 15,
    borderRadius: 10,
    alignItems: "center",
  },
  logoutText: {
    color: "white",
    fontWeight: "bold",
  },
 
});