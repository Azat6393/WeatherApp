package com.azatberdimyradov.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azatberdimyradov.weather.data.locale.LocationItem
import com.azatberdimyradov.weather.databinding.ItemSearchResultBinding
import com.azatberdimyradov.weather.other.Constants
import com.azatberdimyradov.weather.other.Constants.SEARCH_QUERY_IS_EMPTY
import com.azatberdimyradov.weather.other.Constants.SEARCH_QUERY_IS_NOT_EMPTY
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class SearchResultAdapter @Inject constructor ()
    : ListAdapter<LocationItem,SearchResultAdapter.SearchResultViewHolder>(DiffCallBack) {

    companion object {
        val DiffCallBack = object : DiffUtil.ItemCallback<LocationItem>() {
            override fun areItemsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    var searchQueryState = SEARCH_QUERY_IS_EMPTY

    private var onItemClickListener: ((LocationItem, Boolean) -> Unit)? = null

    private var onItemDeleteListener: ((LocationItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (LocationItem, Boolean) -> Unit){
        onItemClickListener = listener
    }

    fun setOnItemDeleteListener(listener: (LocationItem) -> Unit) {
        onItemDeleteListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
            ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val currentLocationItem = getItem(position)
        println(currentLocationItem)
        holder.binding.apply {
            tvLocationName.text = "${currentLocationItem.sub ?: ""} " +
                    "${currentLocationItem.cityName} " +
                    currentLocationItem.countyName
            when (searchQueryState) {
                SEARCH_QUERY_IS_NOT_EMPTY -> imDelete.visibility = View.INVISIBLE
                else -> imDelete.visibility = View.VISIBLE
            }
            tvLocationName.setOnClickListener {
                onItemClickListener?.let { click ->
                    click(currentLocationItem, imDelete.isVisible)
                }
            }
            imDelete.setOnClickListener {
                onItemDeleteListener?.let { click ->
                    click(currentLocationItem)
                }
            }
        }
    }

    inner class SearchResultViewHolder(val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root)

}