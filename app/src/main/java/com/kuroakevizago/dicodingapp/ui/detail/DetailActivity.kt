package com.kuroakevizago.dicodingapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kuroakevizago.dicodingapp.R
import com.kuroakevizago.dicodingapp.data.local.entity.FavoriteEntity
import com.kuroakevizago.dicodingapp.data.remote.response.Event
import com.kuroakevizago.dicodingapp.databinding.ActivityDetailBinding
import com.kuroakevizago.dicodingapp.enumeration.ViewState
import com.kuroakevizago.dicodingapp.helper.ViewModelFactory
import com.kuroakevizago.dicodingapp.ui.favorite.FavoriteViewModel

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel


    private lateinit var detailData: Event
    private lateinit var favoriteEntity: FavoriteEntity

    private var detailId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewModelFactory = ViewModelFactory.getInstance(application)
        detailViewModel = ViewModelProvider(this, viewModelFactory)[DetailViewModel::class.java]
        favoriteViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteViewModel::class.java]

        setupViewModel()
        observeViewModel()
        initFavoriteButton()

        binding.btnFavorite.setOnClickListener {
            //Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show()
            toggleFavorite()
        }
    }

    private fun setupViewModel() {
        val intent = this.intent
        val id = intent.getIntExtra("event_id", -1)
        detailId = id

        if (detailId != -1) {
            detailViewModel.fetchEventData(detailId)
        }

        detailViewModel.loadStateDetail.observe(this) { state ->
            if (state == ViewState.Loading) {
                setShimmerAnimation(true)
            }
            else {
                setShimmerAnimation(false)
            }

            if (state == ViewState.Failed) {
                setDetailErrorRetry(View.VISIBLE)
            }

        }
    }

    private fun observeViewModel() {
        detailViewModel.eventData.observe(this) { eventData ->
            if (eventData != null) {
                binding.tvEventTitle.text = eventData.name
                binding.tvEventOwner.text = eventData.ownerName
                binding.tvEventQuota.text = buildString {
                    append("Quota Left: ")
                    append((eventData.registrants?.let { eventData.quota?.minus(it) }).toString())
                }
                binding.tvEventTime.text = eventData.beginTime
                binding.tvEventDescription.text = HtmlCompat.fromHtml(
                    eventData.description.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                binding.btnOpenLink.setOnClickListener {
                    val linkIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(eventData.link)
                    }
                    startActivity(linkIntent)
                }
                Glide.with(this).load(eventData.mediaCover).into(binding.imgEventIcon)

                detailData = eventData
                favoriteEntity = FavoriteEntity(detailId, detailData.name, detailData.mediaCover, false)
                favoriteViewModel.isFavoriteExists(detailId).observe(this) {
                    favoriteEntity = FavoriteEntity(detailId, detailData.name, detailData.mediaCover, it)
                    initFavoriteButton()
                }
            }
        }
    }

    private fun initFavoriteButton() {
        if (!::favoriteEntity.isInitialized) return

        if (favoriteEntity.isFavorite!!) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_fill)
        }
        //Data Favorite Not exist previously
        else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_null)
        }
    }

    private fun toggleFavorite() {
        if (!::detailData.isInitialized || !::favoriteEntity.isInitialized) return

        //Data favorite is already exist
        if (favoriteEntity.isFavorite!!) {
            favoriteEntity.isFavorite = false
            favoriteViewModel.delete(favoriteEntity)
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_null)
        }
        //Data Favorite Not exist previously
        else {
            favoriteEntity.isFavorite = true
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_fill)
            favoriteViewModel.insert(favoriteEntity)
        }
    }

    private fun setShimmerAnimation(play: Boolean) {
        val detailLayout = binding.detailLayout
        val shimmerDetailLayout = binding.shimmerDetailLayout
        val shimmerDetail = binding.shimmerDetail
        val loadAnimation = AnimationUtils.loadAnimation(this, R.anim.shimmer_animation)

        shimmerDetail.apply {
            if (play) {
                detailLayout.visibility = View.INVISIBLE
                shimmerDetailLayout.visibility = View.VISIBLE
                shimmerTvEventTitle.startAnimation(loadAnimation)
                shimmerBtnOpenLink.startAnimation(loadAnimation)
                shimmerTvEventOwner.startAnimation(loadAnimation)
                shimmerTvEventQuota.startAnimation(loadAnimation)
                shimmerTvEventDescription.startAnimation(loadAnimation)
                shimmerImgEventIcon.startAnimation(loadAnimation)
                shimmerTvEventTime.startAnimation(loadAnimation)
            } else {
                detailLayout.visibility = View.VISIBLE
                binding.shimmerDetailLayout.visibility = View.INVISIBLE
                shimmerTvEventTitle.clearAnimation()
                shimmerBtnOpenLink.clearAnimation()
                shimmerTvEventOwner.clearAnimation()
                shimmerTvEventQuota.clearAnimation()
                shimmerTvEventDescription.clearAnimation()
                shimmerImgEventIcon.clearAnimation()
                shimmerTvEventTime.clearAnimation()
            }
        }
    }

    private fun setDetailErrorRetry(visibility: Int) {
        if (visibility == View.VISIBLE) {
            binding.detailLayout.visibility = View.INVISIBLE
            binding.errorDetailLayout.visibility = View.VISIBLE

            binding.errorDetailRetry.btnRetryConnection.setOnClickListener {
                setDetailErrorRetry(View.INVISIBLE)
                detailViewModel.fetchEventData(detailId)
            }
        } else if (visibility == View.INVISIBLE) {
            binding.detailLayout.visibility = View.VISIBLE
            binding.errorDetailLayout.visibility = View.INVISIBLE
        }
    }
}