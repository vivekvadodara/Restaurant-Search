package com.demo.data.repository

import com.demo.data.local.dao.MenusDao
import com.demo.data.local.dao.RestaurantsDao
import com.demo.model.Menu
import com.demo.model.MenuEntity
import com.demo.model.Restaurant
import com.demo.model.RestaurantEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.min

interface RestaurantRepository {
    fun getAllRestaurants(): Flow<List<Restaurant>>
    fun getMenuById(restId: Long): Flow<Menu>
    suspend fun getRestaurantConsolidatedResult(text: String): Flow<Map<String, List<Restaurant>>>
}

/**
 * Singleton repository for fetching data from remote and storing it in database
 * for offline capability. This is Single source of data.
 */
@ExperimentalCoroutinesApi
class RestaurantRepositoryImpl @Inject constructor(
    private val restaurantsDao: RestaurantsDao,
    private val menuDao: MenusDao
) : RestaurantRepository {

    override fun getAllRestaurants(): Flow<List<Restaurant>> {
        return restaurantsDao.getAll().map {
            it.map { entity ->
                entity.item
            }
        }
    }

    override fun getMenuById(restId: Long): Flow<Menu> {
        return menuDao.getAll().map {
            it.filter {
                it.item.restaurantId == restId
            }[0]
        }.map {
            it.item
        }
    }

    override suspend fun getRestaurantConsolidatedResult(text: String): Flow<Map<String, List<Restaurant>>> {

        return restaurantsDao.getAll().combine(menuDao.getAll()) { restaurantList, menuList ->
            val result: MutableMap<String, MutableList<Restaurant>> = mutableMapOf()
            restaurantList.forEach {
                if (it.item.name.contains(text, true)) {
                    result["NAME"] =
                        result["NAME"]?.plus(it.item)?.toMutableList() ?: mutableListOf(it.item)
                }
            }


            restaurantList.forEach {
                if (it.item.cuisineType.contains(text, true)) {
                    result["CUISINE"] =
                        result["CUISINE"]?.plus(it.item)?.toMutableList() ?: mutableListOf(it.item)
                }
            }

            restaurantList.forEach {
                if (it.item.address.contains(text, true)) {
                    result["ADDRESS"] =
                        result["ADDRESS"]?.plus(it.item)?.toMutableList() ?: mutableListOf(it.item)
                }
            }

            menuList.forEach { menu ->
                if (menu.item.toString().contains(text, true)) {
                    restaurantList.forEach { rest ->
                        if (menu.item.restaurantId == rest.item.id) {
                            val menuStr = menu.item.toString()
                            val start = menuStr.toUpperCase().indexOf(text.toUpperCase())
                            val end = min(start + 100, menuStr.length - 1)
                            val matchedMenuHighlight = menuStr.substring(start, end)
                            result["MENU"] = result["MENU"]?.plus(
                                rest.item.copy(
                                    matchMenu = matchedMenuHighlight
                                )
                            )?.toMutableList()
                                ?: mutableListOf(rest.item.copy(matchMenu = matchedMenuHighlight))
                        }
                    }
                }
            }
            return@combine result
        }
    }
}