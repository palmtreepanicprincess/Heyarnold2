package com.arnoldsroom

import android.Manifest
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.util.Rational
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.RemoteViews
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.combinedClickable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionDisabled
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ---------- Theme colours pulled from the room ----------
val RoomDark = Color(0xFF12141F)
val RoomTeal = Color(0xFF57C4C7)
val RoomSky = Color(0xFF35B6E8)
val RoomOrange = Color(0xFFF26D21)
val RoomYellow = Color(0xFFFFC93C)
val RoomCard = Color(0xFF1D2130)
val RoomTextDim = Color(0xFFB9C2CC)

val CartoonFont = FontFamily(
    Font(R.font.baloo2, FontWeight.Normal),
    Font(R.font.baloo2, FontWeight.Medium),
    Font(R.font.baloo2_bold, FontWeight.SemiBold),
    Font(R.font.baloo2_bold, FontWeight.Bold)
)

val HandwrittenFont = FontFamily(Font(R.font.caveat))

fun cartoonTypography(): Typography {
    val b = Typography()
    return Typography(
        displayLarge = b.displayLarge.copy(fontFamily = CartoonFont),
        displayMedium = b.displayMedium.copy(fontFamily = CartoonFont),
        displaySmall = b.displaySmall.copy(fontFamily = CartoonFont),
        headlineLarge = b.headlineLarge.copy(fontFamily = CartoonFont),
        headlineMedium = b.headlineMedium.copy(fontFamily = CartoonFont),
        headlineSmall = b.headlineSmall.copy(fontFamily = CartoonFont),
        titleLarge = b.titleLarge.copy(fontFamily = CartoonFont),
        titleMedium = b.titleMedium.copy(fontFamily = CartoonFont),
        titleSmall = b.titleSmall.copy(fontFamily = CartoonFont),
        bodyLarge = b.bodyLarge.copy(fontFamily = CartoonFont),
        bodyMedium = b.bodyMedium.copy(fontFamily = CartoonFont),
        bodySmall = b.bodySmall.copy(fontFamily = CartoonFont),
        labelLarge = b.labelLarge.copy(fontFamily = CartoonFont),
        labelMedium = b.labelMedium.copy(fontFamily = CartoonFont),
        labelSmall = b.labelSmall.copy(fontFamily = CartoonFont)
    )
}

// ---------- Data ----------
data class VideoItem(
    val id: Long,
    val name: String,
    val title: String,
    val uri: Uri,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAddedSec: Long,
    val folder: String,
    val relPath: String = ""
)

enum class Filter(val label: String) {
    ALL("All Videos"), MOVIES("Movies"), TV("TV Shows"), FAVORITES("Favorites"),
    CONTINUE("Continue Watching"), RECENT("Recently Added"), UNSORTED("Unsorted")
}

enum class AssignCat { MOVIE, TV, NONE }

sealed class Screen {
    object Room : Screen()
    data class Library(val filter: Filter) : Screen()
    object Categories : Screen()
    object Shows : Screen()
    data class ShowDetail(val showTitle: String) : Screen()
    object MovieFolders : Screen()
    data class FolderDetail(val key: String) : Screen()
    object Search : Screen()
    object Settings : Screen()
    object Stats : Screen()
    data class Player(val queue: List<VideoItem>, val startIndex: Int, val fromLabel: String) : Screen()
}

fun fmtDur(ms: Long): String {
    if (ms <= 0) return "0:00"
    val totalSec = ms / 1000
    val h = totalSec / 3600
    val m = (totalSec % 3600) / 60
    val s = totalSec % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

fun fmtSize(bytes: Long): String {
    if (bytes <= 0) return ""
    val mb = bytes / 1_000_000.0
    return if (mb >= 1000) "%.1f GB".format(mb / 1000.0) else "%.0f MB".format(mb)
}

private val episodeRegex = Regex("(?i)s\\d{1,2}e\\d{1,2}")

fun heuristicCat(v: VideoItem): AssignCat {
    val f = v.folder.lowercase()
    return when {
        episodeRegex.containsMatchIn(v.name) -> AssignCat.TV
        f.contains("season") || f.contains("series") || f.contains("/tv") || f.contains("shows") -> AssignCat.TV
        f.contains("movie") || f.contains("film") || f.contains("cinema") -> AssignCat.MOVIE
        else -> AssignCat.NONE
    }
}

// Personal clips (screen recordings, live wallpapers, camera clips) are not
// entertainment: they live in tell-tale folders, carry tell-tale names, or
// run under a minute — no episode or movie does.
private val junkFolderSegs = setOf(
    "screen recordings", "screen recording", "screenrecorder", "screen recorder",
    "screenshots", "screenshot", "camera", "dcim", "wallpaper", "wallpapers",
    "live wallpaper", "live wallpapers", "stories", "cache", ".thumbnails"
)

fun isJunkClip(v: VideoItem): Boolean {
    if (pathSegments(v).any { it.lowercase() in junkFolderSegs }) return true
    val n = v.name.lowercase()
    if (n.startsWith("screen_recording") || n.startsWith("screen recording") ||
        n.startsWith("screencast") || n.startsWith("lv_")
    ) return true
    if (v.durationMs in 1..59_999L) return true
    return false
}

// ---------- Folder auto-detection: shows, seasons, movie folders ----------
private val seasonSegRegex = Regex("(?i)^(?:season|series)[\\s_]*(\\d{1,2})$|^s(\\d{1,2})$")
private val epCodeRegex = Regex("(?i)s(\\d{1,2})e(\\d{1,3})")
private val genericSegs = setOf(
    "movies", "movie", "films", "film", "cinema", "tv", "tv shows", "shows", "series",
    "download", "downloads", "video", "videos", "media", "storage", "emulated", "0",
    "sdcard", "internal storage"
)

fun cleanTitle(raw: String): String {
    var t = raw.substringBeforeLast('.', raw)
    t = t.replace(Regex("\\[.*?\\]"), " ")
    t = t.replace(Regex("(?i)\\b(720p|1080p|2160p|480p|bluray|blu-ray|webrip|web-dl|hdrip|dvdrip|x264|x265|h264|h265|hevc|aac|mp4|mkv|avi)\\b"), " ")
    t = t.replace(Regex("[._]+"), " ")
    t = t.replace(Regex("\\s+"), " ").trim(' ', '-')
    return t.ifBlank { raw }
}

fun pathSegments(v: VideoItem): List<String> =
    v.relPath.split('/').map { it.trim() }.filter { it.isNotEmpty() }

fun seasonOfSegment(seg: String): Int? =
    seasonSegRegex.find(seg.trim())?.let { m ->
        (m.groupValues[1].ifEmpty { m.groupValues[2] }).toIntOrNull()
    }

fun showTitleOf(v: VideoItem): String {
    val segs = pathSegments(v)
    val sIdx = segs.indexOfFirst { seasonOfSegment(it) != null }
    if (sIdx > 0) {
        val cand = segs[sIdx - 1]
        if (cand.lowercase() !in genericSegs) return cleanTitle(cand)
    }
    epCodeRegex.find(v.name)?.let { m ->
        val t = cleanTitle(v.name.substring(0, m.range.first))
        if (t.isNotBlank()) return t
    }
    val last = segs.lastOrNull { it.lowercase() !in genericSegs }
    if (last != null) return cleanTitle(last)
    return "Unknown Show"
}

fun seasonOf(v: VideoItem): Int {
    for (s in pathSegments(v)) seasonOfSegment(s)?.let { return it }
    epCodeRegex.find(v.name)?.let { return it.groupValues[1].toIntOrNull() ?: 1 }
    return 1
}

fun episodeKey(v: VideoItem): Int {
    epCodeRegex.find(v.name)?.let { return it.groupValues[2].toIntOrNull() ?: Int.MAX_VALUE }
    val nums = Regex("\\d+").findAll(v.title).mapNotNull { it.value.toIntOrNull() }.toList()
    return nums.lastOrNull() ?: Int.MAX_VALUE
}

data class ShowInfo(val title: String, val seasons: Map<Int, List<VideoItem>>)

fun detectShows(videos: List<VideoItem>, catOf: (VideoItem) -> AssignCat): List<ShowInfo> =
    videos.filter { catOf(it) == AssignCat.TV }
        .groupBy { showTitleOf(it) }
        .map { (title, eps) ->
            ShowInfo(
                title,
                eps.groupBy { seasonOf(it) }.toSortedMap()
                    .mapValues { (_, list) ->
                        list.sortedWith(compareBy({ episodeKey(it) }, { it.title.lowercase() }))
                    }
            )
        }
        .sortedBy { it.title.lowercase() }

data class MovieFolder(val key: String, val title: String, val items: List<VideoItem>)

fun movieFolderKey(v: VideoItem): String {
    val segs = pathSegments(v)
    val last = segs.lastOrNull()
    return if (last != null && (segs.size >= 2 || last.lowercase() !in genericSegs)) "dir:$last"
    else "file:${v.id}"
}

fun detectMovieFolders(videos: List<VideoItem>, catOf: (VideoItem) -> AssignCat): List<MovieFolder> =
    videos.filter { catOf(it) == AssignCat.MOVIE }
        .groupBy { movieFolderKey(it) }
        .map { (key, items) ->
            val title = if (key.startsWith("dir:")) cleanTitle(key.removePrefix("dir:"))
            else cleanTitle(items.first().title)
            MovieFolder(key, title, items.sortedBy { it.title.lowercase() })
        }
        .sortedBy { it.title.lowercase() }

fun loadVideos(resolver: ContentResolver): List<VideoItem> {
    val out = mutableListOf<VideoItem>()
    val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    val projection = mutableListOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.DATE_ADDED
    )
    if (Build.VERSION.SDK_INT >= 29) projection.add(MediaStore.Video.Media.RELATIVE_PATH)
    else projection.add(MediaStore.Video.Media.DATA)
    val sort = MediaStore.Video.Media.DATE_ADDED + " DESC"
    resolver.query(collection, projection.toTypedArray(), null, null, sort)?.use { c ->
        val idI = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameI = c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durI = c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val sizeI = c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
        val dateI = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
        val pathI = if (Build.VERSION.SDK_INT >= 29)
            c.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
        else c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        while (c.moveToNext()) {
            val id = c.getLong(idI)
            val name = c.getString(nameI) ?: "Video $id"
            val path = c.getString(pathI) ?: ""
            val folder = path.trim('/').substringBeforeLast('/', path.trim('/'))
            val relPath = if (Build.VERSION.SDK_INT >= 29) path else path.substringBeforeLast('/', "")
            out.add(
                VideoItem(
                    id = id,
                    name = name,
                    title = name.substringBeforeLast('.', name),
                    uri = Uri.withAppendedPath(collection, id.toString()),
                    durationMs = c.getLong(durI),
                    sizeBytes = c.getLong(sizeI),
                    dateAddedSec = c.getLong(dateI),
                    folder = folder,
                    relPath = relPath
                )
            )
        }
    }
    return out
}

