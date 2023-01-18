package com.example.delivr.categoryviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivr.MainActivity
import com.example.delivr.ProductOrderAdapter
import com.example.delivr.R

class CategoryViewerFragment : Fragment() {
    private val viewModel: CategoryViewerViewModel by viewModels()
    private val args: CategoryViewerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setActivityTitle(args.categoryName)
        viewModel.setCategoryId(args.categoryId)

        val view = inflater.inflate(R.layout.fragment_category_viewer, container, false)
        val loadingOverlay = view.findViewById<View>(R.id.loading_overlay)
        val recyclerView = view.findViewById<RecyclerView>(R.id.categoryViewerRecyclerView)
        val adapter = ProductOrderAdapter(onQuantityChanged = { product, quantity ->
            viewModel.onProductQuantityChanged(product, quantity)
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        viewModel.isLoading.observe(viewLifecycleOwner) {
            loadingOverlay.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.products.observe(viewLifecycleOwner) {
            adapter.products = it
        }

        viewModel.cartSize.observe(viewLifecycleOwner) {
            setCartSizeBadge(it)
        }

        return view
    }

    private fun setActivityTitle(title: String) {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = title
    }

    private fun setCartSizeBadge(size: Int) {
        (requireActivity() as MainActivity).setCartSizeBadge(size)
    }
}