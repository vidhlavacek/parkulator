import { SafeAreaView, ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';

export default function HomeScreen() {
  return (
    <SafeAreaView style={styles.safeArea}>
      <ScrollView contentContainerStyle={styles.container}>
        <Text style={styles.title}>
          Find <Text style={styles.titleBold}>Parking</Text> Quickly{'\n'}& Easily
        </Text>

        <Text style={styles.subtitle}>Discover available parking near you.</Text>

        <View style={styles.illustration}>
          <Text style={styles.illustrationText}>Parking illustration placeholder</Text>
        </View>

        <TouchableOpacity style={styles.primaryButton}>
          <Text style={styles.primaryButtonText}>Find Parking</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.secondaryButton}>
          <Text style={styles.secondaryButtonText}>Go to Map</Text>
        </TouchableOpacity>

        <View style={styles.featuresRow}>
          <View style={styles.featureCard}>
            <Text style={styles.featureTitle}>Pay in Seconds</Text>
          </View>

          <View style={styles.featureCard}>
            <Text style={styles.featureTitle}>Parking Alerts</Text>
          </View>

          <View style={styles.featureCard}>
            <Text style={styles.featureTitle}>Parking History</Text>
          </View>
        </View>

        <TouchableOpacity style={styles.loginButton}>
          <Text style={styles.loginButtonText}>Log In</Text>
        </TouchableOpacity>

        <View style={styles.signUpContainer}>
          <Text style={styles.signUpText}>Sign Up</Text>
          <Text style={styles.signUpSubtext}>Create an Account</Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#F4F6F8',
  },
  container: {
    padding: 20,
    paddingBottom: 32,
    alignItems: 'center',
  },
  title: {
    marginTop: 20,
    textAlign: 'center',
    fontSize: 32,
    lineHeight: 40,
    color: '#1F3B5B',
    fontWeight: '400',
  },
  titleBold: {
    fontWeight: '700',
  },
  subtitle: {
    marginTop: 14,
    marginBottom: 24,
    textAlign: 'center',
    fontSize: 17,
    color: '#6B7280',
  },
  illustration: {
    width: '100%',
    height: 190,
    borderRadius: 20,
    backgroundColor: '#DCEBFA',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 24,
  },
  illustrationText: {
    color: '#4B5563',
    fontSize: 16,
  },
  primaryButton: {
    width: '100%',
    backgroundColor: '#37B24D',
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: 'center',
    marginBottom: 14,
  },
  primaryButtonText: {
    color: '#FFFFFF',
    fontSize: 20,
    fontWeight: '700',
  },
  secondaryButton: {
    width: '100%',
    backgroundColor: '#FFFFFF',
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: 'center',
    marginBottom: 24,
    borderWidth: 1,
    borderColor: '#D1D5DB',
  },
  secondaryButtonText: {
    color: '#6B7280',
    fontSize: 20,
    fontWeight: '500',
  },
  featuresRow: {
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-between',
    gap: 8,
    marginBottom: 28,
  },
  featureCard: {
    flex: 1,
    minHeight: 90,
    backgroundColor: '#FFFFFF',
    borderRadius: 14,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: 8,
    borderWidth: 1,
    borderColor: '#E5E7EB',
  },
  featureTitle: {
    textAlign: 'center',
    color: '#374151',
    fontSize: 14,
    fontWeight: '600',
  },
  loginButton: {
    width: '100%',
    backgroundColor: '#1677F2',
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: 'center',
    marginBottom: 20,
  },
  loginButtonText: {
    color: '#FFFFFF',
    fontSize: 20,
    fontWeight: '700',
  },
  signUpContainer: {
    alignItems: 'center',
  },
  signUpText: {
    fontSize: 18,
    fontWeight: '700',
    color: '#6B7280',
  },
  signUpSubtext: {
    marginTop: 6,
    fontSize: 15,
    color: '#9CA3AF',
  },
});