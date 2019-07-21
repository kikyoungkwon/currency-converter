package com.kikyoung.currency.test.util

import android.view.View
import androidx.test.espresso.IdlingResource

/**
 * @see <a href="https://stackoverflow.com/a/35080198/7395851">Wait until view become visible with IdleResource</a>
 */
class ViewVisibilityIdlingResource(private val view: View, private val expectedVisibility: Int) : IdlingResource {

    private var idle: Boolean = false
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    init {
        idle = false
        resourceCallback = null
    }

    override fun getName(): String {
        return ViewVisibilityIdlingResource::class.java.simpleName
    }

    override fun isIdleNow(): Boolean {
        idle = idle || view.visibility == expectedVisibility
        if (idle) if (resourceCallback != null) resourceCallback!!.onTransitionToIdle()
        return idle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.resourceCallback = resourceCallback
    }
}