package com.kuroakevizago.dicodingapp.ui.upcoming

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
import com.kuroakevizago.dicodingapp.databinding.FragmentUpcomingBinding
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.helper.ViewModelFactory
import com.kuroakevizago.dicodingapp.interfaces.OnItemClickListener
import com.kuroakevizago.dicodingapp.ui.detail.DetailActivity
import com.kuroakevizago.dicodingapp.ui.home.HomeViewModel

class UpcomingFragment : Fragment(), OnItemClickListener<List<IDisplayableItem?>> {

    private var _binding: FragmentUpcomingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var upcomingEventList : List<ListEventsItem?>
    private lateinit var homeViewModel : HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory.getInstance(requireActivity().application)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        observeViewModel(homeViewModel)

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun observeViewModel(homeViewModel: HomeViewModel) {
        val upcomingEventViewAdapter = EventViewAdapter(this)
        binding.rvUpcomingEventList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvUpcomingEventList.adapter = upcomingEventViewAdapter

        homeViewModel.loadStateUpcomingEvents.observe(viewLifecycleOwner) { loadState ->
            upcomingEventViewAdapter.viewState = loadState
            binding.rvUpcomingEventList.adapter = upcomingEventViewAdapter
            upcomingEventViewAdapter.notifyDataSetChanged()
            if (loadState == ViewState.Failed) {
                setUpcomingErrorRetry(View.VISIBLE)
            }
        }

        homeViewModel.upcomingEvents.observe(viewLifecycleOwner) { listEvents ->
            if (listEvents != null) {
                upcomingEventList = listEvents
                upcomingEventViewAdapter.eventList = upcomingEventList
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

    private fun setUpcomingErrorRetry(visibility: Int) {
        if (visibility == View.VISIBLE) {
            binding.rvUpcomingEventList.visibility = View.INVISIBLE
            binding.errorUpcomingRetry.root.visibility = View.VISIBLE
            binding.errorUpcomingRetry.btnRetryConnection.setOnClickListener {
                setUpcomingErrorRetry(View.INVISIBLE)
                homeViewModel.fetchUpcomingEvents()
            }
        } else if (visibility == View.INVISIBLE) {
            binding.rvUpcomingEventList.visibility = View.VISIBLE
            binding.errorUpcomingRetry.root.visibility = View.INVISIBLE
        }
    }
}