package com.example.delivr

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.delivr.databinding.ProductItemBinding

class ProductOrderAdapter(private val onQuantityChanged: (ProductOrder, Int) -> Unit) : RecyclerView.Adapter<ProductOrderAdapter.ViewHolder>() {
    var products: List<ProductOrder> = emptyList()
    set(value) {
        field = value
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position]) { product, quantity ->
            onQuantityChanged(product, quantity)
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = products.size


    class ViewHolder private constructor(private val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ProductItemBinding.inflate(parent.inflater(), parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(product: ProductOrder, onQuantityChanged: (ProductOrder, Int) -> Unit) {
            binding.productItemTitle.text = product.data.name
            binding.productItemImage.setImageResource(product.data.image)
            binding.productItemWeight.text = product.data.weightG.asWeightString()
            binding.productItemQuantity.text = product.quantity.toString()
            binding.productItemPrice.text = product.data.price.asPriceString()

            binding.productItemBtnPlus.setOnClickListener {
                onQuantityChanged(product, product.quantity+1)
            }
            binding.productItemBtnMinus.setOnClickListener {
                if (product.quantity > 0)
                    onQuantityChanged(product, product.quantity-1)
            }
        }
    }
}