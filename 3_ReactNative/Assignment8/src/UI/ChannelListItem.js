// ChannelListItem.js
import React from 'react';
import {StyleSheet, Text, View, TouchableOpacity} from 'react-native';
import {formatElapsedTime} from '../Utils/ElapsedTimeFormatter';

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: 10,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
  },
  item: {
    paddingLeft: 10,
    fontSize: 18,
  },
  details: {
    flexDirection: 'column',
    alignItems: 'flex-end',
  },
  detailText: {
    fontSize: 14,
    color: '#888',
  },
});

const ChannelListItem = ({channel, navigation, workspaceName}) => {
  const handlePress = () => {
    navigation.navigate('Messages', {
      channelId: channel.id,
      channelName: channel.name,
      workspaceName: workspaceName,
    });
  };

  // Use the utility function to get the elapsed time
  const elapsedTime = formatElapsedTime(new Date(channel.mostRecentMessage));

  return (
    <TouchableOpacity onPress={handlePress}>
      <View style={styles.container}>
        <Text style={styles.item}>{channel.name}</Text>
        <View style={styles.details}>
          <Text style={styles.detailText}>
            Messages: {channel.messages.length}
          </Text>
          <Text style={styles.detailText}>
            Members: {channel.uniquePosters}
          </Text>
          <Text style={styles.detailText}>Latest: {elapsedTime} ago</Text>
        </View>
      </View>
    </TouchableOpacity>
  );
};

export default ChannelListItem;
