package com.azatberdimyradov.weather.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azatberdimyradov.weather.data.remote.models.Daily
import com.azatberdimyradov.weather.databinding.ItemDailyWeatherBinding
import com.azatberdimyradov.weather.other.iconUrl
import com.azatberdimyradov.weather.other.unixToDay
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class DailyWeatherAdapter @Inject constructor(
    private val glide: RequestManager
) : ListAdapter<Daily,DailyWeatherAdapter.DailyWeatherViewHolder>(DifferCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        return DailyWeatherViewHolder(
            ItemDailyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val currentDailyItem = getItem(position)
        holder.binding.apply {
            tvDay.text = currentDailyItem.dt.unixToDay()
            tvCloud.text = currentDailyItem.weather[0].main
            tvTemp.text = "${currentDailyItem.temp.day}\u2103"
            glide.load(currentDailyItem.weather[0].icon.iconUrl()).into(ivCloud)
        }
    }
    companion object{
        val DifferCallBack = object : DiffUtil.ItemCallback<Daily>() {
            override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class DailyWeatherViewHolder(val binding: ItemDailyWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)

}