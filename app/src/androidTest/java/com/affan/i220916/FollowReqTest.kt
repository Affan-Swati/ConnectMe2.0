package com.affan.i220916

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FollowRequestTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(search_new_users::class.java)

    @Test
    fun testFollowUnfollowUser() {
        onView(withId(R.id.follow)).perform(click())
        onView(withId(R.id.requested)).check(matches(withText("Requested")))
        onView(withId(R.id.requested)).perform(click())
        onView(withId(R.id.follow)).check(matches(withText("Follow")))
    }
}