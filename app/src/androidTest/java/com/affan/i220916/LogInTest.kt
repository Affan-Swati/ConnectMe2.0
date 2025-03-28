package com.affan.i220916

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    var activityRule: ActivityScenarioRule<login> = ActivityScenarioRule(login::class.java)

    @Before
    fun setup() {
        // Initialize Intents to capture intent-based actions
        Intents.init()
    }

    @After
    fun tearDown() {
        // Release Intents to avoid memory leaks
        Intents.release()
    }

    @Test
    fun testUserLogin() {

        onView(withId(R.id.email)).perform(typeText("adil@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("123456789"))
        onView(withId(R.id.password)).perform(closeSoftKeyboard())
        onView(withId(R.id.login)).perform(click())

        Thread.sleep(1000)

        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()))
    }
}
