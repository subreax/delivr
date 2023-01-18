package com.example.delivr

import retrofit2.http.Body
import retrofit2.http.POST

data class ProductOrderDto(val name: String, val price: Double, val quantity: Int, val imageUrl: String)
data class Order(val name: String, val address: String, val tel: String, val email: String, val cart: List<ProductOrderDto>)


interface RetrofitApi {
    @POST("/email/delivery")
    suspend fun makeOrder(@Body order: Order)
}
