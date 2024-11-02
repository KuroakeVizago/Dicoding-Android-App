package com.kuroakevizago.dicodingapp.ui.finished

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuroakevizago.dicodingapp.adapter.EventViewAdapter
import com.kuroakevizago.dicodingapp.data.display.IDisplayableItem
import com.kuroakevizago.dicodingapp.data.remote.response.ListEventsItem
import com.kuroakevizago.dicodingapp.databinding.FragmentFinishedBinding
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.helper.ViewModelFactory
import com.kuroakevizago.dicodingapp.interfaces.OnItemClickListener
import com.kuroakevizago.dicodingapp.ui.detail.DetailActivity
import com.kuroakevizago.dicodingapp.ui.home.HomeViewModel

class FinishedFragment : Fragment(), OnItemClickListener<List<IDisplayableItem?>> {

    private var _binding: FragmentFinishedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var finishedEventList : List<ListEventsItem?>
    private lateinit var homeViewModel : HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val factory = ViewModelFactory.getInstance(requireActivity().application)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        observeViewModel(homeViewModel)

        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun observeViewModel(homeViewModel: HomeViewModel) {
        val upcomingEventViewAdapter = EventViewAdapter(this)
        binding.rvFinishedEventList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.rvFinishedEventList.adapter = upcomingEventViewAdapter

        homeViewModel.loadStateFinishedEvents.observe(viewLifecycleOwner) { loadState ->
            upcomingEventViewAdapter.viewState = loadState
            binding.rvFinishedEventList.adapter = upcomingEventViewAdapter
            upcomingEventViewAdapter.notifyDataSetChanged()
            if (loadState == ViewState.Failed) {
                setFinishedErrorRetry(View.VISIBLE)
            }
        }

        homeViewModel.finishedEvents.observe(viewLifecycleOwner) { listEvents ->
            if (listEvents != null) {
                finishedEventList = listEvents
                upcomingEventViewAdapter.eventList = finishedEventList
                upcomingEventViewAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(position: Int, itemList: List<IDisplayableItem?>) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(
            "event_id",
            itemList[position]?.id
        )
        startActivity(intent)    }

    private fun setFinishedErrorRetry(visibility: Int) {
        if (visibility == View.VISIBLE) {
            binding.rvFinishedEventList.visibility = View.INVISIBLE
            binding.errorFinishedRetry.root.visibility = View.VISIBLE

            binding.errorFinishedRetry.btnRetryConnection.setOnClickListener {
                setFinishedErrorRetry(View.INVISIBLE)
                homeViewModel.fetchFinishedEvents()
            }
        } else if (visibility == View.INVISIBLE) {
            binding.rvFinishedEventList.visibility = View.VISIBLE
            binding.errorFinishedRetry.root.visibility = View.INVISIBLE
        }
    }
}