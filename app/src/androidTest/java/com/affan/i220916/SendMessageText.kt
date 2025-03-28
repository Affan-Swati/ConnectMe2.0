package com.affan.i220916

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
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
class SendMessageTest {
    val message = "testMessage7"
    @get:Rule
    var activityRule: ActivityScenarioRule<login> = ActivityScenarioRule(login::class.java)

    @Test
    fun testMessage() {
        onView(withId(R.id.email)).perform(typeText("adil@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("123456789"))
        onView(withId(R.id.password)).perform(closeSoftKeyboard())
        onView(withId(R.id.login)).perform(click())

        onView(isRoot()).perform(waitFor(2000))

        onView(withId(R.id.open_dms)).perform(click())
        onView(isRoot()).perform(waitFor(4000))
        onView(withId(R.id.recycler_view))
            .perform(clickItemWithUsername("affan"))
        onView(isRoot()).perform(waitFor(3000))
        onView(withId(R.id.type_msg))
            .perform(replaceText(message))
        onView(isRoot()).perform(waitFor(1000))
        onView(isRoot()).perform(waitFor(500))
        onView(withId(R.id.send_button)).perform(click())
        onView(isRoot()).perform(waitFor(1000))


        // Testing if 'affan' received message
        val scenario = activityRule.scenario
        scenario.close()

        val loginScenario = ActivityScenario.launch(login::class.java)

        onView(withId(R.id.email)).perform(typeText("affan@gmail.com"))
        onView(withId(R.id.password)).perform(typeText("123456789"))
        onView(withId(R.id.password)).perform(closeSoftKeyboard())
        onView(withId(R.id.login)).perform(click())
        onView(isRoot()).perform(waitFor(1000))


        onView(withId(R.id.open_dms)).perform(click())
        onView(isRoot()).perform(waitFor(2000))
        onView(withId(R.id.recycler_view))
            .perform(clickItemWithUsername("adil"))
        onView(isRoot()).perform(waitFor(5000))

        onView(withId(R.id.recycler_view_msgs))
            .perform(RecyclerViewActions.scrollToLastPosition<RecyclerView.ViewHolder>())

        onView(withId(R.id.recycler_view_msgs))
            .check(matches(atLastPosition(R.id.msg, withText(message))))

        loginScenario.close()
    }

    fun clickItemWithUsername(username: String): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(RecyclerView::class.java)
            }

            override fun getDescription(): String {
                return "Click item with username: $username"
            }

            override fun perform(uiController: UiController, view: View) {
                val recyclerView = view as RecyclerView
                for (i in 0 until recyclerView.adapter!!.itemCount) {
                    val holder = recyclerView.findViewHolderForAdapterPosition(i)
                    val usernameView = holder?.itemView?.findViewById<TextView>(R.id.username)
                    if (usernameView?.text?.toString() == username) {
                        holder.itemView.performClick() // Click the entire item
                        return
                    }
                }
            }
        }
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

    fun <VH : RecyclerView.ViewHolder> scrollToLastPosition(): ViewAction {
        return object : ViewAction {
            override fun getConstraints() = isAssignableFrom(RecyclerView::class.java)
            override fun getDescription() = "Scroll to last position"
            override fun perform(uiController: UiController, view: View) {
                val recyclerView = view as RecyclerView
                recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount - 1)
            }
        }
    }

    fun atLastPosition(childViewId: Int, viewMatcher: Matcher<View>): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun matchesSafely(view: RecyclerView): Boolean {
                val lastPos = view.adapter!!.itemCount - 1
                val holder = view.findViewHolderForAdapterPosition(lastPos)
                val childView = holder?.itemView?.findViewById<View>(childViewId)
                return viewMatcher.matches(childView)
            }

            override fun describeTo(description: Description) {
                description.appendText("Last item with child view $childViewId matching: ")
                viewMatcher.describeTo(description)
            }
        }
    }
}