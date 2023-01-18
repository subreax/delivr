package com.example.delivr.categoryviewer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delivr.Product
import com.example.delivr.ProductOrder
import com.example.delivr.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class CategoryViewerViewModel : ViewModel() {
    private val _products = MutableLiveData<List<ProductOrder>>()
    val products: LiveData<List<ProductOrder>>
        get() = _products

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _cartSize = MutableLiveData(0)
    val cartSize: LiveData<Int>
        get() = _cartSize

    private var categoryId: String = ""

    fun setCategoryId(id: String) {
        val shouldLoadData = id != categoryId
        categoryId = id
        if (shouldLoadData)
            loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true

            val data = Repository.getProductsForCategory(categoryId)
            val cart = Repository.getCart()

            _products.value = ArrayList<ProductOrder>().apply {
                for (product in data) {
                    val cartProduct = cart.find { it.data.id == product.id }
                    if (cartProduct != null) {
                        add(cartProduct)
                    } else {
                        add(ProductOrder(product, 0))
                    }
                }
                _cartSize.value = Repository.getCartSize()
            }
            _isLoading.value = false
        }
    }

    fun onProductQuantityChanged(product: ProductOrder, quantity: Int) {
        product.quantity = quantity

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (product.quantity > 0) {
                    Repository.addToCart(product)
                }
                else {
                    Repository.removeFromCart(product)
                }
            }
            _cartSize.value = Repository.getCartSize()
        }
    }
}