fun videoPermission(): String =
    if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO
    else Manifest.permission.READ_EXTERNAL_STORAGE

object PipControl {
    var shouldEnter by mutableStateOf(false)
    var inPip by mutableStateOf(false)
}

// Set by the home-screen widget (or a fresh launch) when it wants the app
// to jump straight back into the last-watched video.
object ContinueRequest {
    var videoId by mutableStateOf(-1L)
}

class ContinueWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val prefs = context.getSharedPreferences("arnolds_room", Context.MODE_PRIVATE)
        val title = prefs.getString("last_title", null) ?: "Nothing yet — watch something first"
        val lastId = prefs.getLong("last_id", -1L)
        val lastPos = prefs.getLong("last_pos", 0L)
        val lastDur = prefs.getLong("last_dur", 0L)
        for (id in ids) {
            val views = RemoteViews(context.packageName, R.layout.widget_continue)
            views.setTextViewText(R.id.widget_title, title)
            if (lastDur > 0L && lastPos > 0L) {
                views.setViewVisibility(R.id.widget_progress, android.view.View.VISIBLE)
                views.setProgressBar(
                    R.id.widget_progress, 1000,
                    (lastPos * 1000L / lastDur).toInt().coerceIn(0, 1000), false
                )
                views.setTextViewText(R.id.widget_left, "Left off at ${fmtDur(lastPos)} — tap to jump back in!")
            } else {
                views.setViewVisibility(R.id.widget_progress, android.view.View.GONE)
                views.setTextViewText(R.id.widget_left, "Tap to step into the room")
            }
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("continue_video_id", lastId)
            }
            val pending = PendingIntent.getActivity(
                context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pending)
            manager.updateAppWidget(id, views)
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let { ContinueRequest.videoId = it.getLongExtra("continue_video_id", -1L) }
        setContent { ArnoldsRoomApp() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ContinueRequest.videoId = intent.getLongExtra("continue_video_id", -1L)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && PipControl.shouldEnter) {
            try {
                enterPictureInPictureMode(
                    PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .build()
                )
            } catch (e: Exception) {
                // Some devices refuse PiP; playback simply stays in the app.
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        PipControl.inPip = isInPictureInPictureMode
    }
}

// ---------- App root ----------
@Composable
fun ArnoldsRoomApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("arnolds_room", Context.MODE_PRIVATE) }
    var screen by remember { mutableStateOf<Screen>(Screen.Room) }
    var nightOverride by remember { mutableStateOf<Boolean?>(null) }
    val night = nightOverride ?: isNightNow()
    var videos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, videoPermission()) == PackageManager.PERMISSION_GRANTED
        )
    }
    var pendingDest by remember { mutableStateOf<Screen?>(null) }
    var pendingShuffle by remember { mutableStateOf(false) }
    var miniQueue by remember { mutableStateOf<List<VideoItem>?>(null) }
    var miniIndex by remember { mutableStateOf(0) }
    var musicOn by remember { mutableStateOf(true) }
    var doorGone by remember { mutableStateOf(false) }

    // ----- Room music: plays on the room screen, pauses on any tap / when the app is left -----
    var music by remember { mutableStateOf<MediaPlayer?>(null) }
    fun startMusic() {
        try {
            if (music == null) {
                val songId = context.resources.getIdentifier("football_head", "raw", context.packageName)
                if (songId != 0) {
                    music = MediaPlayer.create(context, songId)?.apply { isLooping = true }
                }
            }
            music?.let { if (!it.isPlaying) it.start() }
        } catch (e: Exception) { /* music is a bonus, never crash over it */ }
    }
    fun pauseMusic() {
        try { music?.let { if (it.isPlaying) it.pause() } } catch (e: Exception) { }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, screen) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (screen is Screen.Room && miniQueue == null && musicOn && doorGone) startMusic()
                Lifecycle.Event.ON_STOP -> pauseMusic()
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(screen, musicOn, miniQueue, doorGone) {
        // The song waits until the doors have fully opened — no music
        // while you're still standing outside knocking.
        if (screen is Screen.Room && musicOn && miniQueue == null && doorGone) {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) startMusic()
        } else pauseMusic()
    }
    // Status bar hides on the room screen so nothing covers the room; back everywhere else.
    val activity = context as? Activity
    LaunchedEffect(screen) {
        val act = activity ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
        if (screen is Screen.Room) controller.hide(WindowInsetsCompat.Type.statusBars())
        else controller.show(WindowInsetsCompat.Type.statusBars())
    }
    DisposableEffect(Unit) { onDispose { try { music?.release() } catch (e: Exception) { } } }

    // ----- Permission + library load -----
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        granted = ok
        if (ok) {
            pendingDest?.let { screen = it }
            pendingDest = null
        }
    }
    LaunchedEffect(granted) {
        if (granted) videos = withContext(Dispatchers.IO) { loadVideos(context.contentResolver) }
    }
    // The home-screen widget asks to jump straight back into a video.
    LaunchedEffect(ContinueRequest.videoId, videos) {
        val id = ContinueRequest.videoId
        if (id != -1L && videos.isNotEmpty()) {
            videos.firstOrNull { it.id == id }?.let { v ->
                pauseMusic()
                miniQueue = null
                screen = Screen.Player(listOf(v), 0, "Continue")
            }
            ContinueRequest.videoId = -1L
        }
    }

    fun go(dest: Screen, needsVideos: Boolean = true) {
        pauseMusic()
        if (needsVideos && !granted) {
            pendingDest = dest
            launcher.launch(videoPermission())
        } else {
            screen = dest
        }
    }

    fun catOf(v: VideoItem): AssignCat {
        val o = prefs.getString("cat_${v.id}", null)
        return when (o) {
            "MOVIE" -> AssignCat.MOVIE
            "TV" -> AssignCat.TV
            else -> heuristicCat(v)
        }
    }
    fun isEntertainment(v: VideoItem): Boolean {
        val o = prefs.getString("cat_${v.id}", null)
        if (o == "MOVIE" || o == "TV") return true
        return !isJunkClip(v)
    }
    fun isFav(v: VideoItem) = prefs.getBoolean("fav_${v.id}", false)
    fun isWatched(v: VideoItem) = prefs.getBoolean("watched_${v.id}", false)
    var libVersion by remember { mutableStateOf(0) }
    fun toggleWatched(v: VideoItem) {
        prefs.edit().putBoolean("watched_${v.id}", !isWatched(v)).apply()
        libVersion++
    }
    fun savedPos(v: VideoItem) = prefs.getLong("pos_${v.id}", 0L)

    // Shuffle asked for permission first: once the library actually
    // arrives, finish the shuffle instead of leaving her in the room.
    LaunchedEffect(videos, pendingShuffle) {
        if (pendingShuffle && videos.isNotEmpty()) {
            pendingShuffle = false
            pauseMusic()
            val pool = videos.filter { isEntertainment(it) }
            val src = if (pool.isNotEmpty()) pool else videos
            screen = Screen.Player(src, src.indices.random(), "Shuffle")
        }
    }

    fun filtered(filter: Filter): List<VideoItem> = when (filter) {
        Filter.ALL -> videos
        Filter.MOVIES -> videos.filter { catOf(it) == AssignCat.MOVIE }
        Filter.TV -> videos.filter { catOf(it) == AssignCat.TV }
        Filter.FAVORITES -> videos.filter { isFav(it) }
        Filter.UNSORTED -> videos.filter { catOf(it) == AssignCat.NONE && isEntertainment(it) }
        Filter.RECENT -> videos.filter { isEntertainment(it) }.take(30)
        Filter.CONTINUE -> videos.filter {
            val p = savedPos(it); it.durationMs > 0 && p > 10_000 && p < it.durationMs * 95 / 100 && isEntertainment(it)
        }
    }

    BackHandler(enabled = screen !is Screen.Room) {
        screen = if (screen is Screen.Player) Screen.Library(Filter.ALL) else Screen.Room
    }

    val libTick = libVersion
    MaterialTheme(typography = cartoonTypography()) {
    Box(Modifier.fillMaxSize().background(RoomDark)) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                (fadeIn(tween(240)) + scaleIn(initialScale = 0.985f, animationSpec = tween(240))) togetherWith
                    fadeOut(tween(160))
            },
            label = "screen"
        ) { s ->
            when (s) {
            is Screen.Room -> RoomScreen(
                night = night,
                onToggleNight = { nightOverride = !night },
                musicOn = musicOn,
                onToggleMusic = { musicOn = !musicOn },
                onHotspot = { dest -> go(dest) },
                onShuffle = {
                    pauseMusic()
                    if (!granted) {
                        pendingShuffle = true
                        launcher.launch(videoPermission())
                    } else if (videos.isNotEmpty()) {
                        val pool = videos.filter { isEntertainment(it) }
                        val src = if (pool.isNotEmpty()) pool else videos
                        val pick = src.indices.random()
                        screen = Screen.Player(src, pick, "Shuffle")
                    }
                }
            )
            is Screen.Categories -> CategoriesScreen(
                counts = Filter.values().associateWith { filtered(it).size },
                showsCount = detectShows(videos, ::catOf).size,
                movieFolderCount = detectMovieFolders(videos, ::catOf).size,
                onPick = { screen = Screen.Library(it) },
                onShows = { screen = Screen.Shows },
                onMovieFolders = { screen = Screen.MovieFolders },
                onRoom = { screen = Screen.Room }
            )
            is Screen.Shows -> ShowsScreen(
                shows = detectShows(videos, ::catOf),
                isWatched = ::isWatched,
                savedPos = ::savedPos,
                onPick = { screen = Screen.ShowDetail(it) },
                onRoom = { screen = Screen.Room }
            )
            is Screen.ShowDetail -> {
                val show = detectShows(videos, ::catOf).firstOrNull { it.title == s.showTitle }
                if (show != null) {
                    ShowDetailScreen(
                        show = show,
                        isFav = ::isFav,
                        isWatched = ::isWatched,
                        savedPos = ::savedPos,
                        onToggleWatched = ::toggleWatched,
                        onPlay = { queue, idx -> screen = Screen.Player(queue, idx, s.showTitle) },
                        onRoom = { screen = Screen.Room }
                    )
                } else {
                    ShowsScreen(
                        shows = detectShows(videos, ::catOf),
                        isWatched = ::isWatched,
                        savedPos = ::savedPos,
                        onPick = { screen = Screen.ShowDetail(it) },
                        onRoom = { screen = Screen.Room }
                    )
                }
            }
            is Screen.MovieFolders -> MovieFoldersScreen(
                folders = detectMovieFolders(videos, ::catOf),
                onPick = { screen = Screen.FolderDetail(it) },
                onRoom = { screen = Screen.Room }
            )
            is Screen.FolderDetail -> {
                val folder = detectMovieFolders(videos, ::catOf).firstOrNull { it.key == s.key }
                LibraryScreen(
                    title = folder?.title ?: "Movies",
                    items = folder?.items ?: emptyList(),
                    prefs = prefs,
                    catOf = ::catOf,
                    isFav = ::isFav,
                    savedPos = ::savedPos,
                    onPlay = { queue, idx -> screen = Screen.Player(queue, idx, folder?.title ?: "Movies") },
                    onRoom = { screen = Screen.Room }
                )
            }
            is Screen.Library -> LibraryScreen(
                title = s.filter.label,
                items = filtered(s.filter),
                prefs = prefs,
                catOf = ::catOf,
                isFav = ::isFav,
                savedPos = ::savedPos,
                onPlay = { queue, idx -> screen = Screen.Player(queue, idx, s.filter.label) },
                onRoom = { screen = Screen.Room },
                onStats = { screen = Screen.Stats }
            )
            is Screen.Search -> SearchScreen(
                items = videos,
                prefs = prefs,
                catOf = ::catOf,
                isFav = ::isFav,
                savedPos = ::savedPos,
                onPlay = { queue, idx -> screen = Screen.Player(queue, idx, "Search") },
                onRoom = { screen = Screen.Room }
            )
            is Screen.Settings -> SettingsScreen(
                videoCount = videos.size,
                granted = granted,
                onGrant = { launcher.launch(videoPermission()) },
                onClearResume = {
                    val ed = prefs.edit()
                    videos.forEach { ed.remove("pos_${it.id}") }
                    ed.apply()
                },
                onRoom = { screen = Screen.Room }
            )
            is Screen.Stats -> StatsScreen(
                videos = videos,
                prefs = prefs,
                isWatched = ::isWatched,
                savedPos = ::savedPos,
                showsCount = detectShows(videos, ::catOf).size,
                onRoom = { screen = Screen.Room }
            )
            is Screen.Player -> PlayerScreen(
                queue = s.queue,
                startIndex = s.startIndex,
                fromLabel = s.fromLabel,
                prefs = prefs,
                onBack = { screen = Screen.Library(Filter.ALL) },
                onShrink = { idx ->
                    miniQueue = s.queue
                    miniIndex = idx
                    screen = Screen.Room
                }
            )
            }
        }
        if (screen is Screen.Room) {
            miniQueue?.let { q ->
                MiniPlayerBox(
                    queue = q,
                    startIndex = miniIndex,
                    prefs = prefs,
                    onExpand = { idx ->
                        miniQueue = null
                        screen = Screen.Player(q, idx, "Mini player")
                    },
                    onClose = { miniQueue = null }
                )
            }
        }
        if (!doorGone) {
            DoorIntro(onDone = { doorGone = true })
            // The music button stays on top of the doors, so she can hush
            // the song before she even steps into the room.
            Column(
                Modifier.align(Alignment.TopStart).padding(12.dp)
                    .clickable { musicOn = !musicOn },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(46.dp).shadow(6.dp, CircleShape).clip(CircleShape)
                        .background(Color(0xCC10131A))
                        .border(2.5.dp, RoomYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (musicOn) Icons.Filled.MusicNote else Icons.Filled.MusicOff,
                        if (musicOn) "Pause music" else "Play music",
                        tint = if (musicOn) RoomYellow else RoomTextDim,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    if (musicOn) "Music" else "Muted",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xAA10131A))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
    }
}


