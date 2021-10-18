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
import kotlinx.coroutines.flow.map
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

    private val _restaurants: MutableStateFlow<State<List<Restaurant>>> =
        MutableStateFlow(State.loading())
    private val _menus: MutableStateFlow<State<List<Menu>>> = MutableStateFlow(State.loading())
    private val _searchedRestaurants: MutableStateFlow<Map<String, List<Restaurant>>> =
        MutableStateFlow(emptyMap())

    private val _menuForRest: MutableStateFlow<Menu?> = MutableStateFlow(null)


    val restaurants: StateFlow<State<List<Restaurant>>> = _restaurants
    val menus: StateFlow<State<List<Menu>>> = _menus
    val menuForRest: StateFlow<Menu?> = _menuForRest
    val searchedRestaurants: StateFlow<Map<String, List<Restaurant>>> = _searchedRestaurants

    fun getRestaurants() {
        clear()
        _searchedRestaurants.value = emptyMap()
        viewModelScope.launch {
            restaurantRepository.getAllRestaurants()
                .map { resource -> State.fromResource(resource) }
                .collect { state -> _restaurants.value = state }
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

    fun getMenus() {
        clear()
        viewModelScope.launch {
            restaurantRepository.getAllMenus()
                .map { resource -> State.fromResource(resource) }
                .collect { state -> _menus.value = state }
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
        _restaurants.value = State.loading()
        _menus.value = State.loading()
        _searchedRestaurants.value = emptyMap()
        _menuForRest.value = null
    }
}
