package com.lanars.purchase.rating

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.lanars.purchase.PurchaseActivity
import com.lanars.purchase.R
import kotlinx.android.synthetic.main.fragment_raiting.*

class RatingFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_raiting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        invalidateRating()
        initToolbar()
        initListeners()
    }

    private fun invalidateRating() {
        srbRate.post { srbRate.rating = PurchaseActivity.ratingBalance }
    }

    private fun initToolbar() {
        toolbar.apply {
            inflateMenu(R.menu.menu_options)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.menuCatalog) {
                    NavHostFragment.findNavController(this@RatingFragment).navigate(R.id.action_to_purchaseFragment)
                }

                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun initListeners() {
        btnSpoil.setOnClickListener {
            PurchaseActivity.ratingBalance = srbRate.rating - 0.5f
            srbRate.rating = PurchaseActivity.ratingBalance
        }
    }
}