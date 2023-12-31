// UI/Login.js

import React, {useState, useContext} from 'react';
import {View, TextInput, Button, StyleSheet, Alert} from 'react-native';
import AuthContext from '../Model/AuthContext';

const Login = ({navigation}) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const {signIn} = useContext(AuthContext);

  const handleLogin = async () => {
    const response = await fetch('https://cse118.com/api/v2/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({email, password}),
    });

    if (response.ok) {
      const data = await response.json();
      signIn(data.accessToken);
      navigation.navigate('Workspaces');
    } else {
      Alert.alert(
        'Login Failed',
        'Please check your credentials and try again.',
      );
    }
  };

  return (
    <View style={styles.container}>
      <TextInput
        style={styles.input}
        placeholder="Email"
        value={email}
        onChangeText={setEmail}
        accessibilityLabel="email"
      />
      <TextInput
        style={styles.input}
        placeholder="Password"
        value={password}
        onChangeText={setPassword}
        secureTextEntry={true}
        accessibilityLabel="password"
      />
      <Button title="Login" onPress={handleLogin} accessibilityLabel="login" />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 16,
  },
  input: {
    height: 40,
    borderColor: 'gray',
    borderWidth: 1,
    marginBottom: 12,
    padding: 8,
  },
});

export default Login;
