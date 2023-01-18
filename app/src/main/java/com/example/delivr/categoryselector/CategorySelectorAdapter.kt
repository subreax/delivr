package com.example.delivr.categoryselector

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.delivr.*
import com.example.delivr.databinding.CategoryListItemBinding

private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_CONTENT = 1

class CategorySelectorAdapter(private val onCategoryClicked: (Category) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: List<Category> = emptyList()


    fun setData(data: List<Category>) {
        this.data = data
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_CONTENT -> ItemViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_CONTENT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewHolder -> {
                holder.bind(data[position-1], onCategoryClicked)
            }
        }
    }

    override fun getItemCount(): Int = data.size + 1



    class HeaderViewHolder private constructor(view: View) : RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val v = parent.inflate(R.layout.category_list_header)
                return HeaderViewHolder(v)
            }
        }
    }

    class ItemViewHolder private constructor(val binding: CategoryListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ItemViewHolder {
                val binding = CategoryListItemBinding.inflate(parent.inflater(), parent, false)
                return ItemViewHolder(binding)
            }
        }

        fun bind(category: Category, onClick: (Category) -> Unit) {
            binding.categoryListItemTitle.text = category.name
            binding.categoryListItemImage.setImageResource(category.imageRes)

            binding.root.setOnClickListener {
                onClick(category)
            }
        }
    }

}
