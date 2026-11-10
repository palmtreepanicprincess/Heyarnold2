package com.arnoldsroom

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer

// One song from the phone's own music library (MediaStore).
data class SongItem(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val uri: String
)

// Shared, process-wide view of the music player's state. The service
// writes it; the Compose UI (library, full player, pop-up mini
// player) reads it, so every surface stays in step with the one
// ExoPlayer living in the service.
object MusicState {
    var queue by mutableStateOf<List<SongItem>>(emptyList())
    var index by mutableStateOf(0)
    var playing by mutableStateOf(false)
    var hasMedia by mutableStateOf(false)
    var positionMs by mutableStateOf(0L)
    var durationMs by mutableStateOf(0L)
    var shuffle by mutableStateOf(false)
    var repeatMode by mutableStateOf(0) // 0 = off, 1 = all, 2 = one
    var speed by mutableStateOf(1f)
    var sleepMinutes by mutableStateOf(0)
    val current: SongItem? get() = queue.getOrNull(index)
}

// May's own music player (2026-11-10): plays the audio files on her
// phone with the same depth as the video player — queue, shuffle,
// repeat, speed, sleep timer, resume positions — and, like the
// radio, it lives in a foreground service so the music keeps playing
// when she leaves the app. The notification carries prev /
// play-pause / next / stop.
class MusicService : Service() {
    private var player: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var sleepRunnable: Runnable? = null
    private var tickCount = 0
    private var lastSongId = -1L

