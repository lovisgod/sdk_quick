package com.interswitchng.interswitchpossdk.activities

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(MainActivityTests::class, UssdActivityTests::class)
class ActivityTestSuite