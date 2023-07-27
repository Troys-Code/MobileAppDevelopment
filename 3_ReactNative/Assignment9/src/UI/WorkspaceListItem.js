// UI/WorkspaceListItem.js
import React, {useState, useEffect, useContext} from 'react';
import {TouchableWithoutFeedback, StyleSheet, Text, View} from 'react-native';
import {ChannelContext} from '../Model/ChannelViewModel';
import AuthContext from '../Model/AuthContext';
import { HeaderBackButton } from '@react-navigation/stack';

const styles = StyleSheet.create({
  // Your styles here
});

const WorkspaceListItem = ({workspace, navigation}) => {
  const [isLoading, setLoading] = useState(false);
  const {loadChannelsForWorkspace} = React.useContext(ChannelContext);
  const {token} = useContext(AuthContext); // <-- Fetch the token from AuthContext

  const handleWorkspaceClick = async () => {
    setLoading(true);
    try {
      console.log('\nUsing token:', token);
      await loadChannelsForWorkspace(workspace.id, token); // <-- Pass the token here
      navigation.navigate('Channels', {
        workspaceName: workspace.name,
      });
    } catch (error) {
      console.error('Error loading channels:', error.message);
      // Potentially display a message to the user here
    }
    setLoading(false);
  };

  return (
    <TouchableWithoutFeedback
      onPress={handleWorkspaceClick}
      accessibilityLabel={workspace.name}
      disabled={isLoading} // Prevent additional clicks while loading
    >
      <View style={styles.container}>
        <Text style={styles.item}>{workspace.name}</Text>
        <Text
          style={styles.details}
          accessibilityLabel={`count for ${workspace.name}`}>
          {`Channels: ${workspace.channels}`}
        </Text>
      </View>
    </TouchableWithoutFeedback>
  );
};

export default WorkspaceListItem;