fun findDrawableId(context: Context, exact: String): Int {
    val direct = context.resources.getIdentifier(exact, "drawable", context.packageName)
    if (direct != 0) return direct
    return try {
        val cls = Class.forName(context.packageName + ".R\$drawable")
        cls.fields.firstOrNull { it.name.endsWith(exact) }?.getInt(null) ?: 0
    } catch (e: Exception) { 0 }
}

fun isNightNow(): Boolean {
    val h = java.time.LocalTime.now().hour
    return h >= 19 || h < 7
}

@Composable
fun rememberRoomBitmap(tall: Boolean, night: Boolean): Bitmap? {
    val context = LocalContext.current
    return remember(tall, night) {
        try {
            val names = when {
                tall && night -> listOf("arnolds_room_tall_night", "file_00000000f44881f585d18819f3427bf8", "arnolds_room_tall", "file_00000000266481f58cd2d715ef42fabe", "arnolds_room_night", "file_00000000395c81f5987ceacf0dab6e59", "arnolds_room")
                tall -> listOf("arnolds_room_tall", "file_00000000266481f58cd2d715ef42fabe", "arnolds_room")
                night -> listOf("arnolds_room_night", "file_00000000395c81f5987ceacf0dab6e59", "arnolds_room")
                else -> listOf("file_00000000ed6081f586fc3d5ad2e02ce3", "arnolds_room_wide", "arnolds_room")
            }
            var bmp: Bitmap? = null
            for (n in names) {
                val id = findDrawableId(context, n)
                if (id != 0) {
                    bmp = android.graphics.BitmapFactory.decodeResource(context.resources, id)
                    if (bmp != null) break
                }
            }
            bmp
        } catch (e: Exception) { null }
    }
}

// ---------- The Room (home) ----------
data class Hotspot(
    val fx: Float, val fy: Float, val label: String, val icon: ImageVector, val dest: Screen?,
    val color: Color
)

fun cropAround(src: Bitmap, fx: Float, fy: Float, frac: Float = 0.17f): Bitmap {
    val side = (src.width * frac).toInt().coerceAtLeast(24)
    val cx = (src.width * fx).toInt()
    val cy = (src.height * fy).toInt()
    val x = (cx - side / 2).coerceIn(0, (src.width - side).coerceAtLeast(0))
    val y = (cy - side / 2).coerceIn(0, (src.height - side).coerceAtLeast(0))
    val s2 = minOf(side, src.width - x, src.height - y)
    return Bitmap.createBitmap(src, x, y, s2, s2)
}

