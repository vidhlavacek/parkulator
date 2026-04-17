
import React from 'react';
import {
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  View,
  Image,
  Pressable,
} from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { LinearGradient } from 'expo-linear-gradient';
import { Ionicons, MaterialCommunityIcons, Entypo } from '@expo/vector-icons';
import { useRouter } from 'expo-router';



const parkingImage = require('../../assets/images/slikaparking.png');
const router = useRouter();

export default function HomeScreen() {
  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar style="dark" />

      <ScrollView
        contentContainerStyle={styles.screen}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.wrapper}>
          <View style={styles.heroCard}>
            <Text style={styles.title}>
              Find <Text style={styles.titleBold}>Parking</Text> Quickly{'\n'}& Easily
            </Text>

            <Text style={styles.subtitle}>
              Discover available parking near you.
            </Text>

            <View style={styles.imageWrapper}>
              <Image
                source={parkingImage}
                style={styles.heroImage}
                resizeMode="cover"
              />
            </View>


            <Pressable
              onPress={() => {}}
              style={({ pressed }) => [
                styles.mainButtonShadow,
                pressed && styles.pressedScale,
              ]}
            >
              {({ pressed }) => (
                <LinearGradient
                  colors={pressed ? ['#45b92e', '#238916'] : ['#58cc3a', '#2fa51f']}
                  start={{ x: 0, y: 0 }}
                  end={{ x: 0, y: 1 }}
                  style={styles.mainButton}
                >
                  <MaterialCommunityIcons name="car-search" size={24} color="#fff" />
                  <Text style={styles.mainButtonText}>Find Parking</Text>
                </LinearGradient>
              )}
            </Pressable>

            <Pressable
              onPress={() => {}}
              style={({ pressed }) => [
                styles.mapButton,
                styles.softShadow,
                pressed && styles.mapButtonPressed,
                pressed && styles.pressedScale,
              ]}
            >
              <Entypo name="map" size={20} color="#72819a" />
              <Text style={styles.mapButtonText}>Open Map</Text>
            </Pressable>
          </View>

          <View style={styles.quickSection}>
            <Text style={styles.sectionTitle}>Quick Access</Text>

            <View style={styles.quickGrid}>
              <Pressable
                onPress={() => {}}
                style={({ pressed }) => [
                  styles.quickCard,
                  styles.softShadow,
                  pressed && styles.quickCardPressed,
                  pressed && styles.pressedScale,
                ]}
              >
                <Ionicons name="heart" size={22} color="#e21b1b" />
                <Text style={styles.quickCardTitle}>Favourites</Text>
                <Text style={styles.quickCardSubtitle}>Saved places</Text>
              </Pressable>

              <Pressable
                onPress={() => {}}
                style={({ pressed }) => [
                  styles.quickCard,
                  styles.softShadow,
                  pressed && styles.quickCardPressed,
                  pressed && styles.pressedScale,
                ]}
              >
                <Ionicons name="time" size={22} color="#b88500" />
                <Text style={styles.quickCardTitle}>History</Text>
                <Text style={styles.quickCardSubtitle}>Recent parking</Text>
              </Pressable>
            </View>
          </View>

          <View style={styles.authCard}>
            <Text style={styles.authTitle}>Already have an account?</Text>

            <Pressable
              //onPress={() => router.navigate('/login')}
              onPress={() => {
                router.push('/login');
              }}           
              style={({ pressed }) => [
                styles.loginShadow,
                pressed && styles.pressedScale,
              ]}
            >
              {({ pressed }) => (
                <LinearGradient
                  colors={pressed ? ['#247ee8', '#0059c9'] : ['#2c8cff', '#0066e8']}
                  start={{ x: 0, y: 0 }}
                  end={{ x: 0, y: 1 }}
                  style={styles.loginButton}
                >
                  <Text style={styles.loginButtonText}>Log In</Text>
                </LinearGradient>
              )}
            </Pressable>
              
            <Pressable
              onPress={() => {
                router.push('/register');
              }}
              style={({ pressed }) => [
                styles.signUpLink,
                pressed && styles.signUpLinkPressed,
              ]}
            >
              <Text style={styles.signUpText}>
                Don’t have an account? <Text style={styles.signUpBold}>Sign Up</Text>
              </Text>
            </Pressable>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#dfe3ea',
  },
  screen: {
    flexGrow: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 16,
    paddingTop: 18,
    paddingBottom: 24,
  },
  wrapper: {
    width: '100%',
    maxWidth: 380,
    gap: 16,
  },

  heroCard: {
    backgroundColor: '#ffffff',
    borderRadius: 30,
    paddingHorizontal: 20,
    paddingTop: 28,
    paddingBottom: 20,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 14,
    shadowOffset: { width: 0, height: 8 },
    elevation: 6,
  },
  title: {
    textAlign: 'center',
    fontSize: 24,
    lineHeight: 32,
    color: '#33496b',
    fontWeight: '400',
  },
  titleBold: {
    fontWeight: '800',
  },
  subtitle: {
    textAlign: 'center',
    fontSize: 16,
    color: '#7a879b',
    marginTop: 12,
    marginBottom: 18,
  },
  imageWrapper: {
    backgroundColor: '#ffffff',
    borderRadius: 18,
    overflow: 'hidden',
    marginBottom: 18,
    padding: 10,
  },
  heroImage: {
    width: '100%',
    height: 170,
  },

  mainButtonShadow: {
    borderRadius: 16,
    marginBottom: 12,
    shadowColor: '#2fa51f',
    shadowOpacity: 0.24,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 6 },
    elevation: 7,
  },
  mainButton: {
    height: 68,
    borderRadius: 16,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  mainButtonText: {
    color: '#fff',
    fontSize: 22,
    fontWeight: '700',
    marginLeft: 10,
  },

  mapButton: {
    height: 56,
    borderRadius: 14,
    backgroundColor: '#fbfcfe',
    borderWidth: 1,
    borderColor: '#d9dee7',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  mapButtonPressed: {
    backgroundColor: '#dbe3ed',
    borderColor: '#9eb0c5',
  },
  mapButtonText: {
    marginLeft: 8,
    color: '#72819a',
    fontSize: 18,
    fontWeight: '600',
  },

  quickSection: {
    gap: 10,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '700',
    color: '#465a79',
    paddingHorizontal: 4,
  },
  quickGrid: {
    flexDirection: 'row',
    gap: 12,
  },
  quickCard: {
    flex: 1,
    backgroundColor: '#ffffff',
    borderRadius: 22,
    minHeight: 115,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 12,
    borderWidth: 1,
    borderColor: '#edf1f5',
  },
  quickCardPressed: {
    backgroundColor: '#e9eef5',
    borderColor: '#c8d3df',
  },
  quickCardTitle: {
    marginTop: 10,
    fontSize: 18,
    fontWeight: '700',
    color: '#3a4e6c',
  },
  quickCardSubtitle: {
    marginTop: 4,
    fontSize: 13,
    color: '#8a97aa',
  },

  authCard: {
    backgroundColor: '#ffffff',
    borderRadius: 24,
    paddingHorizontal: 18,
    paddingVertical: 18,
    shadowColor: '#000',
    shadowOpacity: 0.06,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  authTitle: {
    textAlign: 'center',
    fontSize: 15,
    color: '#72819a',
    marginBottom: 14,
  },
  loginShadow: {
    borderRadius: 16,
    shadowColor: '#0067ea',
    shadowOpacity: 0.22,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 6 },
    elevation: 7,
  },
  loginButton: {
    height: 58,
    borderRadius: 16,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loginButtonText: {
    color: '#fff',
    fontSize: 22,
    fontWeight: '700',
  },

  signUpLink: {
    marginTop: 14,
    alignSelf: 'center',
    paddingHorizontal: 10,
    paddingVertical: 6,
    borderRadius: 10,
  },
  signUpLinkPressed: {
    backgroundColor: '#e9edf3',
    transform: [{ scale: 0.97 }],
  },
  signUpText: {
    fontSize: 15,
    color: '#6f7d92',
  },
  signUpBold: {
    color: '#2c8cff',
    fontWeight: '700',
  },

  softShadow: {
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },
  pressedScale: {
    transform: [{ scale: 0.97 }],
  },
});




