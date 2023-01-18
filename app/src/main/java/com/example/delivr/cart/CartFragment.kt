package com.example.delivr.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.delivr.MainActivity
import com.example.delivr.ProductOrderAdapter
import com.example.delivr.R
import com.example.delivr.asPriceString


class CartFragment : Fragment() {
    private val viewModel: CartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.loadCart()

        if (viewModel.cart.value!!.isEmpty()) {
            return inflater.inflate(R.layout.fragment_cart_is_empty, container, false)
        }

        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.cartRecyclerView)
        val adapter = ProductOrderAdapter(onQuantityChanged = { product, quantity ->
            viewModel.onProductQuantityChanged(product, quantity)
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter

        viewModel.cart.observe(viewLifecycleOwner) {
            if (it != null)
                adapter.products = it
        }

        val costText = view.findViewById<TextView>(R.id.cart_text_cost)
        viewModel.cost.observe(viewLifecycleOwner) {
            costText.text = it.asPriceString()
        }

        val orderBtn = view.findViewById<Button>(R.id.cart_btn_order)
        orderBtn.setOnClickListener {
            viewModel.orderClicked()
        }

        viewModel.eventNavigateToDelivery.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(
                    CartFragmentDirections.actionCartFragmentToDeliveryFragment()
                )
                viewModel.eventNavigateToDeliveryHandled()
            }
        }

        viewModel.cartSize.observe(viewLifecycleOwner) {
            (requireActivity() as MainActivity).setCartSizeBadge(it)

            orderBtn.isEnabled = it > 0
        }

        return view
    }
}