// Each hotspot is drawn as an outline of the thing it stands for — a TV
// outline over the TV, a bed over the bed, a door over the door — traced
// twice: a dark halo so it reads on the busy painting, then the color.
@Composable
fun RoomChip(spot: Hotspot, art: Bitmap?, imgWpx: Int, index: Int, tapped: Boolean, compact: Boolean, pulse: Float, onTap: () -> Unit) {
    // The chip wears the room: a circle cut out of the painting itself,
    // one-to-one, centered on the spot — so the button IS the thing it opens.
    // Around it, just enough dressing to lift it off the wall: a soft glow
    // in the spot's own color, a drop shadow, and a two-tone rim (a dark
    // ink ring outside, the spot's color inside). The glow breathes with
    // the room's pulse until she taps something.
    val density = LocalDensity.current
    val chipDp = if (compact) 52.dp else 68.dp
    val frac = with(density) { chipDp.toPx() } / imgWpx.coerceAtLeast(1).toFloat()
    val crop = remember(art, frac, spot.fx, spot.fy) {
        art?.let { cropAround(it, spot.fx, spot.fy, frac) }
    }
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 70L); shown = true }
    val scale = animateFloatAsState(
        if (shown) 1f else 0.3f,
        spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow), label = "chipPop"
    )
    val color = if (tapped) RoomYellow else spot.color
    val glowAlpha = 0.16f + 0.30f * pulse
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(chipDp)
                .scale(if (tapped) scale.value * 1.18f else scale.value)
                .alpha(if (shown) 1f else 0f),
            contentAlignment = Alignment.Center
        ) {
            // The halo: the spot's color, breathed out around the circle.
            Box(
                Modifier
                    .size(chipDp)
                    .scale(1.5f)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            listOf(color.copy(alpha = glowAlpha), Color.Transparent)
                        )
                    )
            )
            // The circle itself: shadow first so it floats, then the dark
            // ink ring, then inside it the painting crop with its color rim.
            Box(
                Modifier
                    .size(chipDp)
                    .shadow(7.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color(0xFF10131A))
                    .clickable { onTap() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    Modifier
                        .size(chipDp - 5.dp)
                        .clip(CircleShape)
                        .border(2.dp, color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (crop != null) {
                        Image(
                            bitmap = crop.asImageBitmap(),
                            contentDescription = spot.label,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(3.dp))
        Text(
            spot.label,
            color = Color(0xFF10131A),
            fontSize = if (compact) 13.sp else 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background((if (tapped) RoomYellow else color).copy(alpha = 0.95f))
                .padding(horizontal = if (compact) 6.dp else 5.dp, vertical = if (compact) 1.dp else 0.dp)
        )
    }
}
// thump plus a woody click that both die away in about a tenth of a second.
fun buildKnockTrack(): AudioTrack {
    val sampleRate = 44100
    val frames = (sampleRate * 0.11).toInt()
    val pcm = ShortArray(frames)
    val rnd = kotlin.random.Random(7)
    for (i in 0 until frames) {
        val t = i / sampleRate.toDouble()
        val thump = kotlin.math.sin(2.0 * Math.PI * 155.0 * t) * kotlin.math.exp(-t * 48.0)
        val click = (rnd.nextDouble() * 2.0 - 1.0) * kotlin.math.exp(-t * 160.0)
        val v = (thump * 0.75 + click * 0.30).coerceIn(-1.0, 1.0)
        pcm[i] = (v * Short.MAX_VALUE).toInt().toShort()
    }
    val track = AudioTrack.Builder()
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .setAudioFormat(
            AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()
        )
        .setBufferSizeInBytes(pcm.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()
    track.write(pcm, 0, pcm.size)
    return track
}

@Composable
fun DoorIntro(onDone: () -> Unit) {
    val slide = remember { Animatable(0f) }
    val nudge = remember { Animatable(0f) }
    var knocks by remember { mutableStateOf(0) }
    var opening by remember { mutableStateOf(false) }
    val knockTrack = remember { buildKnockTrack() }
    DisposableEffect(Unit) { onDispose { knockTrack.release() } }
    fun knock() {
        try {
            if (knockTrack.playState == AudioTrack.PLAYSTATE_PLAYING) knockTrack.stop()
            knockTrack.reloadStaticData()
            knockTrack.play()
        } catch (e: Exception) {
            // No knock sound on this device; the doors still work.
        }
    }
    LaunchedEffect(knocks) {
        if (knocks == 1) {
            nudge.animateTo(7f, tween(70))
            nudge.animateTo(-4f, tween(70))
            nudge.animateTo(0f, tween(80))
        }
    }
    LaunchedEffect(opening) {
        if (opening) {
            slide.animateTo(1f, tween(950, easing = FastOutSlowInEasing))
            onDone()
        }
    }
    Box(
        Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures(onTap = {
                if (!opening) {
                    knock()
                    knocks++
                    if (knocks >= 2) opening = true
                }
            })
        }
    ) {
        Row(Modifier.fillMaxSize()) {
            DoorPanel(knobOnRight = true, toLeft = true, progress = slide.value, nudgePx = nudge.value)
            DoorPanel(knobOnRight = false, toLeft = false, progress = slide.value, nudgePx = nudge.value)
        }
        Text(
            "knock twice to come in",
            fontFamily = HandwrittenFont,
            color = Color(0xFFFFF3E0).copy(alpha = 0.85f),
            fontSize = 17.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 26.dp)
        )
    }
}

@Composable
fun RowScope.DoorPanel(knobOnRight: Boolean, toLeft: Boolean, progress: Float, nudgePx: Float = 0f) {
    Box(
        Modifier.fillMaxHeight().weight(1f)
            .graphicsLayer {
                translationX = (if (toLeft) -1f else 1f) * progress * this.size.width +
                    (if (toLeft) nudgePx else -nudgePx)
            }
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color(0xFF4A6FA5), Color(0xFF2F4E79))
                )
            )
            .border(2.dp, Color(0xFF22364F))
    ) {
        Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.SpaceEvenly) {
            Box(
                Modifier.fillMaxWidth().weight(1f).padding(6.dp)
                    .border(3.dp, Color(0xFF22364F)).background(Color(0xFF557DB2))
            )
            Box(
                Modifier.fillMaxWidth().weight(1f).padding(6.dp)
                    .border(3.dp, Color(0xFF22364F)).background(Color(0xFF557DB2))
            )
        }
        Box(
            Modifier
                .align(if (knobOnRight) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(horizontal = 10.dp)
                .size(15.dp).shadow(3.dp, CircleShape).clip(CircleShape)
                .background(Color(0xFFFFC93C))
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomScreen(onHotspot: (Screen) -> Unit, onShuffle: () -> Unit, night: Boolean, onToggleNight: () -> Unit, musicOn: Boolean, onToggleMusic: () -> Unit) {
    val spots = listOf(
        Hotspot(0.50f, 0.10f, "New", Icons.Filled.History, Screen.Library(Filter.RECENT), Color(0xFFFF8F00)),
        Hotspot(0.13f, 0.44f, "Search", Icons.Filled.Search, Screen.Search, Color(0xFF2979FF)),
        Hotspot(0.47f, 0.36f, "Shelves", Icons.Filled.Folder, Screen.Categories, Color(0xFF7C4DFF)),
        Hotspot(0.635f, 0.395f, "TV", Icons.Filled.Tv, Screen.Library(Filter.ALL), Color(0xFFFF5722)),
        Hotspot(0.55f, 0.53f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE), Color(0xFF00C853)),
        Hotspot(0.075f, 0.66f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES), Color(0xFFF50057)),
        Hotspot(0.685f, 0.685f, "Shuffle", Icons.Filled.Shuffle, null, Color(0xFF00ACC1)),
        Hotspot(0.915f, 0.47f, "Settings", Icons.Filled.Settings, Screen.Settings, Color(0xFFFF5252))
    )
    // Hotspots measured on the new wide daytime painting (the long room view).
    val wideDaySpots = listOf(
        Hotspot(0.505f, 0.135f, "New", Icons.Filled.History, Screen.Library(Filter.RECENT), Color(0xFFFF8F00)),
        Hotspot(0.124f, 0.501f, "Search", Icons.Filled.Search, Screen.Search, Color(0xFF2979FF)),
        Hotspot(0.40f, 0.395f, "Shelves", Icons.Filled.Folder, Screen.Categories, Color(0xFF7C4DFF)),
        Hotspot(0.650f, 0.400f, "TV", Icons.Filled.Tv, Screen.Library(Filter.ALL), Color(0xFFFF5722)),
        Hotspot(0.548f, 0.518f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE), Color(0xFF00C853)),
        Hotspot(0.075f, 0.653f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES), Color(0xFFF50057)),
        Hotspot(0.708f, 0.682f, "Shuffle", Icons.Filled.Shuffle, null, Color(0xFF00ACC1)),
        Hotspot(0.912f, 0.534f, "Settings", Icons.Filled.Settings, Screen.Settings, Color(0xFFFF5252))
    )
    val tallSpots = listOf(
        Hotspot(0.50f, 0.10f, "New", Icons.Filled.History, Screen.Library(Filter.RECENT), Color(0xFFFF8F00)),
        Hotspot(0.122f, 0.461f, "Search", Icons.Filled.Search, Screen.Search, Color(0xFF2979FF)),
        Hotspot(0.344f, 0.404f, "Shelves", Icons.Filled.Folder, Screen.Categories, Color(0xFF7C4DFF)),
        Hotspot(0.614f, 0.413f, "TV", Icons.Filled.Tv, Screen.Library(Filter.ALL), Color(0xFFFF5722)),
        Hotspot(0.516f, 0.472f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE), Color(0xFF00C853)),
        Hotspot(0.071f, 0.535f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES), Color(0xFFF50057)),
        Hotspot(0.680f, 0.530f, "Shuffle", Icons.Filled.Shuffle, null, Color(0xFF00ACC1)),
        Hotspot(0.890f, 0.470f, "Settings", Icons.Filled.Settings, Screen.Settings, Color(0xFFFF5252))
    )
    // Shake the phone while you're in the room and Shuffle plays something.
    val sensorContext = LocalContext.current
    DisposableEffect(Unit) {
        val sm = sensorContext.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accel = sm?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var lastShake = 0L
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                val gx = e.values[0] / SensorManager.GRAVITY_EARTH
                val gy = e.values[1] / SensorManager.GRAVITY_EARTH
                val gz = e.values[2] / SensorManager.GRAVITY_EARTH
                val g = kotlin.math.sqrt(gx * gx + gy * gy + gz * gz)
                if (g > 2.1f) {
                    val now = System.currentTimeMillis()
                    if (now - lastShake > 1500L) {
                        lastShake = now
                        onShuffle()
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (sm != null && accel != null) {
            sm.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)
        }
        onDispose { sm?.unregisterListener(listener) }
    }
    // The hint: shows on arrival, fades after 4 idle seconds, and comes back
    // whenever a blank spot in the room is tapped (which restarts the timer).
    var hintVisible by remember { mutableStateOf(true) }
    var hintTick by remember { mutableStateOf(0) }
    LaunchedEffect(hintTick) {
        hintVisible = true
        delay(4000)
        hintVisible = false
    }
    var tappedLabel by remember { mutableStateOf<String?>(null) }
    var zoomOrigin by remember { mutableStateOf(TransformOrigin.Center) }
    val roomZoom = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        roomZoom.snapTo(1.05f)
        roomZoom.animateTo(1f, tween(450))
    }
    var interacted by remember { mutableStateOf(false) }
    val pulseTrans = rememberInfiniteTransition(label = "roomPulse")
    val pulseVal by pulseTrans.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1700), RepeatMode.Reverse),
        label = "pulse"
    )
    fun activate(spot: Hotspot) {
        if (tappedLabel != null) return
        interacted = true
        tappedLabel = spot.label
        zoomOrigin = TransformOrigin(spot.fx, spot.fy)
        scope.launch {
            roomZoom.animateTo(1.07f, tween(240))
            val d = spot.dest
            if (d != null) onHotspot(d) else onShuffle()
        }
    }
    // If a tap doesn't lead anywhere — the permission box was answered
    // "no", or Shuffle is still waiting on permission — the room would
    // otherwise sit zoomed-in with its buttons stuck. Heal it: un-zoom
    // and re-arm the buttons. (Real navigation disposes this screen
    // first, which cancels this effect, so it never fights a real tap.)
    LaunchedEffect(tappedLabel) {
        if (tappedLabel != null) {
            delay(1000)
            tappedLabel = null
            roomZoom.animateTo(1f, tween(300))
        }
    }
    BoxWithConstraints(
        Modifier.fillMaxSize().background(Color.Black)
            .pointerInput(Unit) { detectTapGestures(onTap = { hintTick++ }) }
    ) {
        val maxWDp = maxWidth
        val maxHDp = maxHeight
        val tallMode = maxWDp / maxHDp < 0.62f
        // One spot set per screen shape: the day and night paintings share the
        // same composition, so the buttons must not move when the light changes.
        val activeSpots = if (tallMode) tallSpots else wideDaySpots
        val roomBitmap = rememberRoomBitmap(tallMode, night)
        val aspect = roomBitmap?.let { it.width.toFloat() / it.height.toFloat() }
            ?: if (tallMode) 783f / 2007f else 1704f / 2048f
        var fitW = maxWDp
        var fitH = maxWDp / aspect
        if (fitH > maxHDp) { fitH = maxHDp; fitW = maxHDp * aspect }
        // Adaptive zoom: the two layouts (wide and tall) stay, but when a
        // screen's shape doesn't match the painting's, pure fit leaves
        // black bars. Zoom in a little — up to 10% — to fill more of the
        // screen. The spots are placed from this same rect, and the chips
        // crop from it too, so the buttons always sit exactly on their
        // things, on any screen.
        val zoomW = maxWDp / fitW
        val zoomH = maxHDp / fitH
        val fillZoom = (if (zoomW < zoomH) zoomW else zoomH).coerceAtMost(1.10f)
        val drawnW = fitW * fillZoom
        val drawnH = fitH * fillZoom
        val offX = (maxWDp - drawnW) / 2
        val offY = (maxHDp - drawnH) / 2
        // The corner toggles wear the room too: a crop of the sky behind them,
        // cut one-to-one.
        val cornerFrac = (46f / drawnW.value).coerceIn(0.03f, 0.30f)
        val musicCrop = remember(roomBitmap, drawnW) {
            roomBitmap?.let { cropAround(it, if (tallMode) 0.07f else 0.045f, 0.04f, cornerFrac) }
        }
        val dayNightCrop = remember(roomBitmap, drawnW) {
            roomBitmap?.let { cropAround(it, if (tallMode) 0.93f else 0.955f, 0.04f, cornerFrac) }
        }
        Box(
            Modifier.fillMaxSize().graphicsLayer {
                scaleX = roomZoom.value
                scaleY = roomZoom.value
                transformOrigin = zoomOrigin
            }
        ) {
            Crossfade(targetState = roomBitmap, animationSpec = tween(450), label = "roomArt") { bmp ->
                if (bmp != null) {
                    Box(Modifier.offset(x = offX, y = offY).size(width = drawnW, height = drawnH)) {
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Arnold's room",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Box(
                        Modifier.fillMaxSize().background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFF2A4D8F), RoomTeal, RoomOrange)
                            )
                        )
                    )
                }
            }
        }
        val drawnWpx = with(LocalDensity.current) { drawnW.roundToPx() }
        activeSpots.forEachIndexed { i, spot ->
            val cx = offX + drawnW * spot.fx
            val cy = offY + drawnH * spot.fy
            Box(
                Modifier.offset(
                    x = cx - if (tallMode) 26.dp else 34.dp,
                    y = cy - if (tallMode) 26.dp else 34.dp
                )
            ) {
                RoomChip(
                    spot = spot,
                    art = roomBitmap,
                    imgWpx = drawnWpx,
                    index = i,
                    tapped = tappedLabel == spot.label,
                    compact = tallMode,
                    pulse = if (interacted) 0.45f else pulseVal,
                    onTap = { activate(spot) }
                )
            }
        }
        AnimatedVisibility(
            visible = hintVisible,
            exit = fadeOut(tween(500)),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp)
        ) {
            Text(
                "Tap something in the room",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x9910131A))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
        Text(
            "Series created by Craig Bartlett\nApp created by May Marquardt",
            fontFamily = HandwrittenFont,
            color = Color(0xFFFFF3E0).copy(alpha = 0.9f),
            fontSize = 15.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = 12.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .combinedClickable(onClick = onToggleMusic),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (musicCrop != null) Color.Transparent else Color(0xCC10131A))
                    .border(2.5.dp, RoomYellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (musicCrop != null) {
                    Image(
                        bitmap = musicCrop.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(Modifier.fillMaxSize().background(Color(0x59000000)))
                }
                Crossfade(targetState = musicOn, animationSpec = tween(300), label = "musicIcon") { on ->
                    Icon(
                        if (on) Icons.Filled.MusicNote else Icons.Filled.MusicOff,
                        if (on) "Pause music" else "Play music",
                        tint = if (on) RoomYellow else RoomTextDim,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                if (musicOn) "Music" else "Muted",
                color = Color.White,
                fontSize = if (tallMode) 12.5.sp else 11.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xAA10131A))
                    .padding(horizontal = if (tallMode) 6.dp else 5.dp, vertical = if (tallMode) 1.dp else 0.dp)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .combinedClickable(onClick = onToggleNight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (dayNightCrop != null) Color.Transparent else Color(0xCC10131A))
                    .border(2.5.dp, Color(0xFFFF8F00), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (dayNightCrop != null) {
                    Image(
                        bitmap = dayNightCrop.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(Modifier.fillMaxSize().background(Color(0x59000000)))
                }
                Crossfade(targetState = night, animationSpec = tween(300), label = "dayNightIcon") { n ->
                    Icon(
                        if (n) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                        if (n) "Day" else "Night",
                        tint = RoomYellow, modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                if (night) "Day" else "Night",
                color = Color.White,
                fontSize = if (tallMode) 12.5.sp else 11.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xAA10131A))
                    .padding(horizontal = if (tallMode) 6.dp else 5.dp, vertical = if (tallMode) 1.dp else 0.dp)
            )
        }
    }
}

