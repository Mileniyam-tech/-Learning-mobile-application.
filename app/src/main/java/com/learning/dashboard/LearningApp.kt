package com.learning.dashboard

import android.app.Application
import com.learning.dashboard.di.AppContainer

class LearningApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
