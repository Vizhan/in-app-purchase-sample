package com.lanars.purchase.purchase.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.billingclient.api.SkuDetails
import com.lanars.purchase.R
import com.lanars.purchase.base.BaseRecyclerAdapter
import com.lanars.purchase.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_recycler_purchase.view.*

class PurchaseAdapter(private val onClick: (SkuDetails) -> Unit) : BaseRecyclerAdapter<SkuDetails, PurchaseAdapter.PurchaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseViewHolder {
        val view = parent.inflate(R.layout.item_recycler_purchase)
        return PurchaseViewHolder(view)
    }

    override fun areItemsTheSame(oldItem: SkuDetails, newItem: SkuDetails): Boolean = oldItem.sku == newItem.sku

    inner class PurchaseViewHolder(view: View) : BaseViewHolder<SkuDetails>(view) {
        override fun bindData(item: SkuDetails) {
            itemView.tvPurchaseTitle.text = item.title
            itemView.tvPurchaseDescription.text = item.description
            itemView.tvPrice.text = item.price

            itemView.btnBuy.setOnClickListener { onClick.invoke(item) }
        }
    }

    private fun ViewGroup.inflate(layoutID: Int, attachToRoot: Boolean = false): View =
            LayoutInflater.from(context).inflate(layoutID, this, attachToRoot)
}