package com.kuroakevizago.dicodingapp.ui.favorite

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kuroakevizago.dicodingapp.adapter.EventViewAdapter
import com.kuroakevizago.dicodingapp.data.display.IDisplayableItem
import com.kuroakevizago.dicodingapp.data.local.entity.FavoriteEntity
import com.kuroakevizago.dicodingapp.databinding.FragmentFavoriteBinding
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.helper.ViewModelFactory
import com.kuroakevizago.dicodingapp.interfaces.OnItemClickListener
import com.kuroakevizago.dicodingapp.ui.detail.DetailActivity

class FavoriteFragment : Fragment(), OnItemClickListener<List<IDisplayableItem?>> {

    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favoriteBinding : FragmentFavoriteBinding

    private lateinit var viewAdapter: EventViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelFactory = ViewModelFactory.getInstance(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory)[FavoriteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        favoriteBinding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)

        observeViewModel()

        return favoriteBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun observeViewModel() {
        viewAdapter = EventViewAdapter(this)
        favoriteBinding.rvFavoriteEventList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        favoriteBinding.rvFavoriteEventList.adapter = viewAdapter

        viewModel.fetchStateAllFavorites.observe(viewLifecycleOwner) { loadState ->
            viewAdapter.viewState = loadState
            favoriteBinding.rvFavoriteEventList.adapter = viewAdapter
            viewAdapter.notifyDataSetChanged()

            if (loadState == ViewState.Failed) {
                setUpcomingErrorRetry(View.VISIBLE)
            }
        }

        viewModel.getAllFavorites().observe(viewLifecycleOwner) { fetchFavoriteEntities(it) }
    }

    private fun setUpcomingErrorRetry(visibility: Int) {
        if (visibility == View.VISIBLE) {
            favoriteBinding.rvFavoriteEventList.visibility = View.INVISIBLE
            favoriteBinding.errorFavoriteRetry.root.visibility = View.VISIBLE
            favoriteBinding.errorFavoriteRetry.btnRetryConnection.setOnClickListener {
                setUpcomingErrorRetry(View.INVISIBLE)
                viewModel.getAllFavorites().observe(viewLifecycleOwner) { fetchFavoriteEntities(it) }
            }
        } else if (visibility == View.INVISIBLE) {
            favoriteBinding.rvFavoriteEventList.visibility = View.VISIBLE
            favoriteBinding.errorFavoriteRetry.root.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun fetchFavoriteEntities(favoriteEntities : List<FavoriteEntity>) {
        viewAdapter.eventList = favoriteEntities
        viewAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(position: Int, itemList: List<IDisplayableItem?>) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(
            "event_id",
            itemList[position]?.id
        )
        startActivity(intent)
    }
}