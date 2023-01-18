package com.example.delivr

import java.math.BigDecimal


data class Product(val id: String, val categoryId: String, val name: String, val weightG: Int, val price: BigDecimal, val image: ImageResource)

data class ProductOrder(val data: Product, var quantity: Int = 0) {
    override fun hashCode(): Int {
        return data.id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return null != other && other is ProductOrder && data.id == other.data.id
    }
}

data class Category(val id: String, val name: String, val imageRes: ImageResource)

data class User(val name: String, val address: String, val tel: String, val email: String)
