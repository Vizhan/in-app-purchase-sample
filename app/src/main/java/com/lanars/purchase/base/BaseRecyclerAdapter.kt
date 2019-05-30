package com.lanars.purchase.base

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


abstract class BaseRecyclerAdapter<T, VH : BaseViewHolder<T>> : RecyclerView.Adapter<VH>() {

    private val items: MutableList<T> = ArrayList()

    val isEmpty: Boolean
        get() = itemCount == 0

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.bindData(item)
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<T>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtilCallback<T>(items, newItems) {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@BaseRecyclerAdapter.areItemsTheSame(items[oldItemPosition], newItems[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return this@BaseRecyclerAdapter.areContentsTheSame(items[oldItemPosition], newItems[newItemPosition])
            }
        }, true)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItems(): List<T> = items

    fun getItem(position: Int): T = items[position]

    fun add(item: T) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun addAll(items: List<T>) {
        this.items.addAll(items)
        notifyItemRangeInserted(this.items.size - items.size, items.size)
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun remove(item: T) {
        val position = items.indexOf(item)
        if (position > -1) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    open fun updateItem(item: T) = notifyItemChanged(items.indexOf(item))

    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean
    open fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}