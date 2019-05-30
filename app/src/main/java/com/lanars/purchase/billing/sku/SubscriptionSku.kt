package com.lanars.purchase.billing.sku

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetailsParams

class SubscriptionSku : ISkuTypeStrategy {

    private val skuList = listOf("subscription_1_month")

    override fun buildParams(): SkuDetailsParams {
        return SkuDetailsParams
                .newBuilder()
                .setType(BillingClient.SkuType.SUBS)
                .setSkusList(skuList)
                .build()
    }
}