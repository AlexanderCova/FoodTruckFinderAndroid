package com.foodtruckfindermi.client

import android.util.Log
import androidx.lifecycle.ViewModel
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.QonversionError
import com.qonversion.android.sdk.QonversionOfferingsCallback
import com.qonversion.android.sdk.QonversionPermissionsCallback
import com.qonversion.android.sdk.dto.QPermission
import com.qonversion.android.sdk.dto.offerings.QOffering
import com.qonversion.android.sdk.dto.offerings.QOfferings

class qonversionViewModel : ViewModel() {

    var offerings = listOf<QOffering>()
        private set

    var hasPremiumPermission = false
        private set

    init {
        loadOfferings()
        getPermission("Premium")
    }

    private fun loadOfferings() {
        Qonversion.offerings(object : QonversionOfferingsCallback {
            override fun onError(error: QonversionError) {

            }

            override fun onSuccess(offerings: QOfferings) {
                this@qonversionViewModel.offerings = offerings.availableOfferings
            }
        })
    }

    fun getPermission(permissionName : String) {
        Qonversion.checkPermissions(object : QonversionPermissionsCallback {
            override fun onError(error: QonversionError) {

            }

            override fun onSuccess(permissions: Map<String, QPermission>) {
                hasPremiumPermission = permissions[permissionName]?.isActive() == true

            }

        })
    }

}