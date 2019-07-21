package com.kikyoung.currency.test.util

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.TypeSafeMatcher

/**
 * Utility to make asserting on recycler view cells easier.
 */
fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher = RecyclerViewMatcher(recyclerViewId)

/**
 * Helper to run actions on recycler view cells.
 */
fun actionOnItemViewAtPosition(
    position: Int,
    @IdRes viewId: Int,
    viewAction: ViewAction
): ViewAction = ActionOnItemViewAtPositionViewAction(position, viewId, viewAction)

/**
 * Utility matcher to help with asserting the state views contained within a recycler view
 */
class RecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            var childView: View? = null

            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (this.resources != null) {
                    idDescription = try {
                        this.resources!!.getResourceName(recyclerViewId)
                    } catch (var4: Resources.NotFoundException) {
                        String.format(
                            "%s (resource name not found)",
                            Integer.valueOf(recyclerViewId)
                        )
                    }
                }

                description.appendText("with id: $idDescription")
            }

            public override fun matchesSafely(view: View): Boolean {
                this.resources = view.resources

                if (childView == null) {
                    val recyclerView = view.rootView.findViewById<View>(recyclerViewId) as RecyclerView
                    if (recyclerView.id == recyclerViewId) {
                        childView = recyclerView.findViewHolderForAdapterPosition(position)!!.itemView
                    } else {
                        return false
                    }
                }

                return if (targetViewId == -1) {
                    view === childView
                } else {
                    val targetView = childView!!.findViewById<View>(targetViewId)
                    view === targetView
                }

            }
        }
    }
}

private class ActionOnItemViewAtPositionViewAction(
    private val position: Int,
    @param:IdRes private val viewId: Int,
    private val viewAction: ViewAction
) : ViewAction {

    override fun getConstraints(): Matcher<View> = Matchers.allOf(
        ViewMatchers.isAssignableFrom(RecyclerView::class.java),
        ViewMatchers.isDisplayed()
    )

    override fun getDescription(): String {
        return ("""actionOnItemAtPosition performing ViewAction: ${this.viewAction.description} on item at position: ${this.position}""")
    }

    override fun perform(uiController: UiController, view: View) {
        val recyclerView = view as RecyclerView
        ScrollToPositionViewAction(this.position).perform(uiController, view)
        uiController.loopMainThreadUntilIdle()

        val targetView = recyclerView.getChildAt(this.position).findViewById<View>(this.viewId)

        if (targetView == null) {
            throw PerformException.Builder().withActionDescription(this.toString())
                .withViewDescription(
                    HumanReadables.describe(view)
                )
                .withCause(
                    IllegalStateException(
                        """No view with id ${this.viewId} found at position: ${this.position}"""
                    )
                )
                .build()
        } else {
            this.viewAction.perform(uiController, targetView)
        }
    }
}

class ScrollToPositionViewAction(private val position: Int) : ViewAction {

    override fun getConstraints(): Matcher<View> =
        Matchers.allOf(ViewMatchers.isAssignableFrom(RecyclerView::class.java), ViewMatchers.isDisplayed())

    override fun getDescription(): String = """scroll RecyclerView to position: ${this.position}"""

    override fun perform(uiController: UiController, view: View) {
        val recyclerView = view as RecyclerView
        recyclerView.scrollToPosition(this.position)
    }
}

class ScrollByViewAction(private val verticalScrollBy: Int) : ViewAction {

    override fun getConstraints(): Matcher<View> =
        Matchers.allOf(ViewMatchers.isAssignableFrom(RecyclerView::class.java), ViewMatchers.isDisplayed())

    override fun getDescription(): String = """scroll RecyclerView by: ${this.verticalScrollBy}"""

    override fun perform(uiController: UiController, view: View) {
        val recyclerView = view as RecyclerView
        recyclerView.scrollBy(0, verticalScrollBy)
    }
}