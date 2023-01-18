package com.example.delivr.categoryselector

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivr.R

class CategorySelectorFragment : Fragment() {
    private val viewModel: CategorySelectorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_selector, container, false)
        val loadingOverlay = view.findViewById<View>(R.id.loading_overlay)
        val recyclerView = view.findViewById<RecyclerView>(R.id.categoryRecyclerView)
        val adapter = CategorySelectorAdapter(onCategoryClicked = {
            viewModel.onCategorySelected(it)
        })
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        viewModel.eventNavigateToCategory.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                findNavController().navigate(
                    CategorySelectorFragmentDirections.actionCategorySelectorFragmentToCategoryViewerFragment(
                        categoryName = data.category.name,
                        categoryId = data.category.id
                    )
                )
                viewModel.eventNavigateToCategoryHandled()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            loadingOverlay.visibility = if (it) View.VISIBLE else View.GONE
        }

        return view
    }
}