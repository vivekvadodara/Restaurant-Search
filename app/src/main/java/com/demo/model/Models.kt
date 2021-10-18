package com.demo.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//region Restaurants
@JsonClass(generateAdapter = true)
data class Restaurants(
    @Json(name = "restaurants")
    val items: List<Restaurant>
)

@JsonClass(generateAdapter = true)
data class Restaurant(

    @Transient
    val match: String = "" ,

    @Transient
    val matchMenu: String = "" ,

    @Transient
    val menu: Menu? = null ,

    @Json(name = "id")
    val id: Long,

    @Json(name = "name")
    val name: String,

    @Json(name = "neighborhood")
    val neighborhood: String,

    @Json(name = "photograph")
    val photograph: String,

    @Json(name = "address")
    val address: String,

    @Json(name = "latlng")
    val locationPoints: LocationPoints,

    @Json(name = "cuisine_type")
    val cuisineType: String,

    @Json(name = "operating_hours")
    val operatingHours: Map<String, String>,

    @Json(name = "reviews")
    val reviews: List<Review>
)

@JsonClass(generateAdapter = true)
class Review(
    @Json(name = "name")
    val name: String,

    @Json(name = "date")
    val date: String,

    @Json(name = "rating")
    val rating: Double,

    @Json(name = "comments")
    val comments: String
)

@JsonClass(generateAdapter = true)
class LocationPoints(

    @Json(name = "lat")
    val latitude: Double,

    @Json(name = "lng")
    val longitude: Double
)
//endregion

//region Menus
@JsonClass(generateAdapter = true)
data class Menus(
    @Json(name = "menus")
    val items: List<Menu>
)

@JsonClass(generateAdapter = true)
data class Menu(
    @Json(name = "restaurantId")
    val restaurantId: Long,

    @Json(name = "categories")
    val categories: List<Category>
)

@JsonClass(generateAdapter = true)
data class Category(
    @Json(name = "id")
    val id: Long,

    @Json(name = "name")
    val name: String,

    @Json(name = "menu-items")
    val menuList: List<MenuItem>
)

@JsonClass(generateAdapter = true)
data class MenuItem(
    @Json(name = "id")
    val id: Long,

    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String,

    @Json(name = "price")
    val price: Double,

    @Json(name = "images")
    val images: List<String> = emptyList()
)

//endregion


