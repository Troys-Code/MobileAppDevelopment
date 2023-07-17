import SwiftUI
import Combine

struct Assignment6View: View {
    @EnvironmentObject var viewModel: LoginViewModel
    @StateObject private var workspaceProvider = WorkspaceProvider()
    @StateObject private var channelProvider = ChannelProvider()
    @StateObject private var messageProvider = MessageProvider()
    @StateObject private var memberProvider = MemberProvider()

    @State private var navigateToLogin = false

    var body: some View {
        NavigationView {
            if viewModel.user != nil {
                WorkspaceListView(workspaceProvider: workspaceProvider, channelProvider: channelProvider, messageProvider: messageProvider, memberProvider: memberProvider, navigateToLogin: $navigateToLogin)
                    .environmentObject(viewModel)
                    .environmentObject(memberProvider)
            } else {
                LoginView()
            }
        }
        .onAppear {
            navigateToLogin = viewModel.user == nil
        }
    }
}

struct LoginView: View {
    @EnvironmentObject var viewModel: LoginViewModel

    var body: some View {
        ScrollView {
            VStack {
                Text("CSE118 Assignment 6")
                Image("SlugLogo")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 150, height: 150)
                    .clipped()

                TextField("Email", text: $viewModel.email)
                    .accessibilityLabel("EMail")

                SecureField("Password", text: $viewModel.password)
                    .accessibilityLabel("Password")

                Button("Log In", action: viewModel.login)
                    .disabled(!viewModel.isValid)
                    .accessibilityLabel("Login")

                Spacer() // Pushes the VStack to the top
            }
            .padding()
            .accessibilityIdentifier("LoginView")
        }
    }
}

class LoginViewModel: ObservableObject {
    @Published var email = ""
    @Published var password = ""
    @Published var isValid = false
    @Published var user: User? {
        didSet {
            if let user = user {
                print("User has been logged in with the id: \(user.id)")
            } else {
                print("User has been logged out")
            }
        }
    }
    @Published var selectedWorkspace: Workspace? = nil

    private var cancellableSet: Set<AnyCancellable> = []
    @ObservedObject var loginProvider = LoginProvider() // Add LoginProvider as an ObservedObject

    init() {
        isFormValidPublisher
            .receive(on: RunLoop.main)
            .assign(to: \.isValid, on: self)
            .store(in: &cancellableSet)
        
        // observe the user in LoginProvider
        loginProvider.$user
            .assign(to: \.user, on: self)
            .store(in: &cancellableSet)
    }

    private var isFormValidPublisher: AnyPublisher<Bool, Never> {
        Publishers.CombineLatest($email, $password)
            .map { email, password in
                return !email.isEmpty && !password.isEmpty
            }
            .eraseToAnyPublisher()
    }

    func login() {
        loginProvider.login(email: email, password: password)
    }

    func logout() {
        loginProvider.logout()
    }
}

struct WorkspaceListView: View {
    @EnvironmentObject var viewModel: LoginViewModel
    @ObservedObject var workspaceProvider: WorkspaceProvider
    @ObservedObject var channelProvider: ChannelProvider
    @ObservedObject var messageProvider: MessageProvider
    @ObservedObject var memberProvider: MemberProvider
    @Binding var navigateToLogin: Bool // Add navigateToLogin binding

    var body: some View {
        VStack {
            List(workspaceProvider.workspaces) { workspace in
                NavigationLink(destination: ChannelListView(workspace: workspace, channelProvider: channelProvider, messageProvider: messageProvider, memberProvider: memberProvider)) {
                    HStack {
                        Button(action: {}, label: {
                            Text(workspace.name).font(.headline)
                        })
                        .accessibilityIdentifier("\(workspace.name) Workspace")
                        Spacer() // This will push the next Text to the right
                        Text("\(workspace.channels)").accessibilityIdentifier("Channels \(workspace.channels)")
                    }
                }
            }
        }
        .onAppear {
            workspaceProvider.loadWorkspaces(withToken: viewModel.user?.accessToken ?? "")
        }
        .navigationBarTitle("Workspaces", displayMode: .inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarLeading) {
                Button(action: {
                    viewModel.logout()
                    navigateToLogin = true
                }) {
                    Image(systemName: "rectangle.portrait.and.arrow.right")
                        .font(.title)
                        .padding()
                        .accessibility(identifier: "Logout")
                }
            }
        }
        .accessibilityIdentifier("WorkspaceListView")
    }
}

struct ChannelListView: View {
    let workspace: Workspace
    @ObservedObject var channelProvider: ChannelProvider
    @ObservedObject var messageProvider: MessageProvider
    @EnvironmentObject var viewModel: LoginViewModel
    @ObservedObject var memberProvider: MemberProvider

