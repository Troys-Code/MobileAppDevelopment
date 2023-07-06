package edu.ucsc.cse118.assignment3.channels

import ApiHandler
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ucsc.cse118.assignment3.ChannelAdapter
import edu.ucsc.cse118.assignment3.messages.MessagesFragment
import edu.ucsc.cse118.assignment3.R
import edu.ucsc.cse118.assignment3.data.Workspace
import edu.ucsc.cse118.assignment3.data.Channel
import kotlinx.coroutines.launch

class ChannelsFragment : Fragment() {
    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var channels : List<Channel>
    private lateinit var workspaceName: String

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the ChannelsFragment
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }

    // Called immediately after onCreateView() has returned a view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the workspace JSON string from the arguments
        val channelApi = viewLifecycleOwner.lifecycleScope.launch {
            val workspaceJson = arguments?.getString("workspace")

            if (workspaceJson != null) {
                val workspace: Workspace = Gson().fromJson(workspaceJson, Workspace::class.java)
                channels = ApiHandler.getChannels(workspace.id)
                workspaceName = workspace.name
            }
        }

        // Set the action bar title to the workspace name
        (activity as AppCompatActivity).supportActionBar?.title = workspaceName

        // Find the RecyclerView in the layout and set its layout manager
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Create an instance of the ChannelAdapter and set it as the adapter for the RecyclerView
        channelAdapter = ChannelAdapter(channels, ::onChannelClicked)
        recyclerView.adapter = channelAdapter
    }

    // Called when the fragment is visible to the user and actively running
    override fun onResume() {
        super.onResume()

        // Get the workspace JSON string from the arguments
        val workspaceJson = arguments?.getString("workspace")

        if (workspaceJson != null) {
            // Convert JSON string back to Workspace object
            val workspace: Workspace = Gson().fromJson(workspaceJson, Workspace::class.java)

            // Set the action bar title to the workspace name
            (activity as AppCompatActivity).supportActionBar?.title = workspace.name
        }
    }

    // Function to handle the click event on a channel item
    fun onChannelClicked(channel: Channel) {
        // Instantiate the new fragment
        val messagesFragment = MessagesFragment()

        print("channel: ${channel}")

        // Create a new bundle to hold the arguments
        val args = Bundle()

        // Convert channel object to JSON
        val channelJson = Gson().toJson(channel)

        // Add the channel JSON string to the arguments
        args.putString("channel", channelJson)

        // Set the arguments for the fragment
        messagesFragment.arguments = args

        // Replace the current fragment with the new one
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, messagesFragment)
            .addToBackStack(null)
            .commit()
    }
}
