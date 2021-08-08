package com.azatberdimyradov.weather.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.azatberdimyradov.weather.data.remote.models.Hourly
import com.azatberdimyradov.weather.databinding.ItemHourlyWeatherBinding
import com.azatberdimyradov.weather.other.unixToTime
import com.bumptech.glide.RequestManager
import javax.inject.Inject

class HourlyWeatherAdapter @Inject constructor():
    RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder>() {

    private val diffCallBack = object : DiffUtil.ItemCallback<Hourly>(){
        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }
    }

    var differ = AsyncListDiffer(this, diffCallBack)

    var hourlyItems: List<Hourly>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherViewHolder {
        return HourlyWeatherViewHolder(
            ItemHourlyWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: HourlyWeatherViewHolder, position: Int) {
        val currentHourlyItem = hourlyItems[position]
        holder.binding.apply {
            tvTemp.text = "${currentHourlyItem.temp}\u2103"
            tvHour.text = currentHourlyItem.dt.unixToTime()
        }
    }

    override fun getItemCount(): Int {
        return hourlyItems.size
    }

    inner class HourlyWeatherViewHolder(val binding: ItemHourlyWeatherBinding) :
        RecyclerView.ViewHolder(binding.root)

}