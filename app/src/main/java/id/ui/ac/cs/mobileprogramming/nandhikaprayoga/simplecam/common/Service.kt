package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.common

import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class Service {
    enum class HttpMethod(val value: String) {
        GET("GET"),
        POST("POST"),
    }

    companion object {
        fun sendRequest(
            callback: Callback,
            uri: String,
            method: HttpMethod = HttpMethod.GET,
            body: RequestBody? = null,
        ) {
            val client: OkHttpClient = OkHttpClient().newBuilder().build()
            val rawRequest: Request.Builder = Request.Builder()
                .url(uri)
                .method(method.value, body)

            val request: Request = rawRequest.build()
            client.newCall(request).enqueue(callback)
        }
    }
}