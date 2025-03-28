package com.affan.i220916

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(login::class.java)

    @Test
    fun testUserLogin() {

        onView(withId(R.id.email))
            .perform(typeText("adil@gmail.com"))
        onView(withId(R.id.password))
            .perform(typeText("123456789"))
        onView(withId(R.id.login_button)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
    }
}

