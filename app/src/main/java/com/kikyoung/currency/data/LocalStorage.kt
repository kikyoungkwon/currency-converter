package com.kikyoung.currency.data

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

/**
 * TODO Use Room instead of SharedPreferences
 */
class LocalStorage(context: Context, private val gson: Gson) {

    companion object {
        private const val NAME = "localStorage"
    }

    private val sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun put(key: String, any: Any?) {
        sharedPreferences.edit {
            putString(key, gson.toJson(any))
        }
    }

    fun <T> get(key: String, type: Class<T>, defaultValue: T? = null): T? {
        return try {
            sharedPreferences.getString(key, gson.toJson(defaultValue))?.let {
                gson.fromJson(it, type)
            }
        } catch (e: Exception) {
            null
        }
    }
}