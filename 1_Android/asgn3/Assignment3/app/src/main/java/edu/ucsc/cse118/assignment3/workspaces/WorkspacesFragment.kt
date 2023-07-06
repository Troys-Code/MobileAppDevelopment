package edu.ucsc.cse118.assignment3.workspaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import edu.ucsc.cse118.assignment3.channels.ChannelsFragment
import edu.ucsc.cse118.assignment3.R
import edu.ucsc.cse118.assignment3.data.Workspace
import edu.ucsc.cse118.assignment3.WorkspaceAdapter
import androidx.recyclerview.widget.LinearLayoutManager

class WorkspacesFragment : Fragment() {
    private lateinit var workspaceAdapter: WorkspaceAdapter
    private lateinit var workspacesViewModel: WorkspacesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workspaces, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Workspaces"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        workspacesViewModel = ViewModelProvider(this).get(WorkspacesViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        workspacesViewModel.workspacesLiveData.observe(viewLifecycleOwner, { workspaces ->
            workspaceAdapter = WorkspaceAdapter(workspaces, ::onWorkspaceClicked)
            recyclerView.adapter = workspaceAdapter
        })

        workspacesViewModel.fetchWorkspaces()
    }

    fun onWorkspaceClicked(workspace: Workspace) {
        val channelsFragment = ChannelsFragment()

        val args = Bundle()
        val workspaceJson = Gson().toJson(workspace)

        args.putString("workspace", workspaceJson)
        channelsFragment.arguments = args

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, channelsFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "CSE118 Assignment 2"
    }
}
