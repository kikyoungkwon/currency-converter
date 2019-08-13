package com.kikyoung.currency.data

import android.content.Context
import androidx.core.content.edit
import com.squareup.moshi.Moshi

/**
 * TODO Use Room instead of SharedPreferences
 */
class LocalStorage(context: Context, private val moshi: Moshi) {

    companion object {
        private const val NAME = "localStorage"
    }

    private val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun <T> put(key: String, type: Class<T>, value: T?) {
        sharedPreferences.edit {
            putString(key, moshi.adapter(type).toJson(value))
        }
    }

    fun <T> get(key: String, type: Class<T>, defaultValue: T? = null): T? {
        return try {
            sharedPreferences.getString(key, moshi.adapter(type).toJson(defaultValue))?.let {
                moshi.adapter(type).fromJson(it)
            }
        } catch (e: Exception) {
            null
        }
    }
}