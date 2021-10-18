package com.demo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.demo.model.MenuEntity.Companion.MENU_TABLE_NAME
import com.squareup.moshi.Moshi

/**
 * Data class for Database entity and Serialization.
 */


@Entity(
    tableName = MENU_TABLE_NAME,
)
data class MenuEntity(
    @PrimaryKey
    val item: Menu
) {
    companion object {
        const val MENU_TABLE_NAME = "menuentity"
    }
}


class MenuConverters {
    @TypeConverter
    fun fromMenu(value: Menu?): String? {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Menu::class.java)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun stringToMenu(str: String?): Menu? {
        val moshi: Moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(Menu::class.java)
        return str?.let {
            adapter.fromJson(str)
        }
    }
}


//endregion
