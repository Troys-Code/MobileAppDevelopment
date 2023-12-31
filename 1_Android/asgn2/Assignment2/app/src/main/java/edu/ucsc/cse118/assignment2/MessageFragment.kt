package edu.ucsc.cse118.assignment2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MessageFragment : Fragment() {

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for the MessageFragment
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    // Called immediately after onCreateView() has returned a view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the message JSON string from the arguments
        val messageJson = arguments?.getString("message")
        if (messageJson != null) {
            // Convert JSON string back to Message object
            val message: DataClasses.Message = DataClasses.Message.fromJson(messageJson)

            // Set the action bar title to the user's name
            (activity as AppCompatActivity).supportActionBar?.title = message.user.name

            // Find the date and content TextViews in the layout
            val dateTextView: TextView = view.findViewById(R.id.date)
            val contentTextView: TextView = view.findViewById(R.id.content)

            // Format the date string to the desired format
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val targetFormat = SimpleDateFormat("MMM d, yyyy, hh:mm:ss a", Locale.US)
            val formattedDate = targetFormat.format(originalFormat.parse(message.date))

            // Set the formatted date and content text to the TextViews
            dateTextView.text = formattedDate
            contentTextView.text = message.content
        }
    }

    // Called when the view hierarchy associated with the fragment is being removed
    override fun onDestroyView() {
        super.onDestroyView()
        // Show the "Up" button in the action bar
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
