package com.foodtruckfindermi.client.Fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.foodtruckfindermi.client.R
import com.foodtruckfindermi.client.TruckScreen
import com.foodtruckfindermi.client.UserScreen
import com.foodtruckfindermi.client.qonversionViewModel
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionError
import com.qonversion.android.sdk.QonversionPermissionsCallback
import com.qonversion.android.sdk.dto.QPermission
import kotlinx.android.synthetic.main.fragment_premium_popup.*


class PremiumPopupFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_premium_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = (activity as UserScreen).viewModel

        premiumCancelButton.setOnClickListener {
            dismiss()
        }
        premiumUpgradeButton.setOnClickListener {

            Log.i("Qonversion", viewModel.offerings.toString())

            Qonversion.purchase(
                requireActivity(),
                "monthly_premium",
                object : QonversionPermissionsCallback {
                    override fun onError(error: QonversionError) {
                        Toast.makeText(requireActivity(),
                            error.description,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onSuccess(permissions: Map<String, QPermission>) {
                        Toast.makeText(requireActivity(),
                            "Purchase Successful!",
                            Toast.LENGTH_LONG
                        ).show()

                        viewModel.getPermission("Premium")
                        dismiss()
                        (activity as UserScreen).refresh()
                    }

                }
            )
        }


    }


}