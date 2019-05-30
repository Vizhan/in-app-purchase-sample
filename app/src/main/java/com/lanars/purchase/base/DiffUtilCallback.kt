package com.lanars.purchase.base

import androidx.recyclerview.widget.DiffUtil

abstract class DiffUtilCallback<T>(private val oldData: List<T>,
                                   private val newData: List<T>)
    : DiffUtil.Callback() {

    abstract override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldData[oldItemPosition] == newData[newItemPosition]
}