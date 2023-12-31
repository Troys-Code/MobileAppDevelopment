import React from 'react';
import {TouchableWithoutFeedback, StyleSheet, Text, View} from 'react-native';
import {WorkspaceContext} from '../Model/WorkspaceViewModel';

const styles = StyleSheet.create({
  // TODO : Add styles to the card
});

const WorkspaceCard = ({workspace, navigation}) => {
  const {setWorkspace} = React.useContext(WorkspaceContext);
  return (
    <TouchableWithoutFeedback
      onPress={() => {
        setWorkspace(workspace);
        navigation.navigate('Channels', {title: workspace.name});
      }}>
      <View style={styles.container}>
        <Text style={styles.item}>{workspace.name}</Text>
      </View>
    </TouchableWithoutFeedback>
  );
};
export default WorkspaceCard;
