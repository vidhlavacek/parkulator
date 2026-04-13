import React from 'react';
import {
  SafeAreaView,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  Image,
} from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { LinearGradient } from 'expo-linear-gradient';
import {
  Ionicons,
  MaterialCommunityIcons,
  FontAwesome5,
  Entypo,
} from '@expo/vector-icons';

export default function HomeScreen() {
  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar style="dark" />

      <ScrollView
        contentContainerStyle={styles.screen}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.wrapper}>
          <View style={styles.topCard}>
            <Text style={styles.title}>
              Find <Text style={styles.titleBold}>Parking</Text> Quickly{'\n'}& Easily
            </Text>

            <Text style={styles.subtitle}>
              Discover available parking near you.
            </Text>

            <View style={styles.imageWrapper}>
              <Image
                source={{
                  uri: 'https://images.unsplash.com/photo-1590674899484-d5640e854abe?q=80&w=1200&auto=format&fit=crop',
                }}
                style={styles.heroImage}
                resizeMode="cover"
              />
            </View>

            <TouchableOpacity activeOpacity={0.9} style={styles.shadowButton}>
              <LinearGradient
                colors={['#58cc3a', '#2fa51f']}
                start={{ x: 0, y: 0 }}
                end={{ x: 0, y: 1 }}
                style={styles.primaryButton}
              >
                <MaterialCommunityIcons name="car" size={24} color="#fff" />
                <Text style={styles.primaryButtonText}>Find Parking</Text>
              </LinearGradient>
            </TouchableOpacity>

            <TouchableOpacity activeOpacity={0.9} style={[styles.secondaryButton, styles.shadowSoft]}>
              <Entypo name="location-pin" size={22} color="#7e8aa0" />
              <Text style={styles.secondaryButtonText}>Go to Map</Text>
            </TouchableOpacity>

            <View style={styles.dashedLine} />

            <View style={styles.featuresRow}>
              <TouchableOpacity activeOpacity={0.9} style={[styles.featureCard, styles.shadowSoft]}>
                <FontAwesome5 name="coins" size={21} color="#f4c247" />
                <Text style={styles.featureText}>Pay in Seconds</Text>
              </TouchableOpacity>

              <TouchableOpacity activeOpacity={0.9} style={[styles.featureCard, styles.shadowSoft]}>
                <Ionicons name="notifications" size={22} color="#394b65" />
                <Text style={styles.featureText}>Parking Alerts</Text>
              </TouchableOpacity>

              <TouchableOpacity activeOpacity={0.9} style={[styles.featureCard, styles.shadowSoft]}>
                <Ionicons name="time" size={22} color="#394b65" />
                <Text style={styles.featureText}>Parking History</Text>
              </TouchableOpacity>
            </View>
          </View>

          <TouchableOpacity activeOpacity={0.9} style={styles.shadowButton}>
            <LinearGradient
              colors={['#2c8cff', '#0066e8']}
              start={{ x: 0, y: 0 }}
              end={{ x: 0, y: 1 }}
              style={styles.loginButton}
            >
              <Text style={styles.loginButtonText}>Log In</Text>
            </LinearGradient>
          </TouchableOpacity>

          <View style={styles.signUpRow}>
            <View style={styles.line} />
            <Text style={styles.signUpText}>Sign Up</Text>
            <View style={styles.line} />
          </View>

          <Text style={styles.signUpSubtext}>Create an Account</Text>


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
    paddingHorizontal: 18,
    paddingVertical: 20,
  },
  wrapper: {
    width: '100%',
    maxWidth: 360,
  },

  topCard: {
    backgroundColor: '#ffffff',
    borderRadius: 28,
    paddingHorizontal: 18,
    paddingTop: 28,
    paddingBottom: 20,
    marginBottom: 18,
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 14,
    shadowOffset: { width: 0, height: 8 },
    elevation: 6,
  },

  title: {
    marginTop: 8,
    textAlign: 'center',
    fontSize: 24,
    lineHeight: 31,
    color: '#324765',
    fontWeight: '400',
  },
  titleBold: {
    fontWeight: '800',
  },
  subtitle: {
    marginTop: 12,
    marginBottom: 18,
    textAlign: 'center',
    fontSize: 16,
    color: '#7a879b',
  },

  imageWrapper: {
    borderRadius: 18,
    overflow: 'hidden',
    marginBottom: 18,
    backgroundColor: '#edf4ff',
  },
  heroImage: {
    width: '100%',
    height: 170,
  },

  shadowButton: {
    shadowColor: '#000',
    shadowOpacity: 0.16,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 6 },
    elevation: 6,
    marginBottom: 14,
    borderRadius: 14,
  },
  shadowSoft: {
    shadowColor: '#000',
    shadowOpacity: 0.1,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 4 },
    elevation: 4,
  },

  primaryButton: {
    height: 72,
    borderRadius: 14,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
  },
  primaryButtonText: {
    color: '#fff',
    fontSize: 21,
    fontWeight: '700',
    marginLeft: 10,
  },

  secondaryButton: {
    height: 58,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#d9dee7',
    backgroundColor: '#fbfcfe',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 18,
  },
  secondaryButtonText: {
    fontSize: 18,
    color: '#7c889b',
    fontWeight: '500',
    marginLeft: 4,
  },

  dashedLine: {
    borderBottomWidth: 1,
    borderBottomColor: '#e2e7ef',
    borderStyle: 'dashed',
    marginBottom: 18,
  },

  featuresRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 10,
  },
  featureCard: {
    flex: 1,
    backgroundColor: '#ffffff',
    borderRadius: 16,
    minHeight: 88,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 8,
    borderWidth: 1,
    borderColor: '#edf1f5',
  },
  featureText: {
    textAlign: 'center',
    marginTop: 8,
    fontSize: 13,
    lineHeight: 17,
    color: '#3b4c66',
    fontWeight: '500',
  },

  loginButton: {
    height: 58,
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
  },
  loginButtonText: {
    color: '#fff',
    fontSize: 22,
    fontWeight: '700',
  },

  signUpRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 2,
    justifyContent: 'center',
  },
  line: {
    flex: 1,
    height: 1,
    backgroundColor: '#dfe4ec',
  },
  signUpText: {
    marginHorizontal: 12,
    fontSize: 16,
    color: '#5f6d84',
    fontWeight: '700',
  },
  signUpSubtext: {
    textAlign: 'center',
    marginTop: 8,
    marginBottom: 18,
    fontSize: 16,
    color: '#7d899b',
  },

  bottomNav: {
    flexDirection: 'row',
    backgroundColor: '#ffffff',
    borderRadius: 22,
    paddingVertical: 14,
    justifyContent: 'space-around',
    shadowColor: '#000',
    shadowOpacity: 0.08,
    shadowRadius: 10,
    shadowOffset: { width: 0, height: 4 },
    elevation: 5,
  },
  navItem: {
    alignItems: 'center',
    flex: 1,
  },
  navLabel: {
    marginTop: 4,
    fontSize: 14,
    color: '#97a2b5',
  },
  navLabelActive: {
    color: '#1f7cff',
    fontWeight: '700',
  },
});