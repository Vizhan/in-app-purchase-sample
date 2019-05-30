package com.lanars.purchase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PurchaseActivity : AppCompatActivity() {

    companion object {
        var ratingBalance = 2f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)
    }
}
