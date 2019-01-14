package com.interswitchng.interswitchpossdk.config

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(DependencyGraphTest::class, IswPosInstrumentedTest::class, IswPosTest::class)
class ConfigTestSuite