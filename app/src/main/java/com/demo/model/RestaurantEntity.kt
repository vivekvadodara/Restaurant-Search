package com.demo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.demo.model.RestaurantEntity.Companion.RESTAURANT_TABLE_NAME
import com.squareup.moshi.Moshi

/**
 * Data class for Database entity and Serialization.
 */


@Entity(
    tableName = RESTAURANT_TABLE_NAME,
)
data class RestaurantEntity(
    @PrimaryKey
    val item: Restaurant
) {
    companion object {
        const val RESTAURANT_TABLE_NAME = "restaurantentity"
    }
}


class Converters {
    @TypeConverter
    fun fromRestaurant(value: Restaurant?): String? {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Restaurant::class.java)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun stringToRestaurant(str: String?): Restaurant? {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Restaurant::class.java)
        return str?.let {
            adapter.fromJson(str)
        }
    }
}


//endregion
