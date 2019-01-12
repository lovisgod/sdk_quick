package com.interswitchng.interswitchpossdk

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import com.interswitchng.interswitchpossdk.base.MockApplication

class MockTestRunner: AndroidJUnitRunner() {

    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, MockApplication::class.java.name, context)
    }

}