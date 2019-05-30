package com.lanars.purchase.purchase

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.android.billingclient.api.*
import com.lanars.purchase.PurchaseActivity
import com.lanars.purchase.R
import com.lanars.purchase.billing.BillingEngine
import com.lanars.purchase.billing.BillingSecurity
import com.lanars.purchase.billing.sku.InAppSku
import com.lanars.purchase.billing.sku.SubscriptionSku
import com.lanars.purchase.purchase.adapter.PurchaseAdapter
import kotlinx.android.synthetic.main.fragment_purchase.*

class PurchaseFragment : Fragment(), PurchasesUpdatedListener, SkuDetailsResponseListener, ConsumeResponseListener {

    private val purchaseAdapter = PurchaseAdapter { skuDetails -> BillingEngine.launchBillingFlow(requireActivity(), skuDetails) }
    private var currentPurchase: Purchase? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_purchase, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        BillingEngine.initBillingClient(requireContext(), this)

        initRecyclerView()
        initListeners()
    }

    private fun initRecyclerView() {
        recyclerCatalog.adapter = purchaseAdapter
    }

    private fun initListeners() {
        btnPurchases.setOnClickListener { BillingEngine.queryPurchases(InAppSku(), this) }
        btnSubscriptions.setOnClickListener { BillingEngine.queryPurchases(SubscriptionSku(), this) }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult?, purchases: MutableList<Purchase>?) {
        Log.i("+++", "onPurchasesUpdated ${billingResult?.responseCode}")

        purchases?.forEach { purchase ->
            if (!BillingSecurity.verifyValidSignature(purchase)) {
                Log.i("+++", "Got a purchase: $purchase; but signature is bad. S k i p p i n g...")

            } else {
                Log.i("+++", "Got a purchase: $purchase; ${purchase.sku} P r o c e e d...")

                currentPurchase = purchase
                BillingEngine.consumePurchase(purchase, this)
            }
        }
    }

    override fun onSkuDetailsResponse(billingResult: BillingResult?, skuDetailsList: MutableList<SkuDetails>) {
        Log.i("+++", "response CODE # ${billingResult?.responseCode} SIZE = ${skuDetailsList.size}")

        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK) {
            purchaseAdapter.setItems(skuDetailsList)
        }
    }

    override fun onConsumeResponse(billingResult: BillingResult?, purchaseToken: String?) {
        Log.i("+++", "response CODE # ${billingResult?.responseCode}")

        if (billingResult?.responseCode == BillingClient.BillingResponseCode.OK && purchaseToken != null) {
            Log.i("+++", "u p d a t e   r a t i n g...")
            updateRating(currentPurchase)
        }
    }

    private fun updateRating(purchase: Purchase?) {
        when (purchase?.sku) {
            "0.5_star" -> PurchaseActivity.ratingBalance = PurchaseActivity.ratingBalance + 0.5f
            "2.5_star" -> PurchaseActivity.ratingBalance = PurchaseActivity.ratingBalance + 2.5f
        }

        NavHostFragment.findNavController(this@PurchaseFragment).popBackStack()
    }
}