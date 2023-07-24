// ChannelRepo.js

// This utility function is responsible for fetching channels associated
// with a given workspace from the Workspaces.json file.

// // Import the local JSON data.
// const workspacesJson = require('../Resources/Workspaces.json');

// /**
//  * Fetches the channels associated with a specific workspace ID.
//  *
//  * @param {number} workspaceId - The ID of the workspace to retrieve channels for.
//  * @returns {Array} - A list of channels associated with the given workspace,
//  * or an empty array if the workspace is not found.
//  * @throws Will throw an error if there's an issue parsing the JSON data.
//  */

// export const GET_CHANNELS_FOR_WORKSPACE = async workspaceId => {
//   // Search through the workspaces to find a workspace with the given ID.
//   const workspace = workspacesJson.find(ws => ws.id === workspaceId);

//   // If a workspace is found with the matching ID, return its associated channels.
//   // If not found, return an empty array.
//   return workspace.channels;
// };

import { Workspace, Channel, Message, Member } from '../Model/DataClasses'; // Adjust the path as necessary

const workspacesJson = require('../Resources/Workspaces.json');

export const GET_CHANNELS_FOR_WORKSPACE = async workspaceId => {
  const workspace = workspacesJson.find(ws => ws.id === workspaceId);

  if (!workspace) return [];

  const channels = workspace.channels.map(channelData => {
    const messages = channelData.messages.map(messageData => {
      return new Message(
        messageData.id, 
        messageData.content, 
        messageData.posted, 
        new Member(messageData.member.id, messageData.member.name)
      );
    });
    return new Channel(channelData.id, channelData.name, messages);
  });
  
  return channels;
};


/*
SUMMARY:

The ChannelRepo.js module is essentially a data access layer for channels associated
with workspaces. Its primary responsibility is to fetch channel data from the local
Workspaces.json file based on a provided workspace ID.

The `GET_CHANNELS_FOR_WORKSPACE` function takes in a workspace ID as its argument and
returns an array of channels associated with that workspace. If the workspace is not
found in the data, an empty array is returned. In the case of any errors during data
fetching or processing (like JSON parsing errors), the error is logged to the console.

INTERACTION WITH OTHER FILES:

1. Components or other modules that need channel information for a specific workspace
will import this utility function and call it with the appropriate workspace ID.
2. The returned channels (or an empty array) can then be used by the calling component
or module for display, processing, or any other purpose.
3. It's important to handle the results of this function appropriately in calling
components, checking for empty arrays (which indicates that the workspace was not found
in the data) and managing potential errors.
*/
