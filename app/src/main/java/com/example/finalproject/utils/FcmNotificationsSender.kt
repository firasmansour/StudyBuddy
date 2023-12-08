package com.example.finalproject.utils

import android.app.Activity
import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.finalproject.R
import org.json.JSONException
import org.json.JSONObject


class FcmNotificationsSender(
    var userFcmToken: String,
    var title: String,
    var body: String,
    var mContext: Context,
    var mActivity: Activity,
    var groupId :String
) {
    private var requestQueue: RequestQueue? = null
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey =
        "AAAAJehcPCw:APA91bFzculjcX9hCm5AgZCEHeSPiEJZ9CxIG11MVcW4cth0fpEcCi-6TT0lzaYomqOWrRZ32E5W8cieE-OM6CZfJD7u5gFezkBGb3KCrQ9igudrS8Bj1BzeQ2JFjhSyYE1bBMIJ0DcZ"

    fun SendNotifications() {
        requestQueue = Volley.newRequestQueue(mActivity)
        val mainObj = JSONObject()
        val data = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            notiObject.put("icon", R.drawable.ic_notification) // enter icon that exists in drawable only
            data.put("groupId",groupId)
            notiObject.put("data",data)
            mainObj.put("notification", notiObject)
            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST, postUrl, mainObj,
                Response.Listener {
                    // code run is got response
                },
                Response.ErrorListener {
                    // code run is got error
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header: MutableMap<String, String> = HashMap()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue!!.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}