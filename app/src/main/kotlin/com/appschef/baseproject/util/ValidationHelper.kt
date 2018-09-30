package com.appschef.baseproject.util

import android.util.Patterns
import android.view.View
import android.widget.TextView

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Alvin on 10/10/16.
 *
 * A validation helper class.
 */
object ValidationHelper {

    /**
     * Check if a specified [String] is a valid [JSONObject] or [JSONArray].
     * @param message the [String] to check
     * @return true if JSON is valid
     */
    fun isValidJson(message: String): Boolean {
        try {
            JSONObject(message)
        } catch (ex: JSONException) {
            // Check if JSONArray is valid as well…
            try {
                JSONArray(message)
            } catch (ex1: JSONException) {
                return false
            }
        }

        return true
    }

    /**
     * Checks if a [String] is empty.
     * Also checks for whitespaces, so if the text is "   " it will be treated as an empty String.
     * Only checks for visible [TextView].
     * @param textView the [TextView] containing the [String] to check
     * @return true if empty, returns a false when [TextView] isn't visible / null
     */
    fun isEmpty(textView: TextView?): Boolean {
        if (textView?.visibility == View.VISIBLE) return isEmpty(textView.text.toString())
        else return false
    }

    /**
     * Checks if a [String] is empty.
     * Also checks for whitespaces, so if the text is "   " it will be treated as an empty String.
     * @param text the string to check
     * @return true if empty
     */
    fun isEmpty(text: String?): Boolean {
        if (text.isNullOrEmpty()) return true
        if (text!!.replace(" ".toRegex(), "").isEmpty()) return true
        return false
    }

    /**
     * Determines if a password input is valid.
     * Only checks for visible [TextView].
     * @param textView the [TextView] containing the [String] to check
     * @return false if password is invalid, returns a true when [TextView] isn't visible / null
     */
    fun isValidPassword(textView: TextView?): Boolean {
        if (textView?.visibility == View.VISIBLE) return isValidPassword(textView.text.toString())
        else return true
    }

    /**
     * Determines if a password input is valid.
     * @param text the string to check
     * @return false if password is invalid
     */
    fun isValidPassword(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false

        // TODO: Add your own Regex here
        // This sample pattern checks for:
        // (?=.*\d)         - At least 1 number
        // (?=.*[a-zA-Z])   - At least 1 letter
        // \S{6,}           - No whitespaces, and minimum of 6 chars
        val pattern = Pattern.compile("\\A(?=.*\\d)(?=.*[a-zA-Z])\\S{6,}\\Z")
        return pattern.matcher(text).matches()
    }

    /**
     * Determines if an email input is valid.
     * Only checks for visible [TextView].
     * @param textView the [TextView] containing the [String] to check
     * @return false if email is invalid, returns a true when [TextView] isn't visible / null
     */
    fun isValidEmail(textView: TextView?): Boolean {
        if (textView?.visibility == View.VISIBLE) return isValidEmail(textView.text.toString())
        else return true
    }

    /**
     * Determines if an email input is valid.
     * @param text the string to check
     * @return true if it's a valid email
     */
    fun isValidEmail(text: String?): Boolean {
        if (text.isNullOrEmpty()) return false
        return Patterns.EMAIL_ADDRESS.matcher(text).matches()
    }
}
