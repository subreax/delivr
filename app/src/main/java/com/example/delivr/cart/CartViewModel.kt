package com.example.delivr.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delivr.ProductOrder
import com.example.delivr.Repository
import kotlinx.coroutines.launch
import java.math.BigDecimal

class CartViewModel : ViewModel() {
    private val _cart = MutableLiveData<List<ProductOrder>>(emptyList())
    val cart: LiveData<List<ProductOrder>>
        get() = _cart

    private val _cartSize = MutableLiveData(0)
    val cartSize: LiveData<Int>
        get() = _cartSize

    private val _cost = MutableLiveData<BigDecimal>()
    val cost: LiveData<BigDecimal>
        get() = _cost

    private val _eventNavigateToDelivery = MutableLiveData(false)
    val eventNavigateToDelivery: LiveData<Boolean>
        get() = _eventNavigateToDelivery

    fun loadCart() {
        _cart.value = Repository.getCart().toList()
        _cost.value = calculateCost()
        _cartSize.value = Repository.getCartSize()
    }

    fun onProductQuantityChanged(productOrder: ProductOrder, quantity: Int) {
        productOrder.quantity = quantity
        viewModelScope.launch {
            if (quantity > 0) {
                Repository.addToCart(productOrder)
            } else {
                Repository.removeFromCart(productOrder)
            }
            _cost.value = calculateCost()
            _cartSize.value = Repository.getCartSize()
        }
    }

    private fun calculateCost(): BigDecimal {
        return Repository.calculateCartCost()
    }

    fun orderClicked() {
        _eventNavigateToDelivery.value = true
    }

    fun eventNavigateToDeliveryHandled() {
        _eventNavigateToDelivery.value = false
    }
}