    var body: some View {
        List(channelProvider.channels) { channel in
            NavigationLink(destination: MessageListView(channel: channel, messageProvider: messageProvider).environmentObject(viewModel).environmentObject(memberProvider)) {
                HStack {
                    Button(action: {}, label: {
                        Text(channel.name).font(.headline).foregroundColor(Color.primary)
                    })
                    .accessibilityIdentifier("\(channel.name) Channel")

                    Spacer() // This will push the next Text to the right
                    Text("\(channel.messageCount)")
                        .accessibilityIdentifier("Messages \(channel.name) ")
                }
            }
        }
        .navigationTitle(workspace.name)
        .onAppear {
            if let workspaceId = UUID(uuidString: workspace.id) {
                channelProvider.loadChannels(workspaceId: workspaceId, withToken: viewModel.user?.accessToken ?? "")
            }
        }
    }
}

struct MessageListView: View {
    let channel: Channel
    @ObservedObject var messageProvider: MessageProvider
    @EnvironmentObject var viewModel: LoginViewModel
    @EnvironmentObject var memberProvider: MemberProvider

    private let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM d, yyyy 'at' h:mm a"
        return formatter
    }()

    var body: some View {
            List {
                ForEach(messageProvider.messages, id: \.id) { message in
                    VStack(alignment: .leading) {
                        if let memberName = memberProvider.memberName(forID: message.member) {
                            Text("\(memberName)")
                        } else {
                            Text("Posted by: Unknown")
                        }
                        Text(message.content).font(.headline)
                        Text(dateFormatter.string(from: message.posted))
                    }
                }
                .onDelete(perform: delete)
            }
            .navigationTitle(channel.name)
            .navigationBarItems(trailing:
                NavigationLink(
                    destination: ComposeMessageView(messageProvider: messageProvider, channel: channel)
                        .environmentObject(viewModel),
                    label: {
                        Image(systemName: "plus")
                    }
                ).accessibilityIdentifier("New Message")
            )
            .onAppear {
                memberProvider.loadAllMembers(withToken: viewModel.user?.accessToken ?? "") // Load members
                messageProvider.loadMessages(channelId: channel.id, withToken: viewModel.user?.accessToken ?? "") // Load messages
            }
        }

        private func delete(at offsets: IndexSet) {
            print("Deleting at offsets: \(offsets)")
            for index in offsets {
                let message = messageProvider.messages[index]
                messageProvider.deleteMessage(messageId: message.id, withToken: viewModel.user?.accessToken ?? "") { result in
                    switch result {
                    case .success:
                        DispatchQueue.main.async {
                            self.messageProvider.messages.remove(at: index)
                        }
                    case .failure(let error):
                        print("Failed to delete message: \(error)")
                    }
                }
            }
        }
    }

struct ComposeMessageView: View {
    @Environment(\.presentationMode) var presentationMode
    @EnvironmentObject var viewModel: LoginViewModel
    @ObservedObject var messageProvider: MessageProvider
    var channel: Channel

    @State private var messageContent = ""

    var body: some View {
        VStack {
            TextEditor(text: $messageContent)
                .accessibilityIdentifier("Message")
                .padding()
            HStack {
                Spacer()
                
                HStack {
                    Button(action: cancel) {
                        Text("Cancel")
                    }
                    .accessibilityIdentifier("Cancel")
                    
                    Button(action: addMessage) {
                        Text("OK")
                    }
                    .disabled(messageContent.isEmpty)
                    .accessibilityIdentifier("OK")
                }
                Spacer()
            }
            .padding(.horizontal)
            Spacer()
        }
        .padding()
        .navigationBarTitle("New Message", displayMode: .inline)
    }

    private func cancel() {
        presentationMode.wrappedValue.dismiss()
    }

    private func addMessage() {
        guard !messageContent.isEmpty, let user = viewModel.user else {
            return
        }

        let member = user.toMember() // Convert User to Member
        messageProvider.addMessage(content: messageContent, channel: channel, member: member, withToken: viewModel.user?.accessToken ?? "")
        // Clear the input field after adding the message
        messageContent = ""
        presentationMode.wrappedValue.dismiss()
    }
}

struct User: Codable, Identifiable {
    let id: UUID
    let name: String
    let role: String
    let accessToken: String
    var selectedWorkspace: Workspace?
    
    // Function to convert User to Member
    func toMember() -> Member {
        return Member(id: self.id, name: self.name)
    }
}

#if !TESTING
struct Assignment6View_Previews: PreviewProvider {
    static var previews: some View {
        Assignment6View()
            .environmentObject(LoginViewModel())
    }
}
#endif
