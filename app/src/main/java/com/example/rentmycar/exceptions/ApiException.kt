package com.example.rentmycar.exceptions

import org.json.JSONObject

class ApiException(val errorCode: Int, private val errors: String) : Exception() {
    override val message: String
        get() {
            return try {
                val jsonObject = JSONObject(errors)

                if (jsonObject.has("errors")) {
                    val errorList = jsonObject.getJSONArray("errors")
                    (0 until errorList.length())
                        .joinToString(separator = "\n") { errorList.getString(it) }
                }
                else if (jsonObject.has("error")) {
                    jsonObject.getString("error")
                } else {
                    errors // Fallback to raw errors string if neither is present
                }
            } catch (e: Exception) {
                errors
            }
        }
}
