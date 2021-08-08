package com.azatberdimyradov.weather.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.LinearLayoutManager
import com.azatberdimyradov.weather.R
import com.azatberdimyradov.weather.adapters.DailyWeatherAdapter
import com.azatberdimyradov.weather.adapters.HourlyWeatherAdapter
import com.azatberdimyradov.weather.adapters.SearchResultAdapter
import com.azatberdimyradov.weather.data.remote.models.WeatherResponse
import com.azatberdimyradov.weather.databinding.FragmentWeatherBinding
import com.azatberdimyradov.weather.other.Constants
import com.azatberdimyradov.weather.other.Resource
import com.azatberdimyradov.weather.other.iconUrl
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_weather.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment @Inject constructor(
    private val dailyWeatherAdapter: DailyWeatherAdapter,
    private val hourlyWeatherAdapter: HourlyWeatherAdapter,
    private val searchResultAdapter: SearchResultAdapter,
    val glide: RequestManager,
    var viewModel: WeatherViewModel? = null
) : Fragment(R.layout.fragment_weather) {

    private lateinit var binding: FragmentWeatherBinding
    private lateinit var searchItem: MenuItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherBinding.bind(view)
        viewModel =
            viewModel ?: ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)
        subscribeToObserves()
        setupRecyclerView()

        searchResultAdapter.setOnItemClickListener { locationItem, oldItem ->
            viewModel?.searchForWeather(locationItem)
            if (!oldItem){
                viewModel?.insertLocationIntoDb(locationItem)
            }
        }

        searchResultAdapter.setOnItemDeleteListener { locationItem ->
            viewModel?.deleteLocationFromDb(locationItem)
        }


        setHasOptionsMenu(true)
    }

    private fun subscribeToObserves() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel?.weatherResponse?.collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                setupWeatherInfo(it)
                            }
                        }
                        is Resource.Error -> showError(result.message)
                        is Resource.Loading -> onLoading()
                        is Resource.Empty -> { }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel?.locationResponse?.collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                searchResultAdapter.searchQueryState = Constants.SEARCH_QUERY_IS_NOT_EMPTY
                                searchResultAdapter.notifyDataSetChanged()
                                searchResultAdapter.submitList(it)
                            }
                        }
                        is Resource.Error -> showError(result.message)
                        is Resource.Loading -> { }
                        is Resource.Empty -> onLocationResponseEmpty()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel?.currentLocation?.collect {
                    binding.tvCityName.text = it.sub ?: it.cityName
                }
            }
        }
    }

    private fun onLoading() {
        binding.apply {
            progressBar.isVisible = true
            constraintLayout.isVisible = false
        }
    }

    private fun onSuccess(){
        binding.apply {
            progressBar.isVisible = false
            constraintLayout.isVisible = true
        }
    }

    private fun onLocationResponseEmpty() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.whenStarted {
                viewModel?.observeAllLocations?.collect {
                    searchResultAdapter.searchQueryState = Constants.SEARCH_QUERY_IS_EMPTY
                    searchResultAdapter.submitList(it)
                }
            }
        }
    }

    private fun showError(message: String?) {
        Snackbar.make(
            requireActivity().rootLayout,
            message ?: "An unknown error occurred",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setupWeatherInfo(it: WeatherResponse) {
        binding.apply {
            val tempInString = it.current.temp.toString()
            tvTemp.text = "${tempInString.substring(0,2)}\u2103"
            glide.load(it.current.weather[0].icon.iconUrl()).into(ivCloudImage)
            tvNE.text = "${it.current.wind_speed} m/s"
            tvUvIndex.text = it.current.uvi.toString()
            tvReelFeel.text = "${it.current.feels_like}\u2103"
            tvPressure.text = "${it.current.pressure} mb"
            dailyWeatherAdapter.notifyDataSetChanged()
            hourlyWeatherAdapter.notifyDataSetChanged()
            dailyWeatherAdapter.submitList(it.daily)
            hourlyWeatherAdapter.hourlyItems = it.hourly
        }
        onSuccess()
        searchItem.collapseActionView()
    }

    private fun setupRecyclerView() {
        binding.apply {
            rvDailyWeather.apply {
                adapter = dailyWeatherAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            rvHourlyWeather.apply {
                adapter = hourlyWeatherAdapter
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                setHasFixedSize(true)
            }
            rvSearchResult.apply {
                adapter = searchResultAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                binding.rvSearchResult.isVisible = true
                binding.rootLayout.fullScroll(ScrollView.FOCUS_UP)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                binding.rvSearchResult.isVisible = false
                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel?.searchForLocation(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.equals("")){
                    viewModel?.searchForLocation("")
                }
                return true
            }
        })
    }
}