    companion object {
        const val ACTION_PLAY_QUEUE = "com.arnoldsroom.music.PLAY_QUEUE"
        const val ACTION_TOGGLE = "com.arnoldsroom.music.TOGGLE"
        const val ACTION_PAUSE = "com.arnoldsroom.music.PAUSE"
        const val ACTION_NEXT = "com.arnoldsroom.music.NEXT"
        const val ACTION_PREV = "com.arnoldsroom.music.PREV"
        const val ACTION_SEEK = "com.arnoldsroom.music.SEEK"
        const val ACTION_SET_SHUFFLE = "com.arnoldsroom.music.SET_SHUFFLE"
        const val ACTION_SET_REPEAT = "com.arnoldsroom.music.SET_REPEAT"
        const val ACTION_SET_SPEED = "com.arnoldsroom.music.SET_SPEED"
        const val ACTION_SET_SLEEP = "com.arnoldsroom.music.SET_SLEEP"
        const val ACTION_STOP = "com.arnoldsroom.music.STOP"
        const val EXTRA_INDEX = "index"
        const val EXTRA_POS = "pos"
        const val EXTRA_BOOL = "bool"
        const val EXTRA_INT = "int"
        const val EXTRA_FLOAT = "float"
        private const val CHANNEL_ID = "music_player"
        private const val NOTIF_ID = 43
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun prefs() = getSharedPreferences("arnolds_room", Context.MODE_PRIVATE)

    private fun savePos() {
        val p = player ?: return
        val song = MusicState.current ?: return
        try {
            val pos = p.currentPosition
            val dur = p.duration
            val ed = prefs().edit()
            if (dur > 0 && (dur - pos) < 5000) ed.remove("music_pos_${song.id}")
            else ed.putLong("music_pos_${song.id}", pos)
            ed.apply()
        } catch (e: Exception) { }
    }

    private val ticker = object : Runnable {
        override fun run() {
            val p = player
            if (p != null) {
                MusicState.positionMs = p.currentPosition.coerceAtLeast(0L)
                val d = p.duration
                if (d > 0) MusicState.durationMs = d
                if (++tickCount % 10 == 0) savePos()
            }
            handler.postDelayed(this, 500)
        }
    }

    private fun buildNotification(): Notification {
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mgr.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Music player", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val openIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        fun svcAction(action: String, code: Int): PendingIntent =
            PendingIntent.getService(
                this, code,
                Intent(this, MusicService::class.java).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        val song = MusicState.current
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(song?.title ?: "Music")
            .setContentText(song?.artist ?: "Arnold's Room music player")
            .setContentIntent(openIntent)
            .addAction(android.R.drawable.ic_media_previous, "Previous", svcAction(ACTION_PREV, 10))
            .addAction(
                if (MusicState.playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play,
                if (MusicState.playing) "Pause" else "Play",
                svcAction(ACTION_TOGGLE, 11)
            )
            .addAction(android.R.drawable.ic_media_next, "Next", svcAction(ACTION_NEXT, 12))
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", svcAction(ACTION_STOP, 13))
            .setOngoing(MusicState.playing)
            .build()
    }

    private fun postNotification() {
        val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try { mgr.notify(NOTIF_ID, buildNotification()) } catch (e: Exception) { }
    }

    private fun ensurePlayer(): ExoPlayer {
        player?.let { return it }
        val p = ExoPlayer.Builder(
            this,
            DefaultRenderersFactory(this).setEnableDecoderFallback(true)
        ).build()
        p.setWakeMode(C.WAKE_MODE_LOCAL)
        p.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            true
        )
        p.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val cur = player ?: return
                // The song changed: bank the old song's place, then
                // line up the new one's state.
                if (lastSongId != -1L && MusicState.queue.isNotEmpty()) {
                    try {
                        val prev = MusicState.queue.firstOrNull { it.id == lastSongId }
                        if (prev != null) {
                            val pos = MusicState.positionMs
                            val ed = prefs().edit()
                            if (prev.durationMs > 0 && (prev.durationMs - pos) < 5000) ed.remove("music_pos_${prev.id}")
                            else ed.putLong("music_pos_${prev.id}", pos)
                            ed.apply()
                        }
                    } catch (e: Exception) { }
                }
                MusicState.index = cur.currentMediaItemIndex
                MusicState.positionMs = 0L
                MusicState.current?.let {
                    lastSongId = it.id
                    MusicState.durationMs = it.durationMs
                }
                postNotification()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                MusicState.playing = isPlaying
                if (!isPlaying) savePos()
                postNotification()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    MusicState.playing = false
                    postNotification()
                }
                if (playbackState == Player.STATE_READY) {
                    val d = player?.duration ?: 0L
                    if (d > 0) MusicState.durationMs = d
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                // A file the phone can't decode: skip it rather than
                // dying in silence, when there's somewhere to skip to.
                val cur = player ?: return
                if (cur.hasNextMediaItem()) cur.seekToNextMediaItem() else MusicState.playing = false
            }
        })
        player = p
        return p
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP) {
            stopEverything()
            return START_NOT_STICKY
        }
        // Any other start must go foreground promptly (the app uses
        // startForegroundService for queue starts).
        try { startForeground(NOTIF_ID, buildNotification()) } catch (e: Exception) { }
        when (action) {
            ACTION_PLAY_QUEUE -> {
                val q = MusicState.queue
                if (q.isNotEmpty()) {
                    val p = ensurePlayer()
                    val idx = (intent.getIntExtra(EXTRA_INDEX, 0)).coerceIn(0, q.size - 1)
                    val pos = intent.getLongExtra(EXTRA_POS, 0L)
                    p.setMediaItems(q.map { MediaItem.fromUri(it.uri) }, idx, pos)
                    p.shuffleModeEnabled = MusicState.shuffle
                    p.repeatMode = when (MusicState.repeatMode) {
                        1 -> Player.REPEAT_MODE_ALL
                        2 -> Player.REPEAT_MODE_ONE
                        else -> Player.REPEAT_MODE_OFF
                    }
                    p.setPlaybackSpeed(MusicState.speed)
                    p.prepare()
                    p.play()
                    MusicState.hasMedia = true
                    MusicState.index = idx
                    MusicState.durationMs = q[idx].durationMs
                    lastSongId = q[idx].id
                    handler.removeCallbacks(ticker)
                    handler.post(ticker)
                    postNotification()
                }
            }
            ACTION_TOGGLE -> {
                val p = player
                if (p != null) {
                    if (p.isPlaying) p.pause() else p.play()
                }
            }
            ACTION_PAUSE -> player?.pause()
            ACTION_NEXT -> {
                val p = player
                if (p != null) {
                    if (p.hasNextMediaItem()) p.seekToNextMediaItem()
                    p.play()
                }
            }
            ACTION_PREV -> {
                val p = player
                if (p != null) {
                    // Standard music-player manners: more than a few
                    // seconds in, "previous" restarts the song.
                    if (p.currentPosition > 3000) p.seekTo(0)
                    else if (p.hasPreviousMediaItem()) p.seekToPreviousMediaItem()
                    else p.seekTo(0)
                    p.play()
                }
            }
            ACTION_SEEK -> {
                player?.seekTo(intent.getLongExtra(EXTRA_POS, 0L))
            }
            ACTION_SET_SHUFFLE -> {
                val on = intent.getBooleanExtra(EXTRA_BOOL, false)
                MusicState.shuffle = on
                player?.shuffleModeEnabled = on
            }
            ACTION_SET_REPEAT -> {
                val mode = intent.getIntExtra(EXTRA_INT, 0)
                MusicState.repeatMode = mode
                player?.repeatMode = when (mode) {
                    1 -> Player.REPEAT_MODE_ALL
                    2 -> Player.REPEAT_MODE_ONE
                    else -> Player.REPEAT_MODE_OFF
                }
            }
            ACTION_SET_SPEED -> {
                val f = intent.getFloatExtra(EXTRA_FLOAT, 1f)
                MusicState.speed = f
                player?.setPlaybackSpeed(f)
            }
            ACTION_SET_SLEEP -> {
                val mins = intent.getIntExtra(EXTRA_INT, 0)
                MusicState.sleepMinutes = mins
                sleepRunnable?.let { handler.removeCallbacks(it) }
                sleepRunnable = null
                if (mins > 0) {
                    val r = Runnable {
                        player?.pause()
                        MusicState.sleepMinutes = 0
                    }
                    sleepRunnable = r
                    handler.postDelayed(r, mins * 60_000L)
                }
            }
        }
        return START_STICKY
    }

    private fun stopEverything() {
        savePos()
        sleepRunnable?.let { handler.removeCallbacks(it) }
        handler.removeCallbacks(ticker)
        try { player?.stop() } catch (e: Exception) { }
        try { player?.release() } catch (e: Exception) { }
        player = null
        MusicState.playing = false
        MusicState.hasMedia = false
        MusicState.queue = emptyList()
        MusicState.index = 0
        MusicState.positionMs = 0L
        MusicState.durationMs = 0L
        MusicState.sleepMinutes = 0
        lastSongId = -1L
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        handler.removeCallbacks(ticker)
        try { player?.release() } catch (e: Exception) { }
        player = null
        super.onDestroy()
    }
}
