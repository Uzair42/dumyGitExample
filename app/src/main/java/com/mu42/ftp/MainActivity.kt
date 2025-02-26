package com.mu42.ftp

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mu42.ftp.R

class MainActivity : AppCompatActivity() {
    private lateinit var btnToggle: Button
    //its comment in the project//
    private lateinit var tvStatus: TextView
    private lateinit var tvIpPort: TextView
    private lateinit var tvLog: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnToggle = findViewById(R.id.btnToggleServer)
        tvStatus = findViewById(R.id.tvStatus)
        tvIpPort = findViewById(R.id.tvIpPort)
        tvLog = findViewById(R.id.tvLog)

        btnToggle.setOnClickListener {
            if (FtpServerService.isRunning) {
                stopServer()
            } else {
                startServer()
            }
        }
    }

    private fun startServer() {
        val serviceIntent = Intent(this, FtpServerService::class.java)
        startService(serviceIntent)
        btnToggle.text = "Stop Server"
        tvStatus.text = "Server Status: Running"
    }

    private fun stopServer() {
        val serviceIntent = Intent(this, FtpServerService::class.java)
        stopService(serviceIntent)
        btnToggle.text = "Start Server"
        tvStatus.text = "Server Status: Stopped"
        tvIpPort.text = "IP: Not available\nPort: 2221"
    }
}