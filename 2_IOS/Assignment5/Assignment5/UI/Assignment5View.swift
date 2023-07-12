/*
#######################################################################
#
# Copyright (C) 2022-2023 David C. Harrison. All right reserved.
#
# You may not use, distribute, publish, or modify this code without
# the express written permission of the copyright holder.
#
#######################################################################
*/

import SwiftUI

struct DateFormatterUtil {
    static func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM d, yyyy 'at' h:mm a"
        return formatter.string(from: date)
    }
}

struct Assignment5View: View {
    @EnvironmentObject var dataStore: DataStore
    
    var body: some View {
        NavigationView {
            List(dataStore.workspaces) { workspace in
                NavigationLink(destination: ChannelListView(workspace: workspace)) {
                    Text(workspace.name)
                }
            }
            .navigationBarTitle("Workspaces")
        }
    }
}

struct ChannelListView: View {
    let workspace: Workspace

    var body: some View {
        List(workspace.channels) { channel in
            NavigationLink(destination: MessageListView(channel: channel)) {
                Text(channel.name)
            }
        }
        .navigationBarTitle(workspace.name)
    }
}

struct MessageListView: View {
    let channel: Channel

    var body: some View {
        List(channel.messages) { message in
            NavigationLink(destination: MessageDetailView(message: message)) {
                VStack(alignment: .leading) {
                    Text(message.member.name).bold()
                    Text(message.content)
                    Text(DateFormatterUtil.formatDate(message.posted))
                        .frame(maxWidth: .infinity, alignment: .trailing)
                }
            }
        }
        .navigationBarTitle(channel.name)
    }
}

struct MessageDetailView: View {
    let message: Message

    var body: some View {
        VStack(alignment: .leading) {
            Text(message.content)
            Text(DateFormatterUtil.formatDate(message.posted))
                .frame(maxWidth: .infinity, alignment: .trailing)
            Spacer()
        }
        .padding()
        .navigationBarTitle(message.member.name)
    }
}

#if !TESTING
struct Assignment5View_Previews: PreviewProvider {
    static var previews: some View {
        Assignment5View().environmentObject(DataStore())
    }
}
#endif