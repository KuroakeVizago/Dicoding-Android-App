package com.kuroakevizago.dicodingapp.data.remote.response

import com.google.gson.annotations.SerializedName
import com.kuroakevizago.dicodingapp.data.display.IDisplayableItem

data class DicodingEventListResponse(

    @field:SerializedName("listEvents")
	val listEvents: List<ListEventsItem?>? = null,

    @field:SerializedName("error")
	val error: Boolean? = null,

    @field:SerializedName("message")
	val message: String? = null
)

data class ListEventsItem(

	@field:SerializedName("summary")
	val summary: String? = null,

	@field:SerializedName("mediaCover")
	override var mediaCover: String? = null,

	@field:SerializedName("registrants")
	val registrants: Int? = null,

	@field:SerializedName("imageLogo")
	val imageLogo: String? = null,

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("ownerName")
	val ownerName: String? = null,

	@field:SerializedName("cityName")
	val cityName: String? = null,

	@field:SerializedName("quota")
	val quota: Int? = null,

	@field:SerializedName("name")
	override var name: String? = null,

	@field:SerializedName("id")
	override val id: Int? = null,

	@field:SerializedName("beginTime")
	val beginTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null,

	@field:SerializedName("category")
	val category: String? = null
) : IDisplayableItem
