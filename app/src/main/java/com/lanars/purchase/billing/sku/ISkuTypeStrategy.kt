package com.lanars.purchase.billing.sku

import com.android.billingclient.api.SkuDetailsParams

interface ISkuTypeStrategy {
    fun buildParams(): SkuDetailsParams
}