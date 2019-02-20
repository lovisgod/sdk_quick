package com.interswitchng.smartpos.config

import android.app.Application
import android.content.Context
import com.interswitchng.smartpos.IswPos
import com.interswitchng.smartpos.di.activityModules
import com.interswitchng.smartpos.di.appModules
import com.interswitchng.smartpos.shared.interfaces.device.POSDevice
import com.interswitchng.smartpos.shared.models.core.POSConfig
import com.nhaarman.mockitokotlin2.mock
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.get
import org.koin.standalone.getKoin
import org.koin.test.KoinTest
import org.koin.test.checkModules


class DependencyGraphTest: KoinTest {

    @Test
    fun checkDependencyGraph() {
        // add app context
        val appContext = module(override = true) {
            single { mock<Context>() }
        }

        // get all modules
        val moduleList = appModules + activityModules + appContext

        checkModules(moduleList)
    }

    @Test
    fun `check that dependency was setup after configuring IswPos`() {
        val app: Application = mock()
        val device: POSDevice = mock()
        val config: POSConfig = mock()


        IswPos.configureTerminal(app, device, config)
        val isw: IswPos = get()

        assertNotNull(isw)
        assertSame(isw, IswPos.getInstance())
    }


    @After
    fun tearDown() {
        getKoin().close()
    }
}