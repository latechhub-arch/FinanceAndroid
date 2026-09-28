package com.latechhub.finance

import android.app.Application
import com.latechhub.finance.data.remote.RetrofitClient

class FinanceApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.initialize(this)
    }
}
