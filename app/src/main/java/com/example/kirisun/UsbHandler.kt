package com.example.kirisun

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbRequest
import android.os.Build
import android.os.Parcelable
import android.widget.Toast
import java.nio.ByteBuffer
import java.util.UUID

class UsbHandler(private val context: Context) {

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var deviceConnection: UsbDeviceConnection? = null
    private var usbDevice: UsbDevice? = null

    companion object {
        const val ACTION_USB_PERMISSION = "com.example.kirisun.USB_PERMISSION"
    }

    fun start() {
        val filter = IntentFilter().apply {
            addAction(ACTION_USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        context.addReceiver(usbReceiver, filter)
    }

    fun sendData( data: ByteArray) {
        if (usbDevice == null) return
        val usbInterface: UsbInterface = usbDevice!!.getInterface(0)
        val endpointOut: UsbEndpoint = usbInterface.getEndpoint(0)
        val endpointIn: UsbEndpoint = usbInterface.getEndpoint(1)

        deviceConnection = usbManager.openDevice(usbDevice!!)
        if (deviceConnection == null) {
            Toast.makeText(context, "Failed to open USB device connection", Toast.LENGTH_SHORT).show()
            return
        }

        deviceConnection?.claimInterface(usbInterface, true)

        val usbRequest = UsbRequest()
        usbRequest.initialize(deviceConnection, endpointOut)
        usbRequest.queue(ByteBuffer.wrap(data), data.size)


        deviceConnection?.bulkTransfer(endpointOut, data, data.size, 2500)

        // Clean up
        deviceConnection?.releaseInterface(usbInterface)
        deviceConnection?.close()
    }

    fun getDeviceList(): List<UsbDevice> {
        return usbManager.deviceList.values.toList()
    }

    val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                try {
                    synchronized(this) {
                        usbDevice = getUsbDevice(intent)
                    }
                } catch (_: Exception) {}

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                usbDevice = null
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                usbDevice = getUsbDevice(intent)
            }
        }
    }

    private fun getUsbDevice(intent: Intent): UsbDevice? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
        } else {
            intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as? UsbDevice
        }
    }
}
