package com.example.nuru.observer

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import java.lang.IllegalArgumentException

class NetworkConnection(private val context: Context) : LiveData<Boolean>()
{
    private var connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onActive()
    {
        super.onActive()
        updateConnection()
        when
        {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
            {
                Log.d("Registered" , "Registered")
                connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback())
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ->
            {
                lollipopNetworkRequest()
            }
            else ->
            {
                context.registerReceiver(
                    networkReceiver,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }
    }

    override fun onInactive()
    {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {

            try {
                //여기 try catch 안하면 IllegalArgumentException 발생, 왜지?
                //onactive 에서 register 했는데?...
                connectivityManager.unregisterNetworkCallback(connectivityManagerCallback())
            } catch (e: IllegalArgumentException) {
                // Check wether we are in debug mode
                Log.d("Exception" , e.toString())
            }
        } else
        {
            try {
                context.unregisterReceiver(networkReceiver)
            } catch (e: IllegalArgumentException) {
                // Check wether we are in debug mode
                Log.d("Exception" , e.toString())
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun lollipopNetworkRequest()
    {
        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        connectivityManager.registerNetworkCallback(
            requestBuilder.build(),
            connectivityManagerCallback()
        )
    }

    fun connectivityManagerCallback(): ConnectivityManager.NetworkCallback
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            networkCallback = object : ConnectivityManager.NetworkCallback()
            {
                override fun onLost(network: Network)
                {
                    super.onLost(network)
                    postValue(false)
                }

                override fun onAvailable(network: Network)
                {
                    super.onAvailable(network)
                    postValue(true)
                }
            }
            return networkCallback
        } else
        {
            throw IllegalAccessError("Error")
        }
    }

    private val networkReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?)
        {
            updateConnection()
        }
    }

    private fun updateConnection()
    {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        postValue(activeNetwork?.isConnected == true)
    }



}