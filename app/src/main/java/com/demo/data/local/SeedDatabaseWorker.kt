package com.demo.data.local

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.demo.model.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

class SeedDatabaseWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getInstance(applicationContext)
            val restaurantList = getRestaurantsFromAsset()
            database.getRestaurantDao().add(restaurantList.map { RestaurantEntity(it) })

            val menuList = getMenus()
            database.getMenuDao().add(menuList.items.map { MenuEntity(it) })
            Result.success()

        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    private fun getRestaurantsFromAsset(): List<Restaurant> {
        return getRestaurants().items
    }

    private fun getRestaurants(): Restaurants {
        return parse(getJSONData(RESTAURANTS_JSON))
    }

    private fun getMenus(): Menus {
        return parse(getJSONData(MENUS_JSON))
    }

    private inline fun <reified T> parse(rawJson: String): T {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(T::class.java)
        return adapter.fromJson(rawJson)
            ?: throw IllegalStateException("failed parsing ${T::class.java.simpleName}")
    }

    private fun getJSONData(fileName: String): String {
        val inputStream = getInputStreamForJsonFile(fileName)
        return inputStream.bufferedReader().use { it.readText() }
    }

    @Throws(IOException::class)
    private fun getInputStreamForJsonFile(fileName: String): InputStream {
        return context.assets.open(fileName)
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
        private const val RESTAURANTS_JSON = "restaurants.json"
        private const val MENUS_JSON = "menus.json"

    }
}
