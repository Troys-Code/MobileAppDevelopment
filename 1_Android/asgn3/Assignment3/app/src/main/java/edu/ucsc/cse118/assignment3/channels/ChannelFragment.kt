package edu.ucsc.cse118.assignment3.channels

import ApiHandler
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ucsc.cse118.assignment3.ChannelAdapter
import edu.ucsc.cse118.assignment3.messages.MessagesFragment
import edu.ucsc.cse118.assignment3.R
import edu.ucsc.cse118.assignment3.data.DataClasses
import edu.ucsc.cse118.assignment3.data.DataClasses.Workspace
import edu.ucsc.cse118.assignment3.data.DataClasses.Channel
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChannelsFragment : Fragment() {
    private lateinit var channelAdapter: ChannelAdapter

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

        viewLifecycleOwner.lifecycleScope.launch {
            val workspaceJson = arguments?.getString("workspace")

            if (workspaceJson != null) {
                val workspace: DataClasses.Workspace = DataClasses.Workspace.fromJson(workspaceJson)
                // TODO Need to get channels from the server api call but not there yet.
//                channels = ApiHandler.getChannels()

                // Set the action bar title to the workspace name
                (activity as AppCompatActivity).supportActionBar?.title = workspace.name

                // Find the RecyclerView in the layout and set its layout manager
                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
                recyclerView.layoutManager = LinearLayoutManager(context)

                // Create an instance of the ChannelAdapter and set it as the adapter for the RecyclerView
                // workspace.channels
                val dummyList : List<DataClasses.Channel> = listOf()
                channelAdapter = ChannelAdapter(dummyList, ::onChannelClicked)
                recyclerView.adapter = channelAdapter
            }
        }
    }

    // Function to handle the click event on a channel item
    fun onChannelClicked(channel: DataClasses.Channel) {
        // Instantiate the new fragment
        val messagesFragment = MessagesFragment()

        print("channel: ${channel}")

        // Create a new bundle to hold the arguments
        val args = Bundle()

        // Convert channel object to JSON
        val channelJson = channel.toJson()

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
