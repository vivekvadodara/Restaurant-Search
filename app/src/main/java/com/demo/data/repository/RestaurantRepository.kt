package com.demo.data.repository

import android.content.Context
import androidx.annotation.MainThread
import com.demo.data.local.dao.MenusDao
import com.demo.data.local.dao.RestaurantsDao
import com.demo.model.*
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.min

interface RestaurantRepository {
    fun getAllRestaurants(): Flow<Resource<List<Restaurant>>>
    fun getAllMenus(): Flow<Resource<List<Menu>>>
    fun getMenuById(restId: Long): Flow<Menu>
    suspend fun getRestaurantConsolidatedResult(text: String): Flow<Map<String, List<Restaurant>>>
}

/**
 * Singleton repository for fetching data from remote and storing it in database
 * for offline capability. This is Single source of data.
 */
@ExperimentalCoroutinesApi
class RestaurantRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restaurantsDao: RestaurantsDao,
    private val menuDao: MenusDao
) : RestaurantRepository {

    val moshi: Moshi = MoshiFactory.getInstance()

    val lazyRestaurantList = GlobalScope.async(
        Dispatchers.Unconfined,
        start = CoroutineStart.LAZY
    ) {
        getRestaurantsFromAsset()
    }


    val lazyMenuList = GlobalScope.async(
        Dispatchers.Unconfined,
        start = CoroutineStart.LAZY
    ) { getMenusFromAsset() }

    override fun getAllRestaurants(): Flow<Resource<List<Restaurant>>> {
        return object : NetworkBoundRepository<List<Restaurant>, List<Restaurant>>() {

            override suspend fun saveRemoteData(response: List<Restaurant>) =
                restaurantsDao.add(response.map { it.toEntity() })

            override fun fetchFromLocal(): Flow<List<Restaurant>> =
                restaurantsDao.getAll().map {
                    it.map { entity ->
                        entity.item
                    }
                }

            override suspend fun fetchFromRemote(): Response<List<Restaurant>> =
                lazyRestaurantList.await()
        }.asFlow()
    }

    override fun getAllMenus(): Flow<Resource<List<Menu>>> {
        return object : NetworkBoundRepository<List<Menu>, List<Menu>>() {

            override suspend fun saveRemoteData(response: List<Menu>) =
                menuDao.add(response.map { it.toEntity() })

            override fun fetchFromLocal(): Flow<List<Menu>> = menuDao.getAll().map {
                it.map { entity ->
                    entity.toModel()
                }
            }

            override suspend fun fetchFromRemote(): Response<List<Menu>> = lazyMenuList.await()
        }.asFlow()
    }

    @MainThread
    override fun getMenuById(restId: Long): Flow<Menu> {
        return menuDao.getAll().map {
            it.filter {
                it.item.restaurantId == restId
            }[0]
        }.map {
            it?.item ?: throw IllegalStateException("Menu not found")
        }
    }

    private suspend fun getRestaurantsFromAsset(): Response<List<Restaurant>> {
        return withContext(Dispatchers.IO) {
            val response: List<Restaurant> = getRestaurants().items
            Response.success(response)
        }
    }

    private suspend fun getMenusFromAsset(): Response<List<Menu>> {
        return withContext(Dispatchers.IO) {
            val response: List<Menu> = getMenus().items
            Response.success(response)
        }
    }


    @MainThread
    override suspend fun getRestaurantConsolidatedResult(text: String): Flow<Map<String, List<Restaurant>>> {

        val restaurantList: List<RestaurantEntity> = restaurantsDao.getAll().take(1).single()
        val menuList: List<MenuEntity> = menuDao.getAll().take(1).single()

        return flow<Map<String, List<Restaurant>>> {
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
            emit(result)
        }
    }


    private suspend fun getRestaurants(): Restaurants {
        return parse(getJSONData(RESTAURANTS_JSON))
    }

    private suspend fun getMenus(): Menus {
        return parse(getJSONData(MENUS_JSON))
    }

    private suspend inline fun <reified T> parse(rawJson: String): T {
        return withContext(Dispatchers.IO) {
            val adapter = moshi.adapter(T::class.java)
            adapter.fromJson(rawJson)
                ?: throw IllegalStateException("failed parsing ${T::class.java.simpleName}")
        }

    }

    private suspend fun getJSONData(fileName: String): String {
        return withContext(Dispatchers.IO) {
            val inputStream = getInputStreamForJsonFile(fileName)
            inputStream.bufferedReader().use { it.readText() }
        }

    }

    @Throws(IOException::class)
    private fun getInputStreamForJsonFile(fileName: String): InputStream {
        return context.assets.open(fileName)
    }


    companion object {
        private const val RESTAURANTS_JSON = "restaurants.json"
        private const val MENUS_JSON = "menus.json"
    }
}

private fun Restaurant.toEntity(): RestaurantEntity {
    return RestaurantEntity(
        item = this
    )
}

private fun RestaurantEntity.toModel(): Restaurant {
    return this.item
}

private fun Menu.toEntity(): MenuEntity {
    return MenuEntity(
        item = this
    )
}

private fun MenuEntity.toModel(): Menu {
    return this.item
}

internal object MoshiFactory {

    private val moshi: Moshi = Moshi.Builder().build()
    fun getInstance() = moshi
}
