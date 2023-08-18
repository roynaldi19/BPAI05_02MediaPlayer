package com.roynaldi19.dc4_05mediaplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.roynaldi19.dc4_05mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        const val TAG = "MainActivity"
    }

    private var mediaService: Messenger? = null

    private lateinit var boundServiceIntent: Intent
    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mediaService = null
            serviceBound = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mediaService = Messenger(service)
            serviceBound = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnPlay.setOnClickListener {
            if (serviceBound) {
                try {
                    mediaService?.send(Message.obtain(null, MediaService.PLAY, 0, 0))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }
        binding.btnStop.setOnClickListener {
            if (serviceBound) {
                try {
                    mediaService?.send(Message.obtain(null, MediaService.STOP, 0, 0))
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
        }

        boundServiceIntent = Intent(this@MainActivity, MediaService::class.java)
        boundServiceIntent.action = MediaService.ACTION_CREATE

        startService(boundServiceIntent)
        bindService(boundServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        unbindService(serviceConnection)
        boundServiceIntent.action = MediaService.ACTION_DESTROY

        startService(boundServiceIntent)
    }
}