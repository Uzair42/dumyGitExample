package  com.mu42.ftp
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.mu42.ftp.R
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.ftplet.UserManager
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.WritePermission
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class FtpServerService : Service() {
    private var ftpServer: FtpServer? = null
    private var serverIp: String? = null

    companion object {
        var isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        startFtpServer()
    }

    private fun createNotification(): Notification {
        val channelId = "ftp_server_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "FTP Server",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("FTP Server Running")
            .setContentText("Connected to $serverIp:2221")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun startFtpServer() {
        try {
            val serverFactory = FtpServerFactory()
            val listenerFactory = ListenerFactory().apply {
                port = 2221
            }

            serverFactory.addListener("default", listenerFactory.createListener())

            val userManager = serverFactory.userManager
            val user = BaseUser().apply {
                name = "anonymous"
                password = ""
                homeDirectory = getExternalFilesDir(null)?.absolutePath ?: ""
                authorities = listOf<Authority>(WritePermission())
            }

            (userManager as? UserManager)?.save(user)

            ftpServer = serverFactory.createServer()
            ftpServer?.start()

            serverIp = getLocalIpAddress()
            isRunning = true
        } catch (e: Exception) {
            Log.e("FTPServer", "Error starting server: ${e.message}")
        }
    }

    private fun getLocalIpAddress(): String {
        val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            for (addr in Collections.list(intf.inetAddresses)) {
                if (!addr.isLoopbackAddress && addr is Inet4Address) {
                    return addr.hostAddress ?: ""
                }
            }
        }
        return ""
    }

    override fun onDestroy() {
        ftpServer?.stop()
        isRunning = false
        super.onDestroy()
    }
}