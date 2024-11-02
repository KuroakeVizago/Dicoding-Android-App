package com.kuroakevizago.dicodingapp.interfaces

interface OnItemClickListener<T> {
    fun onItemClick(position: Int, itemList: T)
}