// ---------- Shared bits ----------
@Composable
fun ScreenHeader(title: String, onRoom: () -> Unit, right: @Composable () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().background(RoomCard).padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onRoom) { Icon(Icons.Filled.ArrowBack, "Back to room", tint = RoomSky) }
        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        right()
    }
}

@Composable
fun VideoThumb(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bmp by produceState<Bitmap?>(initialValue = null, uri) {
        value = withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= 29)
                    context.contentResolver.loadThumbnail(uri, Size(320, 180), null)
                else null
            } catch (e: Exception) { null }
        }
    }
    Box(modifier.background(Color(0xFF2A2F42)), contentAlignment = Alignment.Center) {
        if (bmp != null) {
            Image(bmp!!.asImageBitmap(), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Icon(Icons.Filled.Movie, null, tint = RoomTextDim, modifier = Modifier.size(30.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoRow(
    v: VideoItem,
    cat: AssignCat,
    fav: Boolean,
    pos: Long,
    selected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    watched: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth()
            .background(if (selected) Color(0xFF2A3350) else Color.Transparent)
            .combinedClickable(onClick = onClick, onLongClick = onLongPress)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoThumb(v.uri, Modifier.width(104.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(8.dp)))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(v.title, color = if (watched) RoomTextDim else Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(3.dp))
            Text(
                listOf(fmtDur(v.durationMs), fmtSize(v.sizeBytes), v.folder).filter { it.isNotBlank() }.joinToString("  •  "),
                color = RoomTextDim, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            if (v.durationMs > 0 && pos > 0) {
                Spacer(Modifier.height(5.dp))
                Box(Modifier.fillMaxWidth(0.7f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF3A405A))) {
                    Box(Modifier.fillMaxWidth((pos.toFloat() / v.durationMs).coerceIn(0f, 1f)).height(4.dp).background(RoomOrange))
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (watched) Icon(Icons.Filled.CheckCircle, "Watched", tint = Color(0xFF00C853), modifier = Modifier.size(19.dp))
            if (fav) Icon(Icons.Filled.Favorite, "Favorite", tint = RoomOrange, modifier = Modifier.size(19.dp))
            Text(
                when (cat) { AssignCat.MOVIE -> "Movie"; AssignCat.TV -> "TV"; AssignCat.NONE -> "—" },
                color = RoomSky, fontSize = 11.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AssignDialog(
    v: VideoItem,
    prefs: SharedPreferences,
    onDismiss: () -> Unit,
    onChanged: () -> Unit
) {
    var fav by remember { mutableStateOf(prefs.getBoolean("fav_${v.id}", false)) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RoomCard,
        title = { Text(v.title, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        text = {
            Column {
                Text("Put this in:", color = RoomTextDim, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { prefs.edit().putString("cat_${v.id}", "MOVIE").apply(); onChanged(); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoomOrange)
                    ) { Text("Movies") }
                    Button(
                        onClick = { prefs.edit().putString("cat_${v.id}", "TV").apply(); onChanged(); onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoomSky)
                    ) { Text("TV Shows") }
                    TextButton(onClick = { prefs.edit().remove("cat_${v.id}").apply(); onChanged(); onDismiss() }) {
                        Text("Unsorted", color = RoomTextDim)
                    }
                }
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = {
                    fav = !fav
                    prefs.edit().putBoolean("fav_${v.id}", fav).apply(); onChanged()
                }) {
                    Icon(if (fav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder, null,
                        tint = RoomOrange, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (fav) "Favorited" else "Add to Favorites", color = Color.White)
                }
                Text("Tip: favorites also live behind the fish tank in the room.",
                    color = RoomTextDim, fontSize = 12.sp)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Done", color = RoomYellow) } }
    )
}

// ---------- Categories ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoriesScreen(
    counts: Map<Filter, Int>,
    showsCount: Int,
    movieFolderCount: Int,
    onPick: (Filter) -> Unit,
    onShows: () -> Unit,
    onMovieFolders: () -> Unit,
    onRoom: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("The Shelves — Categories", onRoom)
        LazyColumn(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
                        .combinedClickable(onClick = onShows).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Tv, null, tint = RoomOrange, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("TV Shows — by Folder & Season", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Text("Auto-detected from your folders", color = RoomTextDim, fontSize = 12.sp)
                    }
                    Text("$showsCount", color = RoomSky, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
                        .combinedClickable(onClick = onMovieFolders).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Movie, null, tint = RoomOrange, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Movies — by Folder", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                        Text("Auto-detected from your folders", color = RoomTextDim, fontSize = 12.sp)
                    }
                    Text("$movieFolderCount", color = RoomSky, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            items(Filter.values().toList()) { f ->
                val icon = when (f) {
                    Filter.MOVIES -> Icons.Filled.Movie
                    Filter.TV -> Icons.Filled.Tv
                    Filter.FAVORITES -> Icons.Filled.Favorite
                    Filter.CONTINUE -> Icons.Filled.PlayCircleFilled
                    Filter.RECENT -> Icons.Filled.History
                    Filter.UNSORTED -> Icons.Filled.Folder
                    Filter.ALL -> Icons.Filled.Star
                }
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
                        .combinedClickable(onClick = { onPick(f) }).padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, null, tint = RoomYellow, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(14.dp))
                    Text(f.label, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f))
                    Text("${counts[f] ?: 0}", color = RoomSky, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            item {
                Text("Long-press any video in a list to file it under Movies or TV Shows, or make it a favorite.",
                    color = RoomTextDim, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
            }
        }
    }
}

// ---------- Auto-detected shows & movie folders ----------
fun nextEpisode(show: ShowInfo, isWatched: (VideoItem) -> Boolean, savedPos: (VideoItem) -> Long): VideoItem? {
    val all = show.seasons.toSortedMap().values.flatten()
    all.firstOrNull { savedPos(it) > 10_000 && !isWatched(it) }?.let { return it }
    return all.firstOrNull { !isWatched(it) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowsScreen(
    shows: List<ShowInfo>,
    isWatched: (VideoItem) -> Boolean,
    savedPos: (VideoItem) -> Long,
    onPick: (String) -> Unit,
    onRoom: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("TV Shows — Folders & Seasons", onRoom)
        if (shows.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No shows detected yet.\nPut episodes in a folder named after the show,\nwith Season 1, Season 2 folders inside,\nand they'll line up here on their own.",
                    color = RoomTextDim, fontSize = 15.sp, modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(shows) { sh ->
                    val epCount = sh.seasons.values.sumOf { it.size }
                    val watchedCount = sh.seasons.values.flatten().count { isWatched(it) }
                    val next = nextEpisode(sh, isWatched, savedPos)
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
                            .combinedClickable(onClick = { onPick(sh.title) }).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Tv, null, tint = RoomYellow, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(sh.title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${sh.seasons.size} season${if (sh.seasons.size == 1) "" else "s"} • $epCount episodes • $watchedCount watched",
                                color = RoomTextDim, fontSize = 13.sp
                            )
                            if (epCount > 0) {
                                Spacer(Modifier.height(7.dp))
                                LinearProgressIndicator(
                                    progress = { watchedCount.toFloat() / epCount },
                                    modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                                    color = Color(0xFF00C853), trackColor = Color(0x33FFFFFF)
                                )
                                Spacer(Modifier.height(5.dp))
                                Text(
                                    if (next == null) "All caught up ✓" else "Next: ${next.title}",
                                    color = if (next == null) Color(0xFF00C853) else RoomOrange,
                                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowDetailScreen(
    show: ShowInfo,
    isFav: (VideoItem) -> Boolean,
    isWatched: (VideoItem) -> Boolean,
    savedPos: (VideoItem) -> Long,
    onToggleWatched: (VideoItem) -> Unit,
    onPlay: (List<VideoItem>, Int) -> Unit,
    onRoom: () -> Unit
) {
    val next = nextEpisode(show, isWatched, savedPos)
    val nextSeason = next?.let { n -> show.seasons.toSortedMap().entries.firstOrNull { n in it.value } }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(show.title, onRoom)
        LazyColumn(Modifier.fillMaxSize()) {
            if (next != null && nextSeason != null) {
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(14.dp)).background(Color(0xFFFF8A3D))
                            .clickable { onPlay(nextSeason.value, nextSeason.value.indexOf(next)) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PlayCircleFilled, null, tint = Color(0xFF10131A), modifier = Modifier.size(34.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("UP NEXT — SEASON ${nextSeason.key}", color = Color(0xFF10131A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(next.title, color = Color(0xFF10131A), fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "All caught up ✓ — every episode watched.",
                        color = Color(0xFF00C853), fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
            item {
                Text(
                    "Long-press an episode to mark it watched, or unwatched again.",
                    color = RoomTextDim, fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 2.dp)
                )
            }
            show.seasons.forEach { (season, eps) ->
                item {
                    Text(
                        "Season $season", color = RoomYellow, fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 6.dp)
                    )
                }
                items(eps) { v ->
                    VideoRow(
                        v, AssignCat.TV, isFav(v), savedPos(v), false,
                        onClick = { onPlay(eps, eps.indexOf(v)) },
                        onLongPress = { onToggleWatched(v) },
                        watched = isWatched(v)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieFoldersScreen(folders: List<MovieFolder>, onPick: (String) -> Unit, onRoom: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Movies — by Folder", onRoom)
        if (folders.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No movie folders detected yet.\nMovies in their own folders will show up here,\nnamed by the folder.",
                    color = RoomTextDim, fontSize = 15.sp, modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(folders) { mf ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
                            .combinedClickable(onClick = { onPick(mf.key) }).padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Movie, null, tint = RoomYellow, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(mf.title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (mf.items.size == 1) fmtDur(mf.items.first().durationMs)
                                else "${mf.items.size} videos",
                                color = RoomTextDim, fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------- Library / list screens (Fold-aware: two panes when wide) ----------
enum class SortMode { NAME, NEWEST, LONGEST }

@Composable
fun LibraryScreen(
    title: String,
    items: List<VideoItem>,
    prefs: SharedPreferences,
    catOf: (VideoItem) -> AssignCat,
    isFav: (VideoItem) -> Boolean,
    savedPos: (VideoItem) -> Long,
    onPlay: (List<VideoItem>, Int) -> Unit,
    onRoom: () -> Unit,
    onStats: (() -> Unit)? = null
) {
    var sort by remember { mutableStateOf(SortMode.NEWEST) }
    var version by remember { mutableStateOf(0) }
    var assignTarget by remember { mutableStateOf<VideoItem?>(null) }
    var selected by remember { mutableStateOf<VideoItem?>(null) }
    val sorted = remember(items, sort, version) {
        when (sort) {
            SortMode.NAME -> items.sortedBy { it.title.lowercase() }
            SortMode.NEWEST -> items.sortedByDescending { it.dateAddedSec }
            SortMode.LONGEST -> items.sortedByDescending { it.durationMs }
        }
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(title, onRoom) {
            if (onStats != null) {
                TextButton(onClick = onStats) {
                    Text("Stats", color = RoomYellow, fontSize = 13.sp)
                }
            }
            TextButton(onClick = {
                sort = when (sort) { SortMode.NEWEST -> SortMode.NAME; SortMode.NAME -> SortMode.LONGEST; SortMode.LONGEST -> SortMode.NEWEST }
            }) { Text("Sort: ${sort.name.lowercase().replaceFirstChar { it.uppercase() }}", color = RoomYellow, fontSize = 13.sp) }
        }
        if (sorted.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (title == Filter.CONTINUE.label) "Nothing half-watched right now.\nStart something from the TV and it'll wait for you here, on the bed."
                    else "Nothing here yet.\nDownload some movies or shows to your phone and they'll show up.",
                    color = RoomTextDim, fontSize = 15.sp, modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val wide = maxWidth >= 600.dp
                if (wide) {
                    Row(Modifier.fillMaxSize()) {
                        LazyColumn(Modifier.weight(1f).fillMaxHeight()) {
                            items(sorted) { v ->
                                val k = version // re-read prefs after edits
                                VideoRow(v, catOf(v), isFav(v), savedPos(v), selected?.id == v.id,
                                    onClick = { selected = v },
                                    onLongPress = { assignTarget = v })
                            }
                        }
                        Box(Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF2A2F42)))
                        Column(Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
                            val sel = selected ?: sorted.first()
                            VideoThumb(sel.uri, Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)))
                            Spacer(Modifier.height(12.dp))
                            Text(sel.title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold,
                                maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(6.dp))
                            Text(listOf(fmtDur(sel.durationMs), fmtSize(sel.sizeBytes), sel.folder).filter { it.isNotBlank() }.joinToString("  •  "),
                                color = RoomTextDim, fontSize = 13.sp)
                            val p = savedPos(sel)
                            if (p > 0 && sel.durationMs > 0)
                                Text("Resume from ${fmtDur(p)}", color = RoomOrange, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(14.dp))
                            Button(
                                onClick = { onPlay(sorted, sorted.indexOf(sel)) },
                                colors = ButtonDefaults.buttonColors(containerColor = RoomOrange),
                                modifier = Modifier.fillMaxWidth()
                            ) { Icon(Icons.Filled.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("Play") }
                            TextButton(onClick = { assignTarget = sel }) { Text("File it / favorite…", color = RoomSky) }
                        }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(sorted) { v ->
                            val k = version
                            VideoRow(v, catOf(v), isFav(v), savedPos(v), false,
                                onClick = { onPlay(sorted, sorted.indexOf(v)) },
                                onLongPress = { assignTarget = v })
                        }
                    }
                }
            }
        }
    }
    assignTarget?.let { v ->
        AssignDialog(v, prefs, onDismiss = { assignTarget = null }, onChanged = { version++ })
    }
}

// ---------- Search ----------
@Composable
fun SearchScreen(
    items: List<VideoItem>,
    prefs: SharedPreferences,
    catOf: (VideoItem) -> AssignCat,
    isFav: (VideoItem) -> Boolean,
    savedPos: (VideoItem) -> Long,
    onPlay: (List<VideoItem>, Int) -> Unit,
    onRoom: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var version by remember { mutableStateOf(0) }
    var assignTarget by remember { mutableStateOf<VideoItem?>(null) }
    val results = remember(items, query, version) {
        if (query.isBlank()) items else items.filter { it.title.contains(query, ignoreCase = true) }
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Search", onRoom)
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            placeholder = { Text("Type a title…", color = RoomTextDim) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        )
        LazyColumn(Modifier.fillMaxSize()) {
            items(results) { v ->
                val k = version
                VideoRow(v, catOf(v), isFav(v), savedPos(v), false,
                    onClick = { onPlay(results, results.indexOf(v)) },
                    onLongPress = { assignTarget = v })
            }
        }
    }
    assignTarget?.let { v ->
        AssignDialog(v, prefs, onDismiss = { assignTarget = null }, onChanged = { version++ })
    }
}

// ---------- Settings ----------
@Composable
fun StatCard(modifier: Modifier, label: String, value: String) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(RoomCard)
            .padding(14.dp)
    ) {
        Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(label, color = RoomTextDim, fontSize = 13.sp)
    }
}

@Composable
fun StatsScreen(
    videos: List<VideoItem>,
    prefs: SharedPreferences,
    isWatched: (VideoItem) -> Boolean,
    savedPos: (VideoItem) -> Long,
    showsCount: Int,
    onRoom: () -> Unit
) {
    val finished = videos.count { isWatched(it) }
    val inProgress = videos.filter { savedPos(it) > 0L }
        .sortedByDescending { savedPos(it) }
    val timeMs = videos.sumOf { v -> if (isWatched(v)) v.durationMs else savedPos(v) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Your watching", onRoom)
        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(Modifier.weight(1f), "Videos in the room", videos.size.toString())
                    StatCard(Modifier.weight(1f), "Shows", showsCount.toString())
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(Modifier.weight(1f), "Finished", finished.toString())
                    StatCard(Modifier.weight(1f), "In progress", inProgress.size.toString())
                }
            }
            item {
                StatCard(Modifier.fillMaxWidth(), "Time watched, all together", fmtDur(timeMs))
            }
            if (inProgress.isNotEmpty()) {
                item {
                    Text(
                        "Still in the middle of",
                        color = RoomYellow, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(inProgress) { v ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(RoomCard).padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            v.title, color = Color.White, fontSize = 14.sp, maxLines = 1,
                            overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("${fmtDur(savedPos(v))} in", color = RoomTextDim, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    videoCount: Int,
    granted: Boolean,
    onGrant: () -> Unit,
    onClearResume: () -> Unit,
    onRoom: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Settings", onRoom)
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Text("$videoCount videos found on this phone.", color = Color.White, fontSize = 16.sp) }
            if (!granted) item {
                Button(onClick = onGrant, colors = ButtonDefaults.buttonColors(containerColor = RoomOrange)) {
                    Text("Allow access to my videos")
                }
            }
            item {
                TextButton(onClick = onClearResume) { Text("Forget all resume positions", color = RoomSky) }
            }
            item {
                Text(
                    "The room map:\n" +
                    "• TV — all your videos\n" +
                    "• Shelves — categories (Movies, TV Shows, Favorites, Unsorted)\n" +
                    "• Bed — continue watching, right where you left off\n" +
                    "• Window — search\n" +
                    "• Fish tank — favorites\n" +
                    "• Skylight — recently added\n" +
                    "• Remote on the rug — shuffle: play something random\n" +
                    "• Door — settings (you're here)\n\n" +
                    "The song plays while you're in the room and pauses the moment you tap anything, or leave the app.\n\n" +
                    "Long-press a video anywhere to file it or favorite it. On the Fold opened up, lists split into two panes so you can browse and preview at the same time.",
                    color = RoomTextDim, fontSize = 14.sp
                )
            }
        }
    }
}

// ---------- Full video player ----------
@OptIn(UnstableApi::class, ExperimentalFoundationApi::class)
@Composable
fun PlayerScreen(
    queue: List<VideoItem>,
    startIndex: Int,
    fromLabel: String,
    prefs: SharedPreferences,
    onBack: () -> Unit,
    onShrink: (Int) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val exo = remember {
        ExoPlayer.Builder(
            context,
            DefaultRenderersFactory(context).setEnableDecoderFallback(true)
        ).build().apply {
            val mediaItems = queue.map { MediaItem.fromUri(it.uri) }
            val startPos = queue.getOrNull(startIndex)?.let { prefs.getLong("pos_${it.id}", 0L) } ?: 0L
            val usable = queue.getOrNull(startIndex)?.let {
                startPos > 0 && (it.durationMs == 0L || startPos < it.durationMs * 95 / 100)
            } ?: false
            setMediaItems(mediaItems, startIndex, if (usable) startPos else 0L)
            prepare()
            playWhenReady = true
        }
    }
    var isPlaying by remember { mutableStateOf(true) }
    var position by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var index by remember { mutableStateOf(startIndex) }
    var speed by remember { mutableStateOf(1.0f) }
    var showControls by remember { mutableStateOf(true) }
    var seeking by remember { mutableStateOf(false) }
    var sliderPos by remember { mutableStateOf(0f) }
    var fullscreen by remember { mutableStateOf(false) }
    var sleepChoice by remember { mutableStateOf(0) }
    var sleepLeftMs by remember { mutableStateOf(0L) }
    var hasCc by remember { mutableStateOf(false) }
    var ccOn by remember { mutableStateOf(true) }
    var marks by remember { mutableStateOf<List<Long>>(emptyList()) }
    var showMarks by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }

    fun loadMarks() {
        val v = queue.getOrNull(index)
        marks = if (v == null) emptyList()
        else prefs.getString("marks_${v.id}", "")
            ?.split(",")?.mapNotNull { it.trim().toLongOrNull() }?.sorted() ?: emptyList()
    }
    fun saveMarks(list: List<Long>) {
        val v = queue.getOrNull(index) ?: return
        prefs.edit().putString("marks_${v.id}", list.joinToString(",")).apply()
        marks = list
    }

    fun savePos(forIndex: Int, pos: Long) {
        queue.getOrNull(forIndex)?.let { v ->
            val ed = prefs.edit()
            if (v.durationMs > 0 && pos > v.durationMs * 95 / 100) {
                ed.remove("pos_${v.id}")
                ed.putBoolean("watched_${v.id}", true)
            } else ed.putLong("pos_${v.id}", pos)
            if (pos >= 1000L) {
                ed.putLong("last_id", v.id)
                ed.putString("last_title", v.title)
                ed.putLong("last_pos", pos)
                ed.putLong("last_dur", v.durationMs)
            }
            ed.apply()
        }
    }

    LaunchedEffect(sleepChoice) {
        if (sleepChoice > 0) {
            val deadline = System.currentTimeMillis() + sleepChoice * 60_000L
            while (true) {
                val left = deadline - System.currentTimeMillis()
                sleepLeftMs = left
                if (left <= 0) break
                delay(1000)
            }
            exo.pause()
            sleepChoice = 0
            sleepLeftMs = 0L
        }
    }

    LaunchedEffect(ccOn) {
        exo.trackSelectionParameters = exo.trackSelectionParameters.buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !ccOn).build()
    }
    LaunchedEffect(index) {
        loadMarks()
        showMarks = false
        playerError = null
    }

    // Picture-in-picture: while an episode is playing, leaving the app pops
    // the video out into a floating window on top of everything else.
    LaunchedEffect(isPlaying) {
        PipControl.shouldEnter = isPlaying
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity?.setPictureInPictureParams(
                PictureInPictureParams.Builder()
                    .setAspectRatio(Rational(16, 9))
                    .setAutoEnterEnabled(isPlaying)
                    .build()
            )
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            PipControl.shouldEnter = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                activity?.setPictureInPictureParams(
                    PictureInPictureParams.Builder().setAutoEnterEnabled(false).build()
                )
            }
        }
    }

    DisposableEffect(exo) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                index = exo.currentMediaItemIndex
            }
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
            override fun onPlayerError(error: PlaybackException) {
                playerError = "This file wouldn't play — its format may not be supported."
                showControls = true
            }
        }
        exo.addListener(listener)
        onDispose {
            savePos(exo.currentMediaItemIndex, exo.currentPosition)
            exo.removeListener(listener)
            exo.release()
        }
    }
    LaunchedEffect(exo) {
        var ticks = 0
        while (true) {
            position = exo.currentPosition.coerceAtLeast(0)
            duration = exo.duration.coerceAtLeast(0)
            isPlaying = exo.isPlaying
            index = exo.currentMediaItemIndex
            hasCc = exo.currentTracks.groups.any { it.type == C.TRACK_TYPE_TEXT }
            if (!seeking) sliderPos = if (duration > 0) position.toFloat() / duration else 0f
            ticks++
            if (ticks % 10 == 0) savePos(index, position)
            delay(500)
        }
    }
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) { delay(3500); showControls = false }
    }
    fun applyFullscreen(on: Boolean) {
        fullscreen = on
        activity?.let { act ->
            act.requestedOrientation = if (on) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
            if (on) {
                controller.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            activity?.let { act ->
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                WindowCompat.getInsetsController(act.window, act.window.decorView)
                    .show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    val current = queue.getOrNull(index)
    val playerBox: @Composable (Modifier) -> Unit = { mod ->
        Box(mod.background(Color.Black)) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exo
                        useController = false
                        keepScreenOn = true
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            // tap = show/hide controls, double-tap sides = skip 10s
            Box(
                Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { showControls = !showControls },
                        onDoubleTap = { off ->
                            if (off.x < size.width / 2) exo.seekTo((exo.currentPosition - 10_000).coerceAtLeast(0))
                            else exo.seekTo(exo.currentPosition + 10_000)
                            showControls = true
                        }
                    )
                }
            )
            if (showControls && !PipControl.inPip) {
                Column(Modifier.fillMaxSize().background(Color(0x66000000))) {
                    Row(Modifier.fillMaxWidth().padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White) }
                        Column(Modifier.weight(1f)) {
                            Text(current?.title ?: "", color = Color.White, fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(fromLabel, color = RoomTextDim, fontSize = 11.sp)
                        }
                        TextButton(onClick = {
                            speed = when (speed) { 1.0f -> 1.25f; 1.25f -> 1.5f; 1.5f -> 2.0f; 2.0f -> 0.5f; 0.5f -> 0.75f; else -> 1.0f }
                            exo.setPlaybackSpeed(speed)
                        }) { Text("${speed}x", color = RoomYellow, fontWeight = FontWeight.Bold) }
                        TextButton(onClick = {
                            sleepChoice = when (sleepChoice) { 0 -> 15; 15 -> 30; 30 -> 45; 45 -> 60; else -> 0 }
                        }) {
                            Icon(Icons.Filled.Bedtime, "Sleep timer", tint = if (sleepChoice > 0) RoomYellow else Color.White, modifier = Modifier.size(19.dp))
                            if (sleepChoice > 0) Text(" ${(sleepLeftMs / 60000) + 1}m", color = RoomYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        if (hasCc) {
                            IconButton(onClick = { ccOn = !ccOn }) {
                                Icon(
                                    if (ccOn) Icons.Filled.ClosedCaption else Icons.Filled.ClosedCaptionDisabled,
                                    "Captions", tint = if (ccOn) RoomYellow else Color.White,
                                    modifier = Modifier.size(21.dp)
                                )
                            }
                        }
                        IconButton(onClick = {
                            val t = exo.currentPosition
                            if (!marks.contains(t)) saveMarks((marks + t).sorted())
                            showMarks = true
                        }) {
                            Icon(Icons.Filled.BookmarkAdd, "Save this moment", tint = Color.White, modifier = Modifier.size(21.dp))
                        }
                        if (marks.isNotEmpty()) {
                            IconButton(onClick = { showMarks = !showMarks }) {
                                Icon(Icons.Filled.Bookmarks, "Favorite moments", tint = RoomYellow, modifier = Modifier.size(21.dp))
                            }
                        }
                        IconButton(onClick = {
                            // Save the exact spot first, so the mini player
                            // continues right here instead of at the last
                            // periodic save (or the beginning).
                            savePos(exo.currentMediaItemIndex, exo.currentPosition)
                            onShrink(exo.currentMediaItemIndex)
                        }) {
                            Icon(Icons.Filled.PictureInPictureAlt, "Mini player", tint = Color.White)
                        }
                        IconButton(onClick = { applyFullscreen(!fullscreen) }) {
                            Icon(if (fullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                "Fullscreen", tint = Color.White)
                        }
                    }
                    if (playerError != null) {
                        Text(
                            playerError!!, color = Color.White, fontSize = 14.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 8.dp, end = 12.dp)
                                .clip(RoundedCornerShape(10.dp)).background(Color(0xCC8F1D1D))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                    if (showMarks && marks.isNotEmpty()) {
                        Column(
                            Modifier.padding(start = 12.dp, top = 8.dp)
                                .clip(RoundedCornerShape(10.dp)).background(Color(0xE61D2130))
                                .padding(10.dp)
                        ) {
                            Text("Favorite moments", color = RoomYellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            marks.forEach { t ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        fmtDur(t), color = Color.White, fontSize = 14.sp,
                                        modifier = Modifier
                                            .combinedClickable(onClick = { exo.seekTo(t); exo.play() })
                                            .padding(vertical = 4.dp, horizontal = 2.dp)
                                    )
                                    IconButton(onClick = { saveMarks(marks - t) }, modifier = Modifier.size(30.dp)) {
                                        Icon(Icons.Filled.Delete, "Remove moment", tint = RoomTextDim, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (exo.hasPreviousMediaItem()) exo.seekToPreviousMediaItem() },
                            modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Filled.SkipPrevious, "Previous", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        IconButton(onClick = { exo.seekTo((exo.currentPosition - 10_000).coerceAtLeast(0)) },
                            modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Filled.Replay10, "Back 10 seconds", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        IconButton(
                            onClick = { if (exo.isPlaying) exo.pause() else exo.play() },
                            modifier = Modifier.size(68.dp).clip(CircleShape).background(RoomOrange)
                        ) {
                            Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                "Play or pause", tint = Color.White, modifier = Modifier.size(38.dp))
                        }
                        IconButton(onClick = { exo.seekTo(exo.currentPosition + 10_000) },
                            modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Filled.Forward10, "Forward 10 seconds", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        IconButton(onClick = { if (exo.hasNextMediaItem()) exo.seekToNextMediaItem() },
                            modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Filled.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(fmtDur(position), color = Color.White, fontSize = 12.sp)
                        Slider(
                            value = sliderPos,
                            onValueChange = { seeking = true; sliderPos = it },
                            onValueChangeFinished = {
                                if (duration > 0) exo.seekTo((sliderPos * duration).toLong())
                                seeking = false
                            },
                            colors = SliderDefaults.colors(thumbColor = RoomOrange, activeTrackColor = RoomOrange),
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                        Text(fmtDur(duration), color = Color.White, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }

    if (fullscreen) {
        playerBox(Modifier.fillMaxSize())
    } else {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth >= 600.dp
            if (wide) {
                Row(Modifier.fillMaxSize()) {
                    playerBox(Modifier.weight(1.6f).fillMaxHeight())
                    Column(Modifier.width(300.dp).fillMaxHeight().background(RoomCard)) {
                        Text("Up next", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(14.dp))
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(queue.withIndex().toList()) { (i, v) ->
                                Row(
                                    Modifier.fillMaxWidth()
                                        .background(if (i == index) Color(0xFF2A3350) else Color.Transparent)
                                        .combinedClickable(onClick = { exo.seekTo(i, 0L); exo.play() })
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    VideoThumb(v.uri, Modifier.width(72.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(6.dp)))
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(v.title, color = Color.White, fontSize = 13.sp, maxLines = 2,
                                            overflow = TextOverflow.Ellipsis)
                                        Text(fmtDur(v.durationMs), color = RoomTextDim, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    playerBox(Modifier.fillMaxWidth().aspectRatio(16f / 9f))
                    Text("Up next", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp))
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(queue.withIndex().toList()) { (i, v) ->
                            Row(
                                Modifier.fillMaxWidth()
                                    .background(if (i == index) Color(0xFF2A3350) else Color.Transparent)
                                    .combinedClickable(onClick = { exo.seekTo(i, 0L); exo.play() })
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VideoThumb(v.uri, Modifier.width(72.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(6.dp)))
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(v.title, color = Color.White, fontSize = 13.sp, maxLines = 2,
                                        overflow = TextOverflow.Ellipsis)
                                    Text(fmtDur(v.durationMs), color = RoomTextDim, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Mini player: a small, movable, resizable video box over the room ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MiniPlayerBox(
    queue: List<VideoItem>,
    startIndex: Int,
    prefs: SharedPreferences,
    onExpand: (Int) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val exo = remember {
        ExoPlayer.Builder(
            context,
            DefaultRenderersFactory(context).setEnableDecoderFallback(true)
        ).build().apply {
            val items = queue.map { MediaItem.fromUri(it.uri) }
            val v = queue.getOrNull(startIndex)
            val startPos = v?.let { prefs.getLong("pos_${it.id}", 0L) } ?: 0L
            val usable = v != null && startPos > 0 &&
                (v.durationMs == 0L || startPos < v.durationMs * 95 / 100)
            setMediaItems(items, startIndex, if (usable) startPos else 0L)
            prepare()
            playWhenReady = true
        }
    }
    var index by remember { mutableStateOf(startIndex) }
    var isPlaying by remember { mutableStateOf(true) }
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appear.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium))
    }

    fun saveNow() {
        val v = queue.getOrNull(exo.currentMediaItemIndex) ?: return
        val pos = exo.currentPosition
        val ed = prefs.edit()
        if (v.durationMs > 0 && pos > v.durationMs * 95 / 100) {
            ed.remove("pos_${v.id}")
            ed.putBoolean("watched_${v.id}", true)
        } else ed.putLong("pos_${v.id}", pos)
        if (pos >= 1000L) {
            ed.putLong("last_id", v.id)
            ed.putString("last_title", v.title)
            ed.putLong("last_pos", pos)
            ed.putLong("last_dur", v.durationMs)
        }
        ed.apply()
    }
    DisposableEffect(exo) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                index = exo.currentMediaItemIndex
            }
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
        }
        exo.addListener(listener)
        onDispose {
            saveNow()
            exo.removeListener(listener)
            exo.release()
        }
    }
    LaunchedEffect(exo) {
        while (true) {
            delay(5000)
            saveNow()
        }
    }
    val current = queue.getOrNull(index)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val maxWPx = with(density) { maxWidth.toPx() }
        val maxHPx = with(density) { maxHeight.toPx() }
        val maxWDpLocal = maxWidth
        var widthDp by remember { mutableStateOf(216.dp) }
        var offX by remember { mutableStateOf<Float?>(null) }
        var offY by remember { mutableStateOf<Float?>(null) }
        val widthPx = with(density) { widthDp.toPx() }
        val heightPx = widthPx * 9f / 16f + with(density) { 98.dp.toPx() }
        val x = (offX ?: (maxWPx - widthPx - with(density) { 12.dp.toPx() }))
            .coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
        val y = (offY ?: (maxHPx - heightPx - with(density) { 64.dp.toPx() }))
            .coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))

        Column(
            Modifier
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .width(widthDp)
                .shadow(10.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFE8A04C))
                .border(3.5.dp, Color(0xFF10131A), RoundedCornerShape(18.dp))
                .padding(9.dp)
                .graphicsLayer {
                    val a = appear.value
                    scaleX = 0.7f + 0.3f * a
                    scaleY = 0.7f + 0.3f * a
                    alpha = a
                }
        ) {
            // Rabbit-ear antennae, so the pop-up reads as an old TV in the room.
            Canvas(
                Modifier.width(96.dp).height(24.dp).align(Alignment.CenterHorizontally)
            ) {
                val swLine = 3.5.dp.toPx()
                val base = Offset(size.width / 2, size.height)
                val tipL = Offset(size.width * 0.08f, 3.dp.toPx())
                val tipR = Offset(size.width * 0.92f, 3.dp.toPx())
                drawLine(Color(0xFF10131A), base, tipL, strokeWidth = swLine, cap = StrokeCap.Round)
                drawLine(Color(0xFF10131A), base, tipR, strokeWidth = swLine, cap = StrokeCap.Round)
                drawCircle(Color(0xFF10131A), radius = 4.dp.toPx(), center = tipL)
                drawCircle(Color(0xFF10131A), radius = 4.dp.toPx(), center = tipR)
            }
            Box(
                Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp)).background(Color.Black)
                    .border(3.5.dp, Color(0xFF10131A), RoundedCornerShape(8.dp))
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exo
                            useController = false
                            keepScreenOn = true
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier.fillMaxSize().pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { if (exo.isPlaying) exo.pause() else exo.play() }
                        )
                    }
                )
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, drag ->
                                change.consume()
                                widthDp = (widthDp + with(density) { drag.x.toDp() })
                                    .coerceIn(140.dp, maxWDpLocal - 8.dp)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.OpenInFull, "Resize",
                        tint = Color.White, modifier = Modifier.size(14.dp)
                    )
                }
            }
            Row(
                Modifier.fillMaxWidth().padding(top = 7.dp, bottom = 1.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                repeat(16) {
                    Box(
                        Modifier.width(2.dp).height(8.dp)
                            .clip(RoundedCornerShape(1.dp)).background(Color(0xFF10131A))
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFF8A5427))
                    .pointerInput(Unit) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            val curX = offX ?: (maxWPx - widthPx - with(density) { 12.dp.toPx() })
                            val curY = offY ?: (maxHPx - heightPx - with(density) { 64.dp.toPx() })
                            offX = (curX + drag.x)
                                .coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
                            offY = (curY + drag.y)
                                .coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))
                        }
                    }
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    "Play or pause", tint = RoomYellow,
                    modifier = Modifier.size(18.dp).combinedClickable(
                        onClick = { if (exo.isPlaying) exo.pause() else exo.play() }
                    )
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Filled.Fullscreen, "Full screen", tint = Color.White,
                    modifier = Modifier.size(18.dp).combinedClickable(
                        onClick = { saveNow(); onExpand(exo.currentMediaItemIndex) }
                    )
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.Filled.Close, "Close", tint = Color.White,
                    modifier = Modifier.size(18.dp).combinedClickable(
                        onClick = { saveNow(); onClose() }
                    )
                )
            }
        }
    }
}
