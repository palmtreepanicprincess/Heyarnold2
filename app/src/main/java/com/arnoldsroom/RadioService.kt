package com.arnoldsroom

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat

// Shared, process-wide view of the stream's state. The service writes
// it; the app's UI reads it (Compose state, so the radio pop-up updates
// itself when the stream connects, stops, or errors out).
object RadioState {
    var playing by mutableStateOf(false)
    var buffering by mutableStateOf(false)
}

// Nightwave Plaza as a foreground service (May, 2026-11-10): the radio
// keeps playing when she leaves the app, and the notification it lives
// behind can stop it. Without the service, Android would kill the
// stream soon after the app went to the background.
class RadioService : Service() {
    private var player: MediaPlayer? = null

    companion object {
        const val ACTION_PLAY = "com.arnoldsroom.radio.PLAY"
        const val ACTION_STOP = "com.arnoldsroom.radio.STOP"
        private const val CHANNEL_ID = "plaza_radio"
        private const val NOTIF_ID = 42
        private const val STREAM_URL = "https://radio.plaza.one/mp3"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(text: String): Notification {
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mgr.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Nightwave Plaza radio", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopIntent = PendingIntent.getService(
            this, 1,
            Intent(this, RadioService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle("Nightwave Plaza")
            .setContentText(text)
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopIntent)
            .setOngoing(true)
            .build()
    }

    private fun postNotification(text: String) {
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try { mgr.notify(NOTIF_ID, buildNotification(text)) } catch (e: Exception) { }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopStream()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                // PLAY (or a plain start): go foreground right away —
                // Android requires it within seconds of the start call.
                startForeground(NOTIF_ID, buildNotification("Connecting…"))
                if (player == null) startStream()
            }
        }
        return START_STICKY
    }

    private fun startStream() {
        RadioState.buffering = true
        try {
            val mp = MediaPlayer()
            mp.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mp.setDataSource(STREAM_URL)
            mp.setOnPreparedListener { p ->
                RadioState.buffering = false
                try {
                    p.start()
                    RadioState.playing = true
                    postNotification("● LIVE — vaporwave radio")
                } catch (e: Exception) { }
            }
            mp.setOnErrorListener { _, _, _ ->
                stopStream()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                true
            }
            player = mp
            mp.prepareAsync()
        } catch (e: Exception) {
            RadioState.buffering = false
        }
    }

    private fun stopStream() {
        try { player?.stop() } catch (e: Exception) { }
        try { player?.release() } catch (e: Exception) { }
        player = null
        RadioState.playing = false
        RadioState.buffering = false
    }

    override fun onDestroy() {
        stopStream()
        super.onDestroy()
    }
}
