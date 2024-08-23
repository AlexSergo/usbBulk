package com.example.kirisun

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

fun Context.addReceiver(broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter, exported: Int? = null){
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            ContextCompat.registerReceiver(this, broadcastReceiver, intentFilter, ContextCompat.RECEIVER_VISIBLE_TO_INSTANT_APPS)
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                registerReceiver(broadcastReceiver, intentFilter, exported ?: Context.RECEIVER_NOT_EXPORTED)
            else
                registerReceiver(broadcastReceiver, intentFilter)
        }
    }catch (e: Exception){
        Log.d("ReceiverExtensionManager", "Не удалось добавить ресивер ${e.message}")
    }
}