package com.example.delivr.delivery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delivr.Repository
import com.example.delivr.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

enum class OrderStatus {
    NOT_ORDERED, IN_PROGRESS, SUCCESS, ERROR
}

class DeliveryViewModel : ViewModel() {
    private val _nameError = MutableLiveData<Boolean>(false)
    val nameError: LiveData<Boolean>
        get() = _nameError

    private val _addressError = MutableLiveData<Boolean>(false)
    val addressError: LiveData<Boolean>
        get() = _addressError

    private val _telError = MutableLiveData<Boolean>(false)
    val telError: LiveData<Boolean>
        get() = _telError

    private val _emailError = MutableLiveData<Boolean>(false)
    val emailError: LiveData<Boolean>
        get() = _emailError


    private val _user = MutableLiveData(Repository.getUser())
    val user: LiveData<User>
        get() = _user


    private val _orderStatus = MutableLiveData(OrderStatus.NOT_ORDERED)
    val orderStatus: LiveData<OrderStatus>
        get() = _orderStatus

    private val _eNavigateToMainScreen = MutableLiveData(false)
    val eNavigateToMainScreen: LiveData<Boolean>
        get() = _eNavigateToMainScreen


    private val _cartCost = MutableLiveData<BigDecimal>()
    val cartCost: LiveData<BigDecimal>
        get() = _cartCost

    init {
        viewModelScope.launch {
            _cartCost.value = Repository.calculateCartCost()
        }
    }

    fun makeOrder(name: String, address: String, tel: String, email: String) {
        _nameError.value = !isNameCorrect(name)
        _addressError.value = !isAddressCorrect(address)
        _telError.value = !isTelCorrect(tel)
        _emailError.value = !isEmailCorrect(email)

        if (!(_nameError.value!! || _addressError.value!! || _telError.value!! || _emailError.value!!)) {
            viewModelScope.launch {
                _orderStatus.value = OrderStatus.IN_PROGRESS

                try {
                    Repository.makeOrder(name, address, tel, email)
                    _orderStatus.value = OrderStatus.SUCCESS
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    _orderStatus.value = OrderStatus.ERROR
                }

                navigateToMainScreen()
            }
        }
    }

    private fun isNameCorrect(name: String): Boolean {
        return name.isNotBlank()
    }

    private fun isAddressCorrect(address: String): Boolean {
        return address.isNotBlank()
    }

    private fun isTelCorrect(tel: String): Boolean {
        var len = tel.length
        if (tel.startsWith('+'))
            len -= 1
        return len == 11
    }

    private fun isEmailCorrect(email: String): Boolean {
        val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
        return emailRegex.matches(email)
    }

    private fun navigateToMainScreen() {
        viewModelScope.launch {
            delay(3000L)
            _eNavigateToMainScreen.value = true
        }
    }

    fun eventNavigateToMainScreenHandled() {
        _eNavigateToMainScreen.value = false
    }
}