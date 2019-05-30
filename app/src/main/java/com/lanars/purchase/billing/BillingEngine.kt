package com.lanars.purchase.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.lanars.purchase.billing.sku.ISkuTypeStrategy

object BillingEngine {

    private lateinit var billingClient: BillingClient

    fun initBillingClient(context: Context, listener: PurchasesUpdatedListener) {
        billingClient = BillingClient
                .newBuilder(context)
                .setListener(listener)
                .enablePendingPurchases()
                .build()
    }

    fun launchBillingFlow(activity: Activity, details: SkuDetails): BillingResult =
            billingClient.launchBillingFlow(activity, buildBillingFlowParams(details))
    
    private fun buildBillingFlowParams(details: SkuDetails): BillingFlowParams = BillingFlowParams
            .newBuilder()
            .setSkuDetails(details)
            .build()

    fun consumePurchase(purchase: Purchase?, listener: ConsumeResponseListener) {
        if (purchase != null) {
            val params = ConsumeParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .setDeveloperPayload(purchase.developerPayload)
                    .build()

            billingClient.consumeAsync(params, listener)
        }
    }

    fun queryPurchases(type: ISkuTypeStrategy, listener: SkuDetailsResponseListener) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.i("+++", "Connection ERROR")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult?) {
                Log.i("+++", "onBillingSetupFinished response CODE # ${billingResult?.responseCode}")

                if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
                    querySkuDetailsAsync(type, listener)
                }
            }
        })
    }

    private fun querySkuDetailsAsync(type: ISkuTypeStrategy, listener: SkuDetailsResponseListener) {
        if (billingClient.isReady) {
            billingClient.querySkuDetailsAsync(type.buildParams(), listener)
        } else {
            Log.i("+++", "Billing client NOT READY")
        }
    }
}