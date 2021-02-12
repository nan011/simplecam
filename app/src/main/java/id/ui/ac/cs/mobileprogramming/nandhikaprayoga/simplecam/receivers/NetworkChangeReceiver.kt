package id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import id.ui.ac.cs.mobileprogramming.nandhikaprayoga.simplecam.states.NetworkState

class NetworkChangeReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        NetworkState.isOnNetwork = isOnline(context)
    }

    private fun isOnline(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            //should check null because in airplane mode it will be null
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}