package com.kuroakevizago.dicodingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kuroakevizago.dicodingapp.R
import com.kuroakevizago.dicodingapp.data.display.IDisplayableItem
import com.kuroakevizago.dicodingapp.databinding.CardViewCarouselBinding
import com.kuroakevizago.dicodingapp.databinding.CardViewCarouselShimmerBinding
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.interfaces.OnItemClickListener

class EventViewAdapterCarousel(
    private val cardListener : OnItemClickListener<List<IDisplayableItem?>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var eventList: List<IDisplayableItem?>? = null
    var viewState: ViewState = ViewState.Loading

    inner class ViewHolder(val viewBinding: CardViewCarouselBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        init {
            viewBinding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    cardListener.onItemClick(position, eventList!!)
                }
            }
        }
    }

    inner class ShimmerViewHolder(private val viewBinding: CardViewCarouselShimmerBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun startShimmer() {
            // Apply shimmer animation to the views
            viewBinding.shimmerCardName.startAnimation(
                AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.shimmer_animation
                )
            )
            viewBinding.shimmerCardImg.startAnimation(
                AnimationUtils.loadAnimation(
                    itemView.context,
                    R.anim.shimmer_animation
                )
            )
        }

        fun stopShimmer() {
            viewBinding.shimmerCardName.clearAnimation()
            viewBinding.shimmerCardImg.clearAnimation()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewState) {
            ViewState.Loaded -> {
                val cardViewBinding =
                    CardViewCarouselBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                ViewHolder(cardViewBinding)
            }
            else -> {
                val shimmerBinding = CardViewCarouselShimmerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ShimmerViewHolder(shimmerBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            if (viewState == ViewState.Loaded && eventList != null) {
                val eventData = eventList!![position]
                holder.viewBinding.tvCardName.text = eventData?.name
                Glide.with(holder.itemView.context).load(eventData?.mediaCover)
                    .into(holder.viewBinding.imgCard)
            }
        }
        else if (holder is ShimmerViewHolder){
            if (viewState == ViewState.Loaded || viewState == ViewState.Failed)
                holder.stopShimmer()
            else
                holder.startShimmer()
        }
    }

    //Provide default holder count initially when loading
    override fun getItemCount(): Int {
        return eventList?.size ?: 2
    }
}