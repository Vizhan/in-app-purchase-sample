package com.lanars.purchase.billing.sku

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsParams

class InAppSku : ISkuTypeStrategy {

    private val skuList = listOf("donate_10_uah", "donate_50_uah", "0.5_star", "2.5_star")

    override fun buildParams(): SkuDetailsParams {
        return SkuDetailsParams
                .newBuilder()
                .setType(BillingClient.SkuType.INAPP)
                .setSkusList(skuList)
                .build()
    }
}