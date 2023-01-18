package com.example.delivr.categoryselector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delivr.Category
import com.example.delivr.Repository
import kotlinx.coroutines.launch

data class CategoryNavData(val category: Category)

class CategorySelectorViewModel : ViewModel() {
    private val _data = MutableLiveData<List<Category>>()
    val data: LiveData<List<Category>>
        get() = _data

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _eventNavigateToCategory = MutableLiveData<CategoryNavData?>(null)
    val eventNavigateToCategory: LiveData<CategoryNavData?>
        get() = _eventNavigateToCategory

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _data.value = Repository.getCategories()
            _isLoading.value = false
        }
    }

    fun onCategorySelected(category: Category) {
        _eventNavigateToCategory.value = CategoryNavData(category)
    }

    fun eventNavigateToCategoryHandled() {
        _eventNavigateToCategory.value = null
    }
}

