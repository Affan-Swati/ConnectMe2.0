//package com.affan.i220916
//
//import androidx.recyclerview.widget.RecyclerView
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.action.ViewActions.typeText
//import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.espresso.matcher.ViewMatchers.withText
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import androidx.test.espresso.matcher.ViewMatchers.*
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.contrib.RecyclerViewActions
//
//
//@RunWith(AndroidJUnit4::class)
//class SendMessageTest {
//
//    @get:Rule
//    val activityRule = ActivityScenarioRule(dm::class.java)
//
//    @Test
//    fun testSendMessage() {
//        onView(withId(R.id.type_msg))
//            .perform(typeText("Hello!"))
//        onView(withId(R.id.send_button)).perform(click())
//        onView(withId(R.id.recycler_view_msgs))
//            .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
//                hasDescendant(withText("Hello!"))
//            ))
//            .check(matches(hasDescendant(withText("Hello!"))))
//
//    }
//}
