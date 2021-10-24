package com.demo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.data.repository.RestaurantRepository
import com.demo.model.Menu
import com.demo.model.Restaurant
import com.demo.model.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [MainActivity]
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _restaurants: MutableStateFlow<List<Restaurant>> = MutableStateFlow(emptyList())
    private val _menus: MutableStateFlow<State<List<Menu>>> = MutableStateFlow(State.loading())
    private val _searchedRestaurants: MutableStateFlow<Map<String, List<Restaurant>>> =
        MutableStateFlow(emptyMap())

    private val _menuForRest: MutableStateFlow<Menu?> = MutableStateFlow(null)


    val restaurants: StateFlow<List<Restaurant>> = _restaurants
    val menuForRest: StateFlow<Menu?> = _menuForRest
    val searchedRestaurants: StateFlow<Map<String, List<Restaurant>>> = _searchedRestaurants

    fun getRestaurants() {
        clear()
        _searchedRestaurants.value = emptyMap()
        viewModelScope.launch {
            restaurantRepository.getAllRestaurants()
                .collect {
                    _restaurants.value = it
                }
        }
    }

    fun getSearchResult(text: String) {
        clear()
        viewModelScope.launch {
            restaurantRepository.getRestaurantConsolidatedResult(text)
                .collect { state: Map<String, List<Restaurant>> ->
                    _searchedRestaurants.value = state
                }
        }
    }

    fun getMenus(restId: Long) {
        clear()
        _menuForRest.value = null
        viewModelScope.launch {
            restaurantRepository.getMenuById(restId)
                .collect { state -> _menuForRest.value = state }
        }
    }

    private fun clear() {
        _restaurants.value = emptyList()
        _menus.value = State.loading()
        _searchedRestaurants.value = emptyMap()
        _menuForRest.value = null
    }
}
