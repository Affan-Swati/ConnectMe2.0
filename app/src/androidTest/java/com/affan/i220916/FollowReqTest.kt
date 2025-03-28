package com.affan.i220916

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.equalToIgnoringWhiteSpace
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FollowTest {
    @get:Rule
    var activityRule: ActivityScenarioRule<login> = ActivityScenarioRule(login::class.java)

    @Test
    fun testFollowRequest() {
        onView(withId(R.id.email)).perform(typeText("adil@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("123456789"))
        onView(withId(R.id.password)).perform(closeSoftKeyboard())
        onView(withId(R.id.login)).perform(click())

        onView(isRoot()).perform(waitFor(2000))

        onView(withId(R.id.search_btn)).perform(click())
        onView(isRoot()).perform(waitFor(1000))

        onView(withId(R.id.search)).perform(typeText("affan"))
        onView(withId(R.id.searchIcon)).perform(click())
        onView(isRoot()).perform(waitFor(6000))


        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0,
                clickChildViewWithId(R.id.follow)))

        // Testing if 'affan' received req
        val scenario = activityRule.scenario
        scenario.close()

        val loginScenario = ActivityScenario.launch(login::class.java)

        onView(withId(R.id.email)).perform(typeText("affan@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("123456789"))
        onView(withId(R.id.password)).perform(closeSoftKeyboard())
        onView(withId(R.id.login)).perform(click())
        onView(isRoot()).perform(waitFor(1000))


        onView(withId(R.id.open_dms)).perform(click())
        onView(isRoot()).perform(waitFor(1000))
        onView(withId(R.id.Requests)).perform(click())
        onView(isRoot()).perform(waitFor(5000))


        onView(withId(R.id.recycler_view))
            .check(matches(hasMinimumChildCount(1)))

        loginScenario.close()
    }

    fun clickChildViewWithId(childViewId: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(View::class.java)
            }

            override fun getDescription(): String {
                return "Click on a child view with specified ID."
            }

            override fun perform(uiController: UiController?, view: View?) {
                val childView = view?.findViewById<View>(childViewId)
                childView?.performClick()
            }
        }
    }

    fun atPositionOnView(position: Int, childViewId: Int, visibility: Matcher<View>): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                val childView = viewHolder?.itemView?.findViewById<View>(childViewId)
                return visibility.matches(childView)
            }

            override fun describeTo(description: Description) {
                description.appendText("RecyclerView item at position $position and child view with ID $childViewId")
            }
        }
    }
    fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints() = isRoot()
            override fun getDescription() = "Wait for $millis milliseconds"
            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
}