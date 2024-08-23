package com.example.kirisun

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var usbHandler: UsbHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usbHandler = UsbHandler(this)
        usbHandler.start()

        val button = findViewById<Button>(R.id.btn_send)
        button.setOnClickListener {
            val deviceList = usbHandler.getDeviceList()
            if (deviceList.isNotEmpty()) {
                usbHandler.sendData("Hello USB".toByteArray())
            } else {
                Toast.makeText(this, "No USB devices found", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
