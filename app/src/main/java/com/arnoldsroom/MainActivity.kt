package com.arnoldsroom

import android.Manifest
import android.app.Activity
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.graphics.drawable.Icon
import android.content.res.Configuration
import android.util.Rational
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.combinedClickable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.geometry.Size as ComposeSize
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.coroutineScope
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
    object About : Screen()
    object MusicLibrary : Screen()
    object MusicPlayer : Screen()
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

// Her own music files, for the music player (May, 2026-11-10): the
// audio side of MediaStore, songs only, sorted by title.
fun loadSongs(resolver: ContentResolver): List<SongItem> {
    val out = mutableListOf<SongItem>()
    val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.TRACK
    )
    resolver.query(
        collection, projection,
        "${MediaStore.Audio.Media.IS_MUSIC} != 0", null,
        "${MediaStore.Audio.Media.TITLE} ASC"
    )?.use { c ->
        val idI = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val tI = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val aI = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val alI = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val dI = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val trI = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
        while (c.moveToNext()) {
            val id = c.getLong(idI)
            val title = c.getString(tI)?.takeIf { it.isNotBlank() } ?: "Unknown title"
            val artist = c.getString(aI)?.takeIf { it.isNotBlank() && it != "<unknown>" } ?: "Unknown artist"
            val album = c.getString(alI)?.takeIf { it.isNotBlank() && it != "<unknown>" } ?: "Unknown album"
            out.add(
                SongItem(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    durationMs = c.getLong(dI),
                    track = c.getInt(trI),
                    uri = Uri.withAppendedPath(collection, id.toString()).toString()
                )
            )
        }
    }
    return out
}

// Track numbers (May, 2026-11-10): MediaStore packs the disc into
// the thousands — 1003 is disc 1 track 3, 2011 is disc 2 track 11 —
// while plenty of files carry a plain 3 or 11, and untagged files 0.
fun trackDisc(track: Int): Int = if (track >= 1000) track / 1000 else 1
fun trackNum(track: Int): Int = if (track >= 1000) track % 1000 else track
fun byTrackOrder(): Comparator<SongItem> = compareBy(
    { trackDisc(it.track) },
    { if (it.track > 0) trackNum(it.track) else Int.MAX_VALUE },
    { it.title.lowercase() }
)
fun trackLabel(track: Int, multiDisc: Boolean): String? = when {
    track <= 0 -> null
    multiDisc -> "${trackDisc(track)}-${trackNum(track)}"
    else -> trackNum(track).toString()
}

fun audioPermission(): String =
    if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

// Send an order to the music service. Queue starts go through the
// foreground-service door (Android insists, since the service must
// show its notification right away); the small orders can use a
// plain start once the service exists.
fun musicAction(context: Context, action: String, build: Intent.() -> Unit = {}) {
    try {
        val intent = Intent(context, MusicService::class.java).setAction(action).apply(build)
        if (action == MusicService.ACTION_PLAY_QUEUE) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
    } catch (e: Exception) { }
}

object PipControl {
    var shouldEnter by mutableStateOf(false)
    var inPip by mutableStateOf(false)
    // The playing video's real shape, reported by the players —
    // the floating window (and the in-app pop-up TV) take it on, so
    // 4:3 shows get a 4:3 window instead of a 16:9 frame.
    var aspect by mutableStateOf(16f / 9f)
    // The floating window's own play/pause button needs to know the
    // state and reach the player: the full player keeps these fresh.
    var playing by mutableStateOf(false)
    var togglePlay: (() -> Unit)? = null
}

const val PIP_TOGGLE_ACTION = "com.arnoldsroom.PIP_TOGGLE"

// Pop-up memory (May, 2026-11-10): where she dragged each pop-up
// and how big she made it now survives closing and reopening it,
// for as long as the app runs. The states live in plain objects —
// process memory, not storage — so a fresh launch starts clean,
// exactly the "until I restart the app" she asked for.
object RadioBoxState {
    val offX = mutableStateOf<Float?>(null)
    val offY = mutableStateOf<Float?>(null)
    val widthDpF = mutableStateOf(230f)
}
// The music library remembers where she was (May, 2026-11-10):
// opening the full player used to reset the library to its
// first tab, because the screen left the composition and its
// remember-state died with it. The place now lives here — tab,
// open album/artist/playlist, search text, and every list's
// scroll — for the session, same as the pop-ups' places.
object MusicLibState {
    val tab = mutableStateOf(0)
    val query = mutableStateOf("")
    val albumPick = mutableStateOf<String?>(null)
    val artistPick = mutableStateOf<String?>(null)
    val playlistPick = mutableStateOf<String?>(null)
    val songsList = LazyListState()
    val albumsList = LazyListState()
    val artistsList = LazyListState()
    val playlistsList = LazyListState()
    val artistAlbumsList = LazyListState()
    val detailList = LazyListState()
}

object MiniTvState {
    val offX = mutableStateOf<Float?>(null)
    val offY = mutableStateOf<Float?>(null)
    val widthDp = mutableStateOf(216.dp)
    // The video's shape lives here too (May, 2026-11-10): it used
    // to reset to 16:9 every time the TV stepped aside for a menu,
    // so the box came back the wrong height until the player
    // reported in again.
    val aspect = mutableStateOf(16f / 9f)
    // Set once the TV has opened for real, so returning from a
    // menu restores it at full size instead of replaying the
    // shrink-in entrance (May, 2026-11-10).
    val everOpened = mutableStateOf(false)
}
object MusicBoxState {
    val offX = mutableStateOf(0f)
    val offY = mutableStateOf(0f)
    val widthDpF = mutableStateOf(175f)
}

// Pop-up traffic control (May, 2026-11-10): the pop-ups may not
// collide or overlap. Each one reports its on-screen rectangle
// here; a drag that would land inside a neighbor slides along
// its edge instead, and a resize that would grow into one is
// refused (the size snaps back to the last one that fit).
object PopRects {
    var radio: Rect? by mutableStateOf(null)
    var music: Rect? by mutableStateOf(null)
    var tv: Rect? by mutableStateOf(null)
    val lastGood = mutableMapOf<String, Float>()
    fun others(self: String): List<Rect> = listOfNotNull(
        if (self != "radio") radio else null,
        if (self != "music") music else null,
        if (self != "tv") tv else null
    )
}

fun Rect.hitsAny(others: List<Rect>, gap: Float = 10f): Boolean =
    others.any { o -> left < o.right + gap && right > o.left - gap && top < o.bottom + gap && bottom > o.top - gap }

fun slideDelta(cur: Rect, dx: Float, dy: Float, others: List<Rect>): Offset {
    if (!cur.translate(Offset(dx, dy)).hitsAny(others)) return Offset(dx, dy)
    if (!cur.translate(Offset(dx, 0f)).hitsAny(others)) return Offset(dx, 0f)
    if (!cur.translate(Offset(0f, dy)).hitsAny(others)) return Offset(0f, dy)
    return Offset.Zero
}

fun popNudge(cur: Rect, others: List<Rect>, gap: Float = 12f): Offset {
    var r = cur
    var dx = 0f
    var dy = 0f
    repeat(4) {
        val o = others.firstOrNull {
            r.left < it.right + gap && r.right > it.left - gap && r.top < it.bottom + gap && r.bottom > it.top - gap
        } ?: return Offset(dx, dy)
        val pushLeft = r.right + gap - o.left
        val pushRight = o.right + gap - r.left
        val pushUp = r.bottom + gap - o.top
        val pushDown = o.bottom + gap - r.top
        when (minOf(pushLeft, pushRight, pushUp, pushDown)) {
            pushLeft -> { dx -= pushLeft; r = r.translate(Offset(-pushLeft, 0f)) }
            pushRight -> { dx += pushRight; r = r.translate(Offset(pushRight, 0f)) }
            pushUp -> { dy -= pushUp; r = r.translate(Offset(0f, -pushUp)) }
            else -> { dy += pushDown; r = r.translate(Offset(0f, pushDown)) }
        }
    }
    return Offset(dx, dy)
}

fun popSizeGuard(self: String, rect: Rect, curW: Float, apply: (Float) -> Unit) {
    val last = PopRects.lastGood[self] ?: curW
    if (rect.hitsAny(PopRects.others(self))) {
        if (kotlin.math.abs(curW - last) > 0.5f) apply(last)
    } else {
        PopRects.lastGood[self] = curW
    }
}

// One builder for every PiP params need: the video's real aspect,
// and (the fix for May's 2026-11-10 report) a real play/pause
// ACTION on the window — without one, the floating player showed
// no pause button at all.
fun buildPipParams(context: Context, autoEnter: Boolean): PictureInPictureParams {
    val a = PipControl.aspect.coerceIn(0.42f, 2.39f)
    val b = PictureInPictureParams.Builder()
        .setAspectRatio(Rational((a * 1000).toInt(), 1000))
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) b.setAutoEnterEnabled(autoEnter)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val intent = Intent(PIP_TOGGLE_ACTION).setPackage(context.packageName)
            val pi = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val iconRes = if (PipControl.playing) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            val label = if (PipControl.playing) "Pause" else "Play"
            b.setActions(listOf(RemoteAction(Icon.createWithResource(context, iconRes), label, label, pi)))
        } catch (e: Exception) { }
    }
    return b.build()
}

class MainActivity : ComponentActivity() {
    private var pipReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Edge-to-edge: the app draws behind the system bars instead of
        // being fenced into the area between them. Without this, phones
        // whose navigation bar reserves space (like the Razr) showed a
        // dead black band under the room — letterboxing at the bottom
        // that no amount of in-app zoom could fill, because the app
        // never got that strip of screen in the first place.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // The floating window's play/pause button broadcasts here.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, i: Intent?) {
                    PipControl.togglePlay?.invoke()
                    try { setPictureInPictureParams(buildPipParams(this@MainActivity, true)) } catch (e: Exception) { }
                }
            }
            pipReceiver = receiver
            val filter = IntentFilter(PIP_TOGGLE_ACTION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(receiver, filter)
            }
        }
        setContent { ArnoldsRoomApp() }
    }

    override fun onDestroy() {
        pipReceiver?.let { try { unregisterReceiver(it) } catch (e: Exception) { } }
        pipReceiver = null
        super.onDestroy()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && PipControl.shouldEnter) {
            try {
                enterPictureInPictureMode(buildPipParams(this, true))
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

// The room's day/night as the APP is currently showing it — the
// real clock plus her moon/sun toggle for this session. The root
// keeps this in step with its own night value; the list-screen
// banners read it, so the cohesive design follows the app's mode
// instead of the raw clock (May, 2026-11-10: the banners stayed in
// night mode no matter what the room was showing).
object RoomNightState {
    var night by mutableStateOf(false)
    var phase by mutableStateOf(DayPhase.DAY)
}

// ---------- App root ----------
@Composable
fun ArnoldsRoomApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("arnolds_room", Context.MODE_PRIVATE) }
    var screen by remember { mutableStateOf<Screen>(Screen.Room) }
    // A real back stack (May, 2026-11-10): Android's back button
    // used to dump her straight back at the room from everywhere —
    // now every step into the video and music menus remembers where
    // it came from, and back retraces it one step at a time.
    // Stepping into the Room clears the trail.
    var backStack by remember { mutableStateOf<List<Screen>>(emptyList()) }
    fun navTo(dest: Screen) {
        if (dest == screen) return
        backStack = if (dest is Screen.Room) emptyList() else backStack + screen
        screen = dest
    }
    var phaseOverride by remember { mutableStateOf<DayPhase?>(null) }
    val phase = phaseOverride ?: phaseForClock()
    val night = phase == DayPhase.NIGHT
    LaunchedEffect(phase) {
        RoomNightState.phase = phase
        RoomNightState.night = night
    }
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
    // Start silent if the phone is silenced (May, 2026-11-10): when
    // the ringer is on vibrate or fully off, the app opens muted.
    // The Music toggle can still turn it on by hand.
    var musicOn by remember {
        mutableStateOf(
            try {
                val am = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
                am.ringerMode == android.media.AudioManager.RINGER_MODE_NORMAL
            } catch (e: Exception) { true }
        )
    }
    var doorGone by remember { mutableStateOf(false) }
    // Sky effects master switch (May, 2026-11-10): one button beside
    // the music button turns the stars AND the lens flare off/on.
    var fxOn by remember { mutableStateOf(true) }

    // Idle dim mode (May, 2026-11-10): two untouched minutes on
    // the room and every icon melts away — just the painting,
    // dimmed, plus any pop-up players still floating. The room
    // also holds the screen awake (below), so the display never
    // times out into black; it settles into the dim instead. One
    // tap anywhere wakes it all back up.
    var lastTouchMs by remember { mutableStateOf(System.currentTimeMillis()) }
    var idleDim by remember { mutableStateOf(false) }
    LaunchedEffect(screen, doorGone) {
        if (screen is Screen.Room && doorGone) {
            while (true) {
                if (!idleDim && System.currentTimeMillis() - lastTouchMs >= 120_000L) idleDim = true
                delay(1000)
            }
        }
    }
    LaunchedEffect(screen) { if (screen !is Screen.Room) idleDim = false }
    // On the room, the screen never sleeps — it dims instead.
    val appView = LocalView.current
    DisposableEffect(screen is Screen.Room) {
        appView.keepScreenOn = screen is Screen.Room
        onDispose { appView.keepScreenOn = false }
    }

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
    // ----- Nightwave Plaza radio (May, 2026-11-10): a live vaporwave
    // stream in a draggable pop-up, opened from a ring in the room.
    // Playing it takes over the room's sound: the room song pauses,
    // and any video — full player or mini pop-up — pauses too (the
    // players watch radioActive and pause themselves). The stream
    // itself lives in RadioService, a foreground service, so it keeps
    // playing when she leaves the app; these just read its state and
    // send it play/stop orders.
    var showRadio by remember { mutableStateOf(false) }
    val radioPlaying = RadioState.playing
    val radioBuffering = RadioState.buffering
    val radioActive = radioPlaying || radioBuffering
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    fun stopRadio() {
        try {
            context.startService(Intent(context, RadioService::class.java).setAction(RadioService.ACTION_STOP))
        } catch (e: Exception) { }
        // The room song restarts by itself via the music effect once
        // the service reports the stream has stopped.
    }
    fun startRadio() {
        pauseMusic()
        // The radio takes the room's sound over completely: her own
        // music player pauses too.
        try {
            context.startService(Intent(context, MusicService::class.java).setAction(MusicService.ACTION_PAUSE))
        } catch (e: Exception) { }
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            try { notifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS) } catch (e: Exception) { }
        }
        try {
            ContextCompat.startForegroundService(
                context,
                Intent(context, RadioService::class.java).setAction(RadioService.ACTION_PLAY)
            )
        } catch (e: Exception) { }
    }
    // ----- Her own music player (May, 2026-11-10): the joystick on
    // the rug opens a full music library + player for the songs on
    // her phone, with a pop-up mini player like the video one. The
    // playback itself lives in MusicService; starting a song takes
    // over the room's sound (radio stops, room song and videos
    // pause), and starting a video or the radio pauses it back.
    var showMusicMini by remember { mutableStateOf(false) }
    fun playSongs(queue: List<SongItem>, index: Int) {
        if (radioActive) stopRadio()
        pauseMusic()
        // Her controls outside the app live in the music
        // notification — make sure Android will actually show it.
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            try { notifLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS) } catch (e: Exception) { }
        }
        MusicState.queue = queue
        // Songs always start from the beginning when she picks one —
        // no resume memory (her call, 2026-11-10).
        musicAction(context, MusicService.ACTION_PLAY_QUEUE) {
            putExtra(MusicService.EXTRA_INDEX, index)
            putExtra(MusicService.EXTRA_POS, 0L)
        }
        showMusicMini = true
        navTo(Screen.MusicPlayer)
    }
    LaunchedEffect(MusicState.playing) {
        if (MusicState.playing && radioActive) stopRadio()
    }
    LaunchedEffect(screen) {
        if (screen is Screen.Player) {
            try {
                context.startService(Intent(context, MusicService::class.java).setAction(MusicService.ACTION_PAUSE))
            } catch (e: Exception) { }
            // The video she tapped takes the room's sound over
            // completely — the Plaza stream stops too, the same
            // takeover her songs and the radio already do to each
            // other.
            if (radioActive) stopRadio()
            // Mark her songs paused right away too: the video players
            // hush themselves while MusicState.playing reads true, and
            // waiting for the service to report back could leave the
            // video she tapped frozen on its first frame.
            MusicState.playing = false
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, screen) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    // Radio state is read LIVE here, not through
                    // radioActive (May, 2026-11-10): this observer
                    // outlives the composition that created it, and
                    // radioActive is a computed value, not state —
                    // its stale copy kept restarting the room song
                    // over the playing radio every time she came
                    // back to the app.
                    if (miniQueue == null && musicOn && doorGone && !RadioState.playing && !RadioState.buffering && !MusicState.playing && !MusicState.wantsPlay && screen !is Screen.Player && screen !is Screen.MusicPlayer) startMusic()
                }
                Lifecycle.Event.ON_STOP -> { pauseMusic() }
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(screen, musicOn, miniQueue, doorGone, radioActive, MusicState.playing, MusicState.wantsPlay) {
        // The room song is the app's ambience (May, 2026-11-10): it
        // keeps playing while she browses — the libraries, shelves,
        // search, stats, settings — and only yields when something
        // else actually plays: one of her songs, the radio, or a
        // video (the full video player and the music player's own
        // screen stay quiet for whatever is loaded there). It still
        // waits until the doors have fully opened first.
        if (musicOn && miniQueue == null && doorGone && !radioActive && !MusicState.playing && !MusicState.wantsPlay &&
            screen !is Screen.Player && screen !is Screen.MusicPlayer
        ) {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) startMusic()
        } else pauseMusic()
    }
    // Keeper (May, 2026-11-10 — "make the app's music play in the
    // settings too"): while the ambience is wanted, check every
    // couple of seconds and restart the song if anything stopped it
    // along the way, so it can never silently go missing from a
    // screen where it belongs.
    LaunchedEffect(screen, musicOn, miniQueue, doorGone, radioActive, MusicState.playing, MusicState.wantsPlay) {
        val wanted = musicOn && miniQueue == null && doorGone && !radioActive && !MusicState.playing && !MusicState.wantsPlay &&
            screen !is Screen.Player && screen !is Screen.MusicPlayer
        if (wanted) {
            while (true) {
                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    var need = false
                    try { need = music?.isPlaying != true } catch (e: Exception) { }
                    if (need) {
                        // Confirm after a breath before starting
                        // (May, 2026-11-10): a song or stream can
                        // read as idle for a moment while it buffers
                        // between tracks, and the room song must not
                        // jump in over it — that was the leak she
                        // heard once or twice.
                        delay(1200)
                        val stillWanted = musicOn && miniQueue == null && doorGone &&
                            !RadioState.playing && !RadioState.buffering &&
                            !MusicState.playing && !MusicState.wantsPlay &&
                            screen !is Screen.Player && screen !is Screen.MusicPlayer
                        var stillNeed = false
                        try { stillNeed = music?.isPlaying != true } catch (e: Exception) { }
                        if (stillWanted && stillNeed) {
                            try { startMusic() } catch (e: Exception) { }
                        }
                    }
                }
                delay(2000)
            }
        }
    }
    // No status bar anywhere in the app — May asked for it gone
    // altogether (it used to come back outside the room and stay).
    // The navigation bar stays VISIBLE everywhere, though (reversed
    // 2026-11-10 at May's request): with it hidden, her gestures
    // needed a first swipe just to reveal the bars before back/home
    // would work. With it shown, gestures land first try. The Razr's
    // black bottom strip stays as it is — the room's raised bottom
    // texts make it read as a frame there.
    val activity = context as? Activity
    LaunchedEffect(Unit) {
        val act = activity ?: return@LaunchedEffect
        // The room paints to the very bottom edge (May, 2026-11-10):
        // the navigation bar goes fully transparent — no black band
        // under the room and no contrast scrim — so the painting
        // shows through the bar's zone and only the pill or buttons
        // float over it. The room's credits and clock sit down in
        // that zone now.
        act.window.navigationBarColor = android.graphics.Color.TRANSPARENT
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            act.window.isNavigationBarContrastEnforced = false
        }
        val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    LaunchedEffect(screen) {
        val act = activity ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(act.window, act.window.decorView)
        controller.hide(WindowInsetsCompat.Type.statusBars())
        controller.show(WindowInsetsCompat.Type.navigationBars())
    }
    DisposableEffect(Unit) { onDispose { try { music?.release() } catch (e: Exception) { } } }

    // ----- Permission + library load -----
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        granted = ok
        if (ok) {
            pendingDest?.let { navTo(it) }
            pendingDest = null
        }
    }
    LaunchedEffect(granted) {
        if (granted) videos = withContext(Dispatchers.IO) { loadVideos(context.contentResolver) }
    }
    // Fresh scan on every entry (May, 2026-11-10): whenever she
    // steps into a video screen — library, categories, search, the
    // player itself — the list is re-read from the phone, so a video
    // downloaded a minute ago is already there. (The music library
    // already reloads its songs each time it opens.)
    LaunchedEffect(screen, granted) {
        if (granted && screen !is Screen.Room) {
            videos = withContext(Dispatchers.IO) { loadVideos(context.contentResolver) }
        }
    }
    fun go(dest: Screen, needsVideos: Boolean = true) {
        if (needsVideos && !granted) {
            pendingDest = dest
            launcher.launch(videoPermission())
        } else {
            navTo(dest)
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
            navTo(Screen.Player(src, src.indices.random(), "Shuffle"))
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

    fun navigateBack() {
        when {
            // Only when the radio is actually on screen (in the
            // room) does Back close it. From a menu, Back must go
            // back — it used to close the hidden radio behind the
            // menu instead, so returning to the room found the
            // radio gone (May, 2026-11-10).
            showRadio && screen is Screen.Room -> showRadio = false
            backStack.isNotEmpty() -> {
                screen = backStack.last()
                backStack = backStack.dropLast(1)
            }
            screen is Screen.MusicPlayer -> screen = Screen.MusicLibrary
            else -> navTo(Screen.Room)
        }
    }
    BackHandler(enabled = screen !is Screen.Room) { navigateBack() }

    val libTick = libVersion
    MaterialTheme(typography = cartoonTypography()) {
    Box(
        Modifier.fillMaxSize().background(RoomDark)
            .pointerInput(Unit) {
                // A silent witness to every touch (May, 2026-11-10):
                // it never eats a tap — except the one that wakes
                // the room from its idle dim, which it swallows so
                // waking up doesn't also press whatever it lands on.
                awaitPointerEventScope {
                    while (true) {
                        val ev = awaitPointerEvent(PointerEventPass.Initial)
                        if (ev.type == PointerEventType.Press) {
                            lastTouchMs = System.currentTimeMillis()
                            if (idleDim) {
                                idleDim = false
                                ev.changes.forEach { it.consume() }
                            }
                        }
                    }
                }
            }
    ) {
        if (screen !is Screen.Room && screen !is Screen.Player && screen !is Screen.MusicPlayer) {
            RoomBackdrop()
        }
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                // More motion, especially around the players
                // (May, 2026-11-10): stepping into the video or
                // music player, it rises up over the room; stepping
                // back out, it sinks away. Everything else keeps
                // the gentle fade-and-grow.
                when {
                    targetState is Screen.Player || targetState is Screen.MusicPlayer ->
                        (slideInVertically(animationSpec = tween(320)) { h -> h / 3 } + fadeIn(tween(320))) togetherWith
                            fadeOut(tween(180))
                    initialState is Screen.Player || initialState is Screen.MusicPlayer ->
                        fadeIn(tween(260)) togetherWith
                            (slideOutVertically(animationSpec = tween(300)) { h -> h / 3 } + fadeOut(tween(300)))
                    // Continuity pass (May, 2026-11-10): a menu is a
                    // place inside the room, not another app — it
                    // rises up out of the room when you open it and
                    // sinks back down when you return, and the room
                    // itself only ever fades, never scales, so it
                    // feels like the fixed place it is.
                    initialState is Screen.Room ->
                        (slideInVertically(animationSpec = tween(300)) { h -> h / 5 } + fadeIn(tween(300))) togetherWith
                            fadeOut(tween(200))
                    targetState is Screen.Room ->
                        fadeIn(tween(280)) togetherWith
                            (slideOutVertically(animationSpec = tween(280)) { h -> h / 5 } + fadeOut(tween(280)))
                    else ->
                        (fadeIn(tween(240)) + scaleIn(initialScale = 0.985f, animationSpec = tween(240))) togetherWith
                            fadeOut(tween(160))
                }
            },
            label = "screen"
        ) { s ->
            when (s) {
            is Screen.Room -> RoomScreen(
                phase = phase,
                onCyclePhase = {
                    phaseOverride = when (phase) {
                        DayPhase.DAWN -> DayPhase.DAY
                        DayPhase.DAY -> DayPhase.DUSK
                        DayPhase.DUSK -> DayPhase.NIGHT
                        DayPhase.NIGHT -> DayPhase.DAWN
                    }
                },
                musicOn = musicOn,
                onToggleMusic = { musicOn = !musicOn },
                onOpenRadio = { showRadio = true },
                fxOn = fxOn,
                onToggleFx = { fxOn = !fxOn },
                doorsOpen = doorGone,
                dimmed = idleDim,
                onHotspot = { dest -> go(dest, needsVideos = dest !is Screen.MusicLibrary) },
                onShuffle = {
                    pauseMusic()
                    if (!granted) {
                        pendingShuffle = true
                        launcher.launch(videoPermission())
                    } else if (videos.isNotEmpty()) {
                        val pool = videos.filter { isEntertainment(it) }
                        val src = if (pool.isNotEmpty()) pool else videos
                        val pick = src.indices.random()
                        navTo(Screen.Player(src, pick, "Shuffle"))
                    }
                }
            )
            is Screen.Categories -> CategoriesScreen(
                counts = Filter.values().associateWith { filtered(it).size },
                showsCount = detectShows(videos, ::catOf).size,
                movieFolderCount = detectMovieFolders(videos, ::catOf).size,
                onPick = { navTo(Screen.Library(it)) },
                onShows = { navTo(Screen.Shows) },
                onMovieFolders = { navTo(Screen.MovieFolders) },
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
            )
            is Screen.Shows -> ShowsScreen(
                shows = detectShows(videos, ::catOf),
                isWatched = ::isWatched,
                savedPos = ::savedPos,
                onPick = { navTo(Screen.ShowDetail(it)) },
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
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
                        onPlay = { queue, idx -> navTo(Screen.Player(queue, idx, s.showTitle)) },
                        onRoom = { navTo(Screen.Room) },
                        onBack = { navigateBack() }
                    )
                } else {
                    ShowsScreen(
                        shows = detectShows(videos, ::catOf),
                        isWatched = ::isWatched,
                        savedPos = ::savedPos,
                        onPick = { navTo(Screen.ShowDetail(it)) },
                        onRoom = { navTo(Screen.Room) },
                        onBack = { navigateBack() }
                    )
                }
            }
            is Screen.MovieFolders -> MovieFoldersScreen(
                folders = detectMovieFolders(videos, ::catOf),
                onPick = { navTo(Screen.FolderDetail(it)) },
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
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
                    onPlay = { queue, idx -> navTo(Screen.Player(queue, idx, folder?.title ?: "Movies")) },
                    onRoom = { navTo(Screen.Room) },
                    onBack = { navigateBack() }
                )
            }
            is Screen.Library -> LibraryScreen(
                title = s.filter.label,
                items = filtered(s.filter),
                prefs = prefs,
                catOf = ::catOf,
                isFav = ::isFav,
                savedPos = ::savedPos,
                onPlay = { queue, idx -> navTo(Screen.Player(queue, idx, s.filter.label)) },
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() },
                onStats = { navTo(Screen.Stats) }
            )
            is Screen.Search -> SearchScreen(
                items = videos,
                prefs = prefs,
                catOf = ::catOf,
                isFav = ::isFav,
                savedPos = ::savedPos,
                onPlay = { queue, idx -> navTo(Screen.Player(queue, idx, "Search")) },
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
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
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
            )
            is Screen.Stats -> StatsScreen(
                videos = videos,
                prefs = prefs,
                isWatched = ::isWatched,
                savedPos = ::savedPos,
                showsCount = detectShows(videos, ::catOf).size,
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
            )
            is Screen.About -> AboutScreen(
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
            )
            is Screen.MusicLibrary -> MusicLibraryScreen(
                prefs = prefs,
                onPlaySong = { q, idx -> playSongs(q, idx) },
                onOpenPlayer = { navTo(Screen.MusicPlayer) },
                onRoom = { navTo(Screen.Room) },
                onBack = { navigateBack() }
            )
            is Screen.MusicPlayer -> MusicPlayerScreen(
                prefs = prefs,
                onBack = { navigateBack() },
                onShrink = {
                    showMusicMini = true
                    navTo(Screen.Room)
                }
            )
            is Screen.Player -> PlayerScreen(
                queue = s.queue,
                startIndex = s.startIndex,
                fromLabel = s.fromLabel,
                prefs = prefs,
                onBack = { navigateBack() },
                onShrink = { idx ->
                    miniQueue = s.queue
                    miniIndex = idx
                    navTo(Screen.Room)
                },
                radioPause = radioActive || MusicState.playing
            )
            }
        }
        // The pop-ups ride along (continuity pass, May, 2026-11-10):
        // the mini TV, the radio and the cassette used to vanish the
        // moment any menu opened, which threw her out of the room.
        // Now they follow her through every menu and only step aside
        // for the two full-screen players.
        if (doorGone && screen !is Screen.Player && screen !is Screen.MusicPlayer) {
            miniQueue?.let { q ->
                MiniPlayerBox(
                    queue = q,
                    startIndex = miniIndex,
                    prefs = prefs,
                    onExpand = { idx ->
                        miniQueue = null
                        navTo(Screen.Player(q, idx, "Mini player"))
                    },
                    onClose = { miniQueue = null },
                    radioPause = radioActive || MusicState.playing
                )
            }
        }
        if (screen !is Screen.Player && screen !is Screen.MusicPlayer && showRadio) {
            RadioPlayerBox(
                playing = radioPlaying,
                buffering = radioBuffering,
                onToggle = { if (radioActive) stopRadio() else startRadio() },
                onClose = { showRadio = false }
            )
        }
        if (MusicState.hasMedia && screen !is Screen.Player && screen !is Screen.MusicPlayer && doorGone && showMusicMini && !PipControl.inPip) {
            MusicMiniBox(
                onExpand = { navTo(Screen.MusicPlayer) },
                onClose = { showMusicMini = false }
            )
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
                    Modifier.size(38.dp).shadow(4.dp, CircleShape).clip(CircleShape)
                        .background(Color(0xCC10131A))
                        .border(1.8.dp, RoomYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (musicOn) Icons.Filled.MusicNote else Icons.Filled.MusicOff,
                        if (musicOn) "Pause music" else "Play music",
                        tint = if (musicOn) RoomYellow else RoomTextDim,
                        modifier = Modifier.size(18.dp)
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
                        .background(Color(0x8810131A))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
            // The effects button rides beside it on the doors too.
            Column(
                Modifier.align(Alignment.TopStart).padding(start = 64.dp, top = 12.dp)
                    .clickable { fxOn = !fxOn },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(38.dp).shadow(4.dp, CircleShape).clip(CircleShape)
                        .background(Color(0xCC10131A))
                        .border(1.8.dp, RoomYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (fxOn) Icons.Filled.Star else Icons.Filled.StarBorder,
                        if (fxOn) "Turn effects off" else "Turn effects on",
                        tint = if (fxOn) RoomYellow else RoomTextDim,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    if (fxOn) "Effects" else "No FX",
                    color = Color.White,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0x8810131A))
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

// The room's four times of day (May, 2026-11-10): dawn and dusk
// join day and night, each with its own paintings on both screens.
// Left alone, the room follows the real clock; the corner button
// steps through all four by hand for the session.
enum class DayPhase { DAWN, DAY, DUSK, NIGHT }

fun phaseForClock(): DayPhase {
    val h = java.time.LocalTime.now().hour
    return when {
        h in 5..6 -> DayPhase.DAWN
        h in 7..18 -> DayPhase.DAY
        h in 19..20 -> DayPhase.DUSK
        else -> DayPhase.NIGHT
    }
}

fun isNightNow(): Boolean = phaseForClock() == DayPhase.NIGHT

// A safety net for the dawn/dusk art: if the uploaded file's name
// holds the word (whatever else the phone wrapped around it), find
// the drawable whose name carries it for this screen shape.
fun findPhaseDrawableId(context: Context, keyword: String, tall: Boolean): Int {
    return try {
        val cls = Class.forName(context.packageName + ".R\$drawable")
        cls.fields.firstOrNull { f ->
            val n = f.name.lowercase()
            n.contains(keyword) && (if (tall) n.contains("tall") else !n.contains("tall"))
        }?.getInt(null) ?: 0
    } catch (e: Exception) { 0 }
}

@Composable
fun rememberRoomBitmap(tall: Boolean, phase: DayPhase): Bitmap? {
    val context = LocalContext.current
    return remember(tall, phase) {
        try {
            var bmp: Bitmap? = null
            // Dawn and dusk get first try at their own paintings.
            if (phase == DayPhase.DAWN || phase == DayPhase.DUSK) {
                val kw = if (phase == DayPhase.DAWN) "dawn" else "dusk"
                val pid = findDrawableId(context, if (tall) "arnolds_room_tall_$kw" else "arnolds_room_wide_$kw")
                    .takeIf { it != 0 } ?: findPhaseDrawableId(context, kw, tall)
                if (pid != 0) bmp = android.graphics.BitmapFactory.decodeResource(context.resources, pid)
            }
            if (bmp == null) {
                // Until the dawn/dusk art is in the app, dawn borrows
                // the day and dusk borrows the night.
                val names = when {
                    tall && (phase == DayPhase.NIGHT || phase == DayPhase.DUSK) -> listOf("arnolds_room_tall_night", "file_00000000f44881f585d18819f3427bf8", "arnolds_room_tall", "file_00000000266481f58cd2d715ef42fabe", "arnolds_room_night", "file_00000000395c81f5987ceacf0dab6e59", "arnolds_room")
                    tall -> listOf("arnolds_room_tall", "file_00000000266481f58cd2d715ef42fabe", "arnolds_room")
                    phase == DayPhase.NIGHT || phase == DayPhase.DUSK -> listOf("arnolds_room_night", "file_00000000395c81f5987ceacf0dab6e59", "arnolds_room")
                    else -> listOf("file_00000000ed6081f586fc3d5ad2e02ce3", "arnolds_room_wide", "arnolds_room")
                }
                for (n in names) {
                    val id = findDrawableId(context, n)
                    if (id != 0) {
                        bmp = android.graphics.BitmapFactory.decodeResource(context.resources, id)
                        if (bmp != null) break
                    }
                }
            }
            bmp
        } catch (e: Exception) { null }
    }
}

// The cohesive backgrounds follow the screen the app is
// actually on (May, 2026-11-10): the tall painting on the cover
// screen, the wide painting unfolded — the room's own 0.62 rule.
// The backdrop, headers and music banner used to hardcode the
// wide painting, so the folded phone wore the inner screen's room.
@Composable
fun rememberRoomBitmapForWindow(phase: DayPhase): Bitmap? {
    val cfg = LocalConfiguration.current
    val tall = cfg.screenWidthDp.toFloat() / cfg.screenHeightDp.coerceAtLeast(1).toFloat() < 0.62f
    return rememberRoomBitmap(tall, phase)
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
fun SkyEffects(night: Boolean, tall: Boolean, t: Float) {
    // A living sky over the painting's own sky, drawn inside the
    // painting's coordinate system so it tracks on any screen. The
    // shapes come from the paintings themselves (pixel-measured
    // 2026-11-10): the glass ceiling is a wide trapezoid spanning the
    // whole top and narrowing downward, and the sun/moon sits in the
    // upper-right panes — sun wide (0.793, 0.145), tall (0.776, 0.078);
    // moon wide (0.802, 0.146), tall (0.774, 0.094).
    val stars = remember(tall) {
        // Spread evenly instead of purely at random (May,
        // 2026-11-10): random scatter clumped stars together and
        // dropped some into hidden corners of the glass. The
        // trapezoid is cut into a grid of cells — one star per cell,
        // jittered inside it — so the whole skylight is covered, the
        // edges are respected, and the moon stays clear.
        val rnd = kotlin.random.Random(if (tall) 7 else 42)
        val cols = if (tall) 6 else 7
        val rowsN = 4
        val yTop = if (tall) 0.005f else 0.010f
        val yBot = if (tall) 0.235f else 0.270f
        val halfTop = if (tall) 0.45f else 0.46f
        val halfBot = if (tall) 0.19f else 0.28f
        val moonX = if (tall) 0.774f else 0.802f
        val moonY = if (tall) 0.094f else 0.146f
        val moonR = if (tall) 0.068f else 0.062f
        val out = mutableListOf<Triple<Float, Float, Float>>()
        for (r in 0 until rowsN) {
            val bandTop = yTop + (yBot - yTop) * r / rowsN
            val bandBot = yTop + (yBot - yTop) * (r + 1) / rowsN
            for (c in 0 until cols) {
                var placed: Triple<Float, Float, Float>? = null
                var tries = 0
                while (placed == null && tries < 8) {
                    tries++
                    val fy = bandTop + (0.25f + 0.5f * rnd.nextFloat()) * (bandBot - bandTop)
                    val half = (halfTop + (halfBot - halfTop) * ((fy - yTop) / (yBot - yTop))) * 0.92f
                    val cellW = 2f * half / cols
                    val fx = 0.5f - half + cellW * (c + 0.25f + 0.5f * rnd.nextFloat())
                    if (kotlin.math.hypot((fx - moonX).toDouble(), (fy - moonY).toDouble()) < moonR) continue
                    placed = Triple(fx, fy, rnd.nextFloat())
                }
                placed?.let { out.add(it) }
            }
        }
        out
    }
    Canvas(Modifier.fillMaxSize()) {
        val scaleF = (size.width / 900f).coerceIn(0.7f, 1.6f)
        if (night) {
            stars.forEach { (fx, fy, seed) ->
                val phase = (t + seed) % 1f
                val tw = kotlin.math.sin(phase * 2f * Math.PI.toFloat()).coerceAtLeast(0f)
                val a = 0.10f + 0.90f * tw * tw
                val cx = fx * size.width
                val cy = fy * size.height
                val r = (2.4f + 3.6f * seed) * (0.7f + 0.5f * tw) * scaleF
                val col = Color(0xFFFFF8E1).copy(alpha = a)
                drawCircle(col, radius = r * 0.32f, center = Offset(cx, cy))
                drawLine(col, Offset(cx - r, cy), Offset(cx + r, cy), strokeWidth = r * 0.20f, cap = StrokeCap.Round)
                drawLine(col, Offset(cx, cy - r), Offset(cx, cy + r), strokeWidth = r * 0.20f, cap = StrokeCap.Round)
            }
        } else {
            val sunFx = if (tall) 0.776f else 0.793f
            val sunFy = if (tall) 0.078f else 0.145f
            val cx = sunFx * size.width
            val cy = sunFy * size.height
            val breathe = 0.5f + 0.5f * kotlin.math.sin(t * 2f * Math.PI.toFloat())
            // The warm halo, breathing visibly this time. Drawn with
            // BlendMode.Plus: plain alpha cream washed out completely
            // over the already-bright painted sun (May: 'no lens flare'
            // even when it was centered right) — adding light is what
            // makes a flare read as a flare.
            // The wide (inner Fold screen) flare runs gentler than the
            // tall one — at the size the big screen shows it, the tall
            // dose was overwhelming (May, 2026-11-10).
            val radius = size.width * (if (tall) 0.19f else 0.17f) * (0.92f + 0.16f * breathe)
            drawCircle(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    listOf(
                        Color(0xFFFFF9C4).copy(alpha = (if (tall) 0.11f else 0.07f) + (if (tall) 0.06f else 0.04f) * breathe),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = radius
                ),
                radius = radius,
                center = Offset(cx, cy),
                blendMode = androidx.compose.ui.graphics.BlendMode.Plus
            )
            // Six rays reaching past the painted sunburst, swaying
            // gently and stretching with the breath — the visible
            // motion. Long and additive now, so they can be seen
            // against the burst instead of hiding inside it.
            val sway = 10f * kotlin.math.sin(t * 2f * Math.PI.toFloat())
            val rayLen = size.width * 0.12f * (0.90f + 0.25f * breathe)
            val rayCol = Color(0xFFFFF59D).copy(alpha = if (tall) 0.16f else 0.10f)
            for (k in 0..5) {
                val ang = Math.toRadians((sway + k * 30f).toDouble())
                val dx = kotlin.math.cos(ang).toFloat()
                val dy = kotlin.math.sin(ang).toFloat()
                drawLine(
                    rayCol,
                    Offset(cx - dx * rayLen * 0.35f, cy - dy * rayLen * 0.35f),
                    Offset(cx + dx * rayLen, cy + dy * rayLen),
                    strokeWidth = 4.5f * scaleF,
                    cap = StrokeCap.Round,
                    blendMode = androidx.compose.ui.graphics.BlendMode.Plus
                )
            }
            // Lens ghosts on the sun-through-center line, drifting a
            // breath along it: wide sun throws them lower-left, tall
            // sun throws them nearly straight down.
            val dirX = 0.5f - sunFx
            val dirY = 0.5f - sunFy
            listOf(
                Triple(0.62f, if (tall) 0.08f else 0.05f, 30f),
                Triple(1.05f, if (tall) 0.055f else 0.035f, 18f)
            ).forEach { (k, alpha, rad) ->
                val drift = (breathe - 0.5f) * 0.012f
                drawCircle(
                    Color(0xFFB3E5FC).copy(alpha = alpha + 0.02f * breathe),
                    radius = rad * scaleF,
                    center = Offset(
                        (sunFx + dirX * (1f + k) + drift) * size.width,
                        (sunFy + dirY * (1f + k) + drift) * size.height
                    )
                )
            }
        }
    }
}

@Composable
fun RoomChip(spot: Hotspot, art: Bitmap?, imgWpx: Int, index: Int, tapped: Boolean, compact: Boolean, pulse: Float, onTap: () -> Unit) {
    // The chip is a clear ring on the painting itself (May, 2026-11-10):
    // the circle's inside is transparent, so what you see in it is the
    // room behind it — no crop to drift off its thing, ever. Around it,
    // just enough dressing to lift it off the wall: a soft glow in the
    // spot's own color, a drop shadow, and a two-tone rim (a dark ink
    // ring outside, the spot's color inside). The glow breathes with
    // the room's pulse until she taps something. (The art/imgWpx params
    // are vestigial from the crop era; the corner toggles still crop.)
    val chipDp = if (compact) 52.dp else 68.dp
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(index * 70L); shown = true }
    val scale = animateFloatAsState(
        if (shown) 1f else 0.3f,
        spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow), label = "chipPop"
    )
    val color = if (tapped) RoomYellow else spot.color
    // The Plaza ring's pink reads hotter than the other colors at the
    // same alpha — its glow is dimmed a touch so all the rings pulse
    // at the same felt strength (May, 2026-11-10).
    // Strengthened at her word (2026-11-10): the pulse now covers
    // the whole icon — full strength held out to the ring's edge —
    // and swings a little deeper so it reads clearly.
    val glowAlpha = (0.18f + 0.30f * pulse) * if (spot.label == "Plaza") 0.7f else 1f
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(chipDp)
                .scale(if (tapped) scale.value * 1.18f else scale.value)
                .alpha(if (shown) 1f else 0f),
            contentAlignment = Alignment.Center
        ) {
            // The halo: the spot's color, breathed out around the circle.
            // Drawn at its true size, not scaled up from a smaller box —
            // scaling a clipped circle up rendered its edge as a faceted
            // little octagon inside the ring (May, 2026-11-10). The
            // three-stop gradient softens the falloff further.
            Box(
                Modifier
                    .size(chipDp * 1.5f)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.radialGradient(
                            colorStops = arrayOf(
                                0f to color.copy(alpha = glowAlpha),
                                0.66f to color.copy(alpha = glowAlpha),
                                0.85f to color.copy(alpha = glowAlpha * 0.4f),
                                1f to Color.Transparent
                            )
                        )
                    )
            )
            // The circle itself: shadow first so it floats, then the two
            // rings — dark ink outside, the spot's color inside — and
            // nothing inside them: the painting shows straight through.
            Box(
                Modifier
                    .size(chipDp)
                    .shadow(7.dp, CircleShape)
                    .clip(CircleShape)
                    .border(2.5.dp, Color(0xFF10131A), CircleShape)
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

// The phone's own navigation bar overlaps the bottom of the
// app — the app draws edge to edge under it, and the bar's
// height varies a lot by phone (a slim gesture pill on some, a
// fat button hotbar on others). Bottom-anchored things ask the
// system for the real height and lift clear of it.
@Composable
private fun navBarInsetDp(): Dp {
    val density = LocalDensity.current
    return with(density) { WindowInsets.navigationBars.getBottom(density).toDp() }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomScreen(onHotspot: (Screen) -> Unit, onShuffle: () -> Unit, phase: DayPhase, onCyclePhase: () -> Unit, musicOn: Boolean, onToggleMusic: () -> Unit, onOpenRadio: () -> Unit, fxOn: Boolean, onToggleFx: () -> Unit, doorsOpen: Boolean, dimmed: Boolean = false) {
    val spots = listOf(
        Hotspot(0.13f, 0.44f, "Search", Icons.Filled.Search, Screen.Search, Color(0xFF2979FF)),
        Hotspot(0.47f, 0.350f, "Shelves", Icons.Filled.Folder, Screen.Categories, Color(0xFF7C4DFF)),
        Hotspot(0.135f, 0.345f, "Plaza", Icons.Filled.Radio, null, Color(0xFFFF6EC7)),
        Hotspot(0.635f, 0.395f, "Hey Arnold", Icons.Filled.Tv, Screen.About, Color(0xFFFF5722)),
        Hotspot(0.55f, 0.53f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE), Color(0xFF00C853)),
        Hotspot(0.075f, 0.66f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES), Color(0xFFF50057)),
        Hotspot(0.685f, 0.685f, "Shuffle", Icons.Filled.Shuffle, null, Color(0xFF00ACC1)),
        Hotspot(0.915f, 0.47f, "Settings", Icons.Filled.Settings, Screen.Settings, Color(0xFFFF5252)),
        Hotspot(0.31f, 0.78f, "Music", Icons.Filled.MusicNote, Screen.MusicLibrary, Color(0xFFB388FF))
    )
    // Hotspots measured on the new wide daytime painting (the long room view).
    val wideDaySpots = listOf(
        Hotspot(0.124f, 0.501f, "Search", Icons.Filled.Search, Screen.Search, Color(0xFF2979FF)),
        Hotspot(0.40f, 0.360f, "Shelves", Icons.Filled.Folder, Screen.Categories, Color(0xFF7C4DFF)),
        Hotspot(0.130f, 0.260f, "Plaza", Icons.Filled.Radio, null, Color(0xFFFF6EC7)),
        Hotspot(0.650f, 0.400f, "Hey Arnold", Icons.Filled.Tv, Screen.About, Color(0xFFFF5722)),
        Hotspot(0.548f, 0.518f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE), Color(0xFF00C853)),
        Hotspot(0.075f, 0.653f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES), Color(0xFFF50057)),
        Hotspot(0.708f, 0.682f, "Shuffle", Icons.Filled.Shuffle, null, Color(0xFF00ACC1)),
        Hotspot(0.912f, 0.534f, "Settings", Icons.Filled.Settings, Screen.Settings, Color(0xFFFF5252)),
        Hotspot(0.315f, 0.870f, "Music", Icons.Filled.MusicNote, Screen.MusicLibrary, Color(0xFFB388FF))
    )
    val tallSpots = listOf(
        Hotspot(0.115f, 0.478f, "Search", Icons.Filled.Search, Screen.Search, Color(0xFF2979FF)),
        Hotspot(0.370f, 0.415f, "Shelves", Icons.Filled.Folder, Screen.Categories, Color(0xFF7C4DFF)),
        Hotspot(0.120f, 0.335f, "Plaza", Icons.Filled.Radio, null, Color(0xFFFF6EC7)),
        Hotspot(0.678f, 0.412f, "Hey Arnold", Icons.Filled.Tv, Screen.About, Color(0xFFFF5722)),
        Hotspot(0.558f, 0.530f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE), Color(0xFF00C853)),
        Hotspot(0.058f, 0.575f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES), Color(0xFFF50057)),
        Hotspot(0.752f, 0.600f, "Shuffle", Icons.Filled.Shuffle, null, Color(0xFF00ACC1)),
        Hotspot(0.925f, 0.452f, "Settings", Icons.Filled.Settings, Screen.Settings, Color(0xFFFF5252)),
        Hotspot(0.312f, 0.646f, "Music", Icons.Filled.MusicNote, Screen.MusicLibrary, Color(0xFFB388FF))
    )
    // (Shake-to-shuffle lived here until 2026-11-10 — May asked for it
    // gone; tapping the remote is the way to shuffle now.)
    val sensorContext = LocalContext.current
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
    val skyTrans = rememberInfiniteTransition(label = "skyAnim")
    val skyT by skyTrans.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3600, easing = LinearEasing), RepeatMode.Restart),
        label = "skyT"
    )
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
        // No room-zoom on tap anymore (May, 2026-11-10): scaling the
        // room toward the tapped spot read as the whole app shifting —
        // worst on the lower icons. The chip's own pop is feedback
        // enough; the room stays still.
        scope.launch {
            val d = spot.dest
            if (spot.label == "Plaza") onOpenRadio()
            else if (d != null) onHotspot(d) else onShuffle()
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
    // Stay-aware: the app hides the status bar everywhere, so the room
    // itself carries the time (handwritten, bottom-left corner) and the
    // battery (a small % under the day/night toggle). Ticks every 15s.
    var clockText by remember { mutableStateOf("") }
    var batteryPct by remember { mutableStateOf(-1) }
    LaunchedEffect(Unit) {
        val fmt = java.text.SimpleDateFormat("h:mm a", java.util.Locale.US)
        while (true) {
            clockText = fmt.format(java.util.Date())
            try {
                val bi = sensorContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val level = bi?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scaleB = bi?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, 100) ?: 100
                if (level >= 0 && scaleB > 0) batteryPct = level * 100 / scaleB
            } catch (e: Exception) { }
            delay(15_000)
        }
    }
    BoxWithConstraints(
        Modifier.fillMaxSize().background(Color.Black)
            .pointerInput(Unit) { detectTapGestures(onTap = { hintTick++ }) }
    ) {
        val maxWDp = maxWidth
        val maxHDp = maxHeight
        val tallMode = maxWDp / maxHDp < 0.62f
        // Razr-shaped screens (aspect ~0.41) get extra lift in the
        // base pad below; the edge pad then puts the credits and
        // clock right at the bottom on that shape (May, 2026-11-10).
        val winAspect = maxWDp / maxHDp
        // Lift clear of BOTH flat-bottom traps: the Razr's black
        // strip (the shape rule) and the phone's own navigation
        // bar, whose real height is asked from the system — a
        // fat button hotbar lifts the texts much further than a
        // slim gesture pill does (a download user's S25+ hotbar
        // was eating the room's bottom row, 2026-11-10).
        val bottomTextPad = maxOf(
            if (winAspect in 0.40f..0.42f) 30.dp else 12.dp,
            navBarInsetDp() + 4.dp
        )
        // The credits and the clock/battery sit at the VERY bottom
        // on Razr-shaped windows (May, 2026-11-10) — down in the
        // transparent bar zone on the painting itself, where that
        // phone's black band was. On her Fold they keep the exact
        // spot they already had: that view is perfect, don't touch
        // it. The "Tap something" hint keeps its spot everywhere.
        val bottomEdgePad = if (winAspect in 0.40f..0.42f) 5.dp else bottomTextPad
        // One spot set per screen shape: the day and night paintings share the
        // same composition, so the buttons must not move when the light changes.
        val activeSpots = if (tallMode) tallSpots else wideDaySpots
        val roomBitmap = rememberRoomBitmap(tallMode, phase)
        val aspect = roomBitmap?.let { it.width.toFloat() / it.height.toFloat() }
            ?: if (tallMode) 783f / 2007f else 1704f / 2048f
        var fitW = maxWDp
        var fitH = maxWDp / aspect
        if (fitH > maxHDp) { fitH = maxHDp; fitW = maxHDp * aspect }
        // Adaptive zoom: the two layouts (wide and tall) stay, but when a
        // screen's shape doesn't match the painting's, pure fit leaves
        // black bars. So the painting zooms — per phone, by however
        // much that screen needs — until it fills the whole screen
        // (May, 2026-11-10: the app is made for her Fold 5 but must
        // adapt to other phones and tablets; zoom the photo to fit).
        // The spots are placed from this same rect, and the chips
        // crop from it too, so the buttons always sit exactly on their
        // things, on any screen. The 2x cap only guards absurd
        // freeform-window shapes; no real phone or tablet nears it.
        val zoomW = maxWDp / fitW
        val zoomH = maxHDp / fitH
        // Fill means the LARGER ratio: one side already fits exactly,
        // the other side's zoom is what eats the bars (crops the long
        // side of the painting instead).
        val fillZoom = (if (zoomW > zoomH) zoomW else zoomH).coerceAtMost(2.0f)
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
        // ONE container holds the painting AND the buttons, sized and
        // placed by the same rect — so on any screen, at any zoom, the
        // buttons cannot drift off their things: they live in the
        // painting's own coordinate system. (Before, the painting sat in
        // its own full-screen layer and the buttons were placed
        // separately; where the fill-zoom truly engaged, like the Razr,
        // the two could disagree and the icons floated off their spots.)
        val drawnWpx = with(LocalDensity.current) { drawnW.roundToPx() }
        // requiredSize, NOT size (found 2026-11-10, from May's Razr
        // screenshot): size() gets coerced down to the window when the
        // zoomed painting is bigger than it, while the offset still
        // assumes the full drawn rect — so the painting stopped short
        // of the bottom by half the overflow (2.44% on the Razr, ~9%
        // on an S25+), leaving a black strip no window flag could fix,
        // because it was never the window. requiredSize keeps the
        // container at the true drawn size so it overflows evenly and
        // covers the screen edge to edge, top AND bottom.
        Box(
            Modifier
                .offset(x = offX, y = offY)
                .requiredSize(width = drawnW, height = drawnH)
                .graphicsLayer {
                    scaleX = roomZoom.value
                    scaleY = roomZoom.value
                    transformOrigin = zoomOrigin
                }
        ) {
            Crossfade(targetState = roomBitmap, animationSpec = tween(850), label = "roomArt") { bmp ->
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Arnold's room",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
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
            if (fxOn && (phase == DayPhase.DAY || phase == DayPhase.NIGHT)) SkyEffects(night = phase == DayPhase.NIGHT, tall = tallMode, t = skyT)
            if (!dimmed) activeSpots.forEachIndexed { i, spot ->
                Box(
                    Modifier.offset(
                        x = drawnW * spot.fx - if (tallMode) 26.dp else 34.dp,
                        y = drawnH * spot.fy - if (tallMode) 26.dp else 34.dp
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
        }
        // Idle dim (May, 2026-11-10): a soft black veil fades in
        // over the whole room; the pop-up players are composed
        // outside this screen, so they stay lit on top of it.
        val dimAlpha by animateFloatAsState(
            targetValue = if (dimmed) 0.62f else 0f,
            animationSpec = tween(700), label = "idleDim"
        )
        if (dimAlpha > 0.01f) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = dimAlpha)))
        }
        if (!dimmed) {
        Row(
            Modifier.align(Alignment.BottomStart).padding(start = 14.dp, bottom = bottomEdgePad),
            verticalAlignment = Alignment.Bottom
        ) {
            // The AM/PM renders smaller than the digits — at full size
            // it looked bigger than the time itself (May, 2026-11-10).
            val timeParts = clockText.split(" ")
            Text(
                timeParts.getOrElse(0) { clockText },
                fontFamily = HandwrittenFont,
                color = Color(0xFFFFF3E0).copy(alpha = 0.9f),
                fontSize = 20.sp
            )
            if (timeParts.size > 1) {
                Spacer(Modifier.width(5.dp))
                Text(
                    timeParts[1],
                    fontFamily = HandwrittenFont,
                    color = Color(0xFFFFF3E0).copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }
            if (batteryPct >= 0) {
                Spacer(Modifier.width(9.dp))
                Text(
                    "$batteryPct%",
                    fontFamily = HandwrittenFont,
                    color = Color(0xFFFFF3E0).copy(alpha = 0.9f),
                    fontSize = 17.sp
                )
            }
        }
        }
        if (!dimmed) {
        Text(
            "Series created by Craig Bartlett\nApp created by May Marquardt",
            fontFamily = HandwrittenFont,
            color = Color(0xFFFFF3E0).copy(alpha = 0.9f),
            fontSize = 15.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = bottomEdgePad)
        )
        }
        // The hint composes after the clock and the credits so it
        // draws ON TOP of everything else in the room (May,
        // 2026-11-10): where the bottom texts meet it, they were
        // covering its edges and fighting its words.
        AnimatedVisibility(
            visible = hintVisible && !dimmed,
            exit = fadeOut(tween(500)),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = bottomTextPad + 2.dp)
        ) {
            Text(
                "Tap something in the room",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF10131A))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
        if (!dimmed) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .alpha(if (doorsOpen) 1f else 0f)
                .combinedClickable(onClick = onToggleMusic),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (musicCrop != null) Color.Transparent else Color(0xCC10131A))
                    .border(1.8.dp, RoomYellow, CircleShape),
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
                        modifier = Modifier.size(18.dp)
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
                    .background(Color(0x8810131A))
                    .padding(horizontal = if (tallMode) 6.dp else 5.dp, vertical = if (tallMode) 1.dp else 0.dp)
            )
        }
        }
        if (!dimmed) {
        // Effects toggle (May, 2026-11-10): sits right beside the music
        // button; one tap stills the sky — stars and lens flare both
        // gone — and a second tap brings them back.
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 64.dp, top = 12.dp)
                .alpha(if (doorsOpen) 1f else 0f)
                .combinedClickable(onClick = onToggleFx),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (musicCrop != null) Color.Transparent else Color(0xCC10131A))
                    .border(1.8.dp, RoomYellow, CircleShape),
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
                Crossfade(targetState = fxOn, animationSpec = tween(300), label = "fxIcon") { on ->
                    Icon(
                        if (on) Icons.Filled.Star else Icons.Filled.StarBorder,
                        if (on) "Turn effects off" else "Turn effects on",
                        tint = if (on) RoomYellow else RoomTextDim,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                if (fxOn) "Effects" else "No FX",
                color = Color.White,
                fontSize = if (tallMode) 12.5.sp else 11.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0x8810131A))
                    .padding(horizontal = if (tallMode) 6.dp else 5.dp, vertical = if (tallMode) 1.dp else 0.dp)
            )
        }
        }
        if (!dimmed) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .alpha(if (doorsOpen) 1f else 0f)
                .combinedClickable(onClick = onCyclePhase),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(38.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(if (dayNightCrop != null) Color.Transparent else Color(0xCC10131A))
                    .border(1.8.dp, Color(0xFFFF8F00), CircleShape),
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
                Crossfade(targetState = phase, animationSpec = tween(300), label = "dayNightIcon") { ph ->
                    Icon(
                        when (ph) {
                            DayPhase.DAWN -> Icons.Filled.WbTwilight
                            DayPhase.DAY -> Icons.Filled.LightMode
                            DayPhase.DUSK -> Icons.Filled.WbTwilight
                            DayPhase.NIGHT -> Icons.Filled.DarkMode
                        },
                        "Time of day",
                        tint = RoomYellow, modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                when (phase) {
                    DayPhase.DAWN -> "Dawn"
                    DayPhase.DAY -> "Day"
                    DayPhase.DUSK -> "Dusk"
                    DayPhase.NIGHT -> "Night"
                },
                color = Color.White,
                fontSize = if (tallMode) 12.5.sp else 11.5.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0x8810131A))
                    .padding(horizontal = if (tallMode) 6.dp else 5.dp, vertical = if (tallMode) 1.dp else 0.dp)
            )
        }
        }
    }
}

// ---------- Shared bits ----------

// The room, dimmed, behind every list screen (May, 2026-11-10,
// cohesion round three): the painting itself — in whichever of
// the four lights the room is in — glows faintly through a dark
// scrim, so walking into a list feels like stepping a few feet
// deeper into the same room rather than leaving it. The players
// keep their own black worlds; the lists stay fully readable.
@Composable
fun RoomBackdrop() {
    val bmp = rememberRoomBitmapForWindow(RoomNightState.phase)
    Box(Modifier.fillMaxSize()) {
        Crossfade(targetState = bmp, animationSpec = tween(850), label = "backdropArt") { b ->
            if (b != null) {
                Image(b.asImageBitmap(), null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Box(Modifier.fillMaxSize())
            }
        }
        // 0.88 buried the painting so completely the menus felt
        // like a different app (continuity pass, May, 2026-11-10).
        // At 0.72 the room reads through everything — the cards on
        // top are opaque, so nothing gets harder to read.
        Box(Modifier.fillMaxSize().background(RoomDark.copy(alpha = 0.72f)))
    }
}

// The way home (May, 2026-11-10): now that the back button and
// the header arrow step back one level at a time, every menu
// carries a clear X in a white ring, labeled "Room" — one tap,
// straight back to the main part of the app, however deep she is.
@Composable
fun RoomXButton(onRoom: () -> Unit) {
    // A generous target, held off the very corner (May, 2026-11-10:
    // tucked into the corner, she couldn't reliably tap it) — the
    // whole little column is the button, label and all.
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(start = 8.dp, end = 6.dp).clickable { onRoom() }
    ) {
        Box(
            Modifier.size(40.dp).shadow(3.dp, CircleShape).clip(CircleShape)
                .background(Color(0xCC10131A)).border(1.8.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Close, "Back to the room", tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Text("Room", color = Color.White, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
    }
}

// The room's clock follows her into the menus (continuity pass,
// May, 2026-11-10): the same handwritten Caveat time as the room's
// own corner, ticking on the same 15 seconds, sitting in every
// menu's banner — the room's moment is never left behind.
@Composable
fun HeaderClock() {
    var text by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val fmt = java.text.SimpleDateFormat("h:mm a", java.util.Locale.US)
        while (true) {
            text = fmt.format(java.util.Date())
            delay(15_000)
        }
    }
    if (text.isNotEmpty()) {
        Text(
            text,
            fontFamily = HandwrittenFont,
            color = Color(0xFFFFF3E0).copy(alpha = 0.9f),
            fontSize = 17.sp,
            modifier = Modifier.padding(end = 2.dp)
        )
    }
}

@Composable
fun ScreenHeader(title: String, onRoom: () -> Unit, onBack: () -> Unit, accent: Color = RoomYellow, right: @Composable () -> Unit = {}) {
    // Every list screen now opens with a slice of the room itself:
    // the painting as a banner (day/night by the same clock as the
    // room), an ink line and a colored line under it in the two-tone
    // style of the room's rings — so leaving the room doesn't leave
    // its world behind (the cohesion pass, 2026-11-10).
    val bmp = rememberRoomBitmapForWindow(RoomNightState.phase)
    Column(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(62.dp)) {
            if (bmp != null) {
                Image(bmp.asImageBitmap(), null, contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(), alignment = Alignment.TopCenter)
            }
            Box(Modifier.fillMaxSize().background(
                Brush.horizontalGradient(listOf(Color(0xE810131A), Color(0xA610131A)))
            ))
            Row(
                Modifier.fillMaxSize().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White) }
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                right()
                HeaderClock()
                RoomXButton(onRoom)
            }
        }
        Box(Modifier.fillMaxWidth().height(3.dp).background(Color(0xFF10131A)))
        Box(Modifier.fillMaxWidth().height(2.dp).background(accent))
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

// Section heading in the room's own grammar (May, 2026-11-10,
// cohesion round two): a bold title over the same two-tone rule
// the screen banners wear — dark ink line, accent line under it.
@Composable
fun SectionTitle(text: String, accent: Color, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Box(Modifier.width(54.dp).height(3.dp).background(Color(0xFF10131A)))
        Box(Modifier.width(54.dp).height(2.dp).background(accent))
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
        VideoThumb(v.uri, Modifier.width(104.dp).aspectRatio(16f / 9f).clip(RoundedCornerShape(8.dp)).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(8.dp)))
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
    onRoom: () -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("The Shelves — Categories", onRoom, onBack, accent = Color(0xFF7C4DFF))
        LazyColumn(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp))
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
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp))
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
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp))
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
    onRoom: () -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("TV Shows — Folders & Seasons", onRoom, onBack, accent = Color(0xFF7C4DFF))
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
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp))
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
    onRoom: () -> Unit,
    onBack: () -> Unit
) {
    val next = nextEpisode(show, isWatched, savedPos)
    val nextSeason = next?.let { n -> show.seasons.toSortedMap().entries.firstOrNull { n in it.value } }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader(show.title, onRoom, onBack)
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
                    SectionTitle(
                        "Season $season", Color(0xFF7C4DFF),
                        Modifier.padding(start = 16.dp, top = 14.dp, bottom = 6.dp)
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
fun MovieFoldersScreen(folders: List<MovieFolder>, onPick: (String) -> Unit, onRoom: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Movies — by Folder", onRoom, onBack, accent = Color(0xFFFFC107))
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
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp))
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
    onBack: () -> Unit,
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
        ScreenHeader(title, onRoom, onBack, accent = when (title) {
            Filter.MOVIES.label -> Color(0xFFFFC107)
            Filter.TV.label -> Color(0xFF7C4DFF)
            Filter.FAVORITES.label -> Color(0xFFF50057)
            Filter.CONTINUE.label -> Color(0xFF00C853)
            Filter.RECENT.label -> Color(0xFF00ACC1)
            Filter.UNSORTED.label -> Color(0xFF90A4AE)
            else -> Color(0xFFFF5722)
        }) {
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
                    if (title == Filter.CONTINUE.label) "Nothing half-watched right now.\nStart something from the shelves and it'll wait for you here, on the bed."
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
                            VideoThumb(sel.uri, Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(12.dp)))
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
    onRoom: () -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var version by remember { mutableStateOf(0) }
    var assignTarget by remember { mutableStateOf<VideoItem?>(null) }
    val results = remember(items, query, version) {
        if (query.isBlank()) items else items.filter { it.title.contains(query, ignoreCase = true) }
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Search", onRoom, onBack, accent = Color(0xFF2979FF))
        OutlinedTextField(
            value = query, onValueChange = { query = it },
            placeholder = { Text("Type a title…", color = RoomTextDim) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2979FF),
                unfocusedBorderColor = Color(0xFF39424E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF2979FF),
                focusedContainerColor = RoomCard,
                unfocusedContainerColor = RoomCard
            ),
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
            .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp))
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
    onRoom: () -> Unit,
    onBack: () -> Unit
) {
    val finished = videos.count { isWatched(it) }
    val inProgress = videos.filter { savedPos(it) > 0L }
        .sortedByDescending { savedPos(it) }
    val timeMs = videos.sumOf { v -> if (isWatched(v)) v.durationMs else savedPos(v) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Your watching", onRoom, onBack, accent = Color(0xFF00ACC1))
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
                    SectionTitle(
                        "Still in the middle of", Color(0xFF00ACC1),
                        Modifier.padding(top = 6.dp)
                    )
                }
                items(inProgress) { v ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .background(RoomCard).border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(10.dp)).padding(horizontal = 12.dp, vertical = 9.dp),
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
    onRoom: () -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Settings", onRoom, onBack, accent = Color(0xFFFF5252))
        LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
                        .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp)).padding(14.dp)
                ) {
                    Text("$videoCount videos found on this phone.", color = Color.White, fontSize = 16.sp)
                }
            }
            if (!granted) item {
                Button(onClick = onGrant, colors = ButtonDefaults.buttonColors(containerColor = RoomOrange)) {
                    Text("Allow access to my videos")
                }
            }
            item {
                TextButton(onClick = onClearResume) { Text("Forget all resume positions", color = RoomSky) }
            }
            item {
              Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
                    .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp)).padding(14.dp)
              ) {
                Text(
                    "The room map — knock twice to come in:\n" +
                    "• The TV at the back — Hey Arnold: the story of the show this room comes from\n" +
                    "• Up on the left wall — Plaza: the Nightwave Plaza radio\n" +
                    "• Shelves — categories: every video list lives behind them\n" +
                    "• Bed — continue watching, right where you left off\n" +
                    "• Window — search\n" +
                    "• Fish tank — favorites\n" +
                    "• Remote on the rug — shuffle a random video\n" +
                    "• Joystick on the rug — your music: songs, albums, artists, favorites, and playlists you make yourself\n" +
                    "• Door — settings (you're here)\n\n" +
                    "How the sound works: the room song is the app's ambience. The music button in the room hushes it. It plays in the room and follows you through the lists and settings, and steps aside only when you play something — a song, a video, or the radio. Whatever you played last wins. Videos remember where you left off; songs always start from the beginning, the way you asked.\n\n" +
                    "Every list wears a strip of the room's painting at the top, in the same day or night as the room. The button in the room's top corner steps through dawn, day, dusk, and night — tap it to live in whichever light you like; left alone, the room follows the real clock. The star button beside it turns the twinkling stars and sun glare off or on.\n\n" +
                    "Long-press a video to file it, favorite it, or mark it watched. Long-press a song to add it to a playlist. The pop-up players live in the room: open a menu and they step aside, close it and they're back right where you left them. The TV reshapes itself to the video — old 4:3 shows get a 4:3 TV, wide movies get a wide one. On the Fold opened up, lists split into two panes so you can browse and preview at the same time.",
                    color = RoomTextDim, fontSize = 14.sp
                )
              }
            }
        }
    }
}

// ---------- Hey Arnold! — the learning page ----------
// May (2026-11-10): the old All ring came back as "Hey Arnold",
// sitting where All used to sit (on the TV), and it opens this:
// facts about the show, its creator, its people, its movies.
@Composable
fun AboutCard(title: String, body: String) {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(RoomCard)
            .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp)).padding(14.dp)
    ) {
        Column {
            Text(title, color = Color(0xFFFFC107), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(body, color = RoomTextDim, fontSize = 14.sp)
        }
    }
}

@Composable
fun AboutScreen(onRoom: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Hey Arnold!", onRoom, onBack, accent = Color(0xFFFFC107))
        LazyColumn(
            Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AboutCard(
                    "Move it, football head!",
                    "Hey Arnold! is the Nickelodeon cartoon this whole app lives inside. Arnold is a fourth grader with a football-shaped head and a huge heart, growing up in the city of Hillwood with his grandparents and a boarding house full of characters. Nearly every episode, somebody in the neighborhood has a problem — and Arnold is the kid who stops, listens, and helps them through it."
                )
            }
            item {
                AboutCard(
                    "The show",
                    "Created by Craig Bartlett, Hey Arnold! premiered on Nickelodeon on October 7, 1996, and ran for five seasons and 100 episodes, ending in 2004. It stood out from other cartoons of its day: real city streets, kids with real problems — money worries, divorced parents, being the new kid — handled gently, with a jazzy score by Jim Lang playing underneath it all."
                )
            }
            item {
                AboutCard(
                    "The creator",
                    "Craig Bartlett started Arnold long before the series: in the late 1980s he made Arnold claymation shorts (one of them turned up on Sesame Street), and a version of Arnold appeared in a 1996 short shown in theaters. Bartlett was also a story editor on Rugrats, and went on to create Dinosaur Train and Ready Jet Go! for PBS. The name on the room's credit line — \"Series created by Craig Bartlett\" — is his."
                )
            }
            item {
                AboutCard(
                    "The gang",
                    "Helga G. Pataki talks tough and bullies Arnold in public — and secretly adores him, keeping a locket with his picture and a closet shrine at home. Gerald Johanssen is Arnold's best friend and the keeper of Hillwood's urban legends. Grandpa Phil and Grandma Gertie run the Sunset Arms boarding house, where the boarders — Ernie, Mr. Hyunh, and the ever-scheming Oskar Kokoshka — are family whether they like it or not."
                )
            }
            item {
                AboutCard(
                    "The voices",
                    "Because the show ran for years while its star was growing up, Arnold was voiced by a line of young actors: Toran Caudell first, then Phillip Van Dyke, Spencer Klein, and Alex D. Linz. Francesca Marie Smith voiced Helga for the whole run, and Jamil Walker Smith was Gerald. When Arnold returned in The Jungle Movie, Mason Vale Cotton took over the football head."
                )
            }
            item {
                AboutCard(
                    "The movies",
                    "Hey Arnold!: The Movie (2002) took the gang to the big screen to save their neighborhood from a developer named Scheck who wanted to bulldoze it. Fans then waited fifteen years for the ending the series never got: Hey Arnold!: The Jungle Movie (2017) finally sent Arnold to San Lorenzo to learn what really happened to his parents, Miles and Stella — the show's biggest mystery, answered at last."
                )
            }
            item {
                AboutCard(
                    "Did you know?",
                    "• Hillwood is a blend of the cities Bartlett knew: Seattle, Portland, and Brooklyn.\n• Helga's middle name is Geraldine — after Gerald.\n• Stoop Kid is afraid to leave his stoop.\n• Arnold's bedroom, with its skylight, remote-controlled everything, and couch made from a car seat, was voted by fans as the coolest cartoon bedroom ever — and it's the room on this app's home screen.\n• Arnold's last name was a mystery for the whole original run — The Jungle Movie finally confirmed it: Shortman, the very nickname Grandpa called him all along."
                )
            }
        }
    }
}


// ---------- Backup video engine (plays-anything fallback) ----------
// (May, 2026-11-10) ExoPlayer can't open some containers at all —
// .avi is the famous one, along with its DivX/Xvid/WMV-era
// cousins. When the main engine gives up on a file, this backup
// engine (the same core VLC is built on) takes that one file
// over; it plays practically anything. The library, the
// controls, the resume spots all stay exactly the same — only
// the surface and the transport calls branch.
private class VlcBackup(private val context: Context) {
    private var lib: org.videolan.libvlc.LibVLC? = null
    var player: org.videolan.libvlc.MediaPlayer? = null
        private set
    private var pendingSeekMs = 0L
    private var attachedView: android.view.TextureView? = null
    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
    var onEnded: (() -> Unit)? = null
    var onError: (() -> Unit)? = null
    var onAspect: ((Float) -> Unit)? = null
    var onPlayState: ((Boolean) -> Unit)? = null

    private val layoutListener = org.videolan.libvlc.interfaces.IVLCVout.OnNewVideoLayoutListener {
        _, _, _, visibleWidth, visibleHeight, sarNum, sarDen ->
        if (visibleWidth > 0 && visibleHeight > 0) {
            val sar = if (sarNum > 0 && sarDen > 0) sarNum.toFloat() / sarDen.toFloat() else 1f
            val a = visibleWidth * sar / visibleHeight
            mainHandler.post { onAspect?.invoke(a) }
        }
    }

    fun play(uri: Uri, startMs: Long, rate: Float) {
        try {
            stopPlayer()
            val l = lib ?: org.videolan.libvlc.LibVLC(
                context, arrayListOf("--no-video-title-show")
            ).also { lib = it }
            val p = org.videolan.libvlc.MediaPlayer(l)
            player = p
            pendingSeekMs = startMs
            p.setEventListener { ev ->
                when (ev.type) {
                    org.videolan.libvlc.MediaPlayer.Event.Playing -> {
                        if (pendingSeekMs > 0L) {
                            try { p.time = pendingSeekMs } catch (e: Exception) { }
                            pendingSeekMs = 0L
                        }
                        mainHandler.post { onPlayState?.invoke(true) }
                    }
                    org.videolan.libvlc.MediaPlayer.Event.Paused ->
                        mainHandler.post { onPlayState?.invoke(false) }
                    org.videolan.libvlc.MediaPlayer.Event.EndReached ->
                        mainHandler.post { onEnded?.invoke() }
                    org.videolan.libvlc.MediaPlayer.Event.EncounteredError ->
                        mainHandler.post { onError?.invoke() }
                }
            }
            val m = org.videolan.libvlc.Media(l, uri)
            p.media = m
            m.release()
            try { p.setRate(rate) } catch (e: Exception) { }
            attachedView?.let { attachToCurrent(it) }
            p.play()
        } catch (e: Exception) {
            mainHandler.post { onError?.invoke() }
        }
    }

    private fun attachToCurrent(tv: android.view.TextureView) {
        val p = player ?: return
        try {
            try { p.vlcVout.detachViews() } catch (e: Exception) { }
            p.vlcVout.setVideoView(tv)
            p.vlcVout.attachViews(layoutListener)
        } catch (e: Exception) { }
    }

    fun attach(tv: android.view.TextureView) {
        attachedView = tv
        attachToCurrent(tv)
    }

    fun detach() {
        attachedView = null
        try { player?.vlcVout?.detachViews() } catch (e: Exception) { }
    }

    fun stopPlayer() {
        try { player?.stop() } catch (e: Exception) { }
        try { player?.release() } catch (e: Exception) { }
        player = null
    }

    fun releaseAll() {
        stopPlayer()
        try { lib?.release() } catch (e: Exception) { }
        lib = null
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
    onShrink: (Int) -> Unit,
    radioPause: Boolean
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
    // Backup engine state (May, 2026-11-10): vlcMode means the
    // VLC-core backup currently owns the file on screen.
    var vlcMode by remember { mutableStateOf(false) }
    var vlcTriedFor by remember { mutableStateOf(-1) }
    val vlcBackup = remember { VlcBackup(context) }
    var isPlaying by remember { mutableStateOf(true) }
    // Takeovers (May, 2026-11-10): the radio or one of her songs
    // starting hushes this video — and when the takeover clears, a
    // video she tapped to play starts again on its own, so a tapped
    // video always auto-plays. A pause she makes herself (button or
    // sleep timer) clears `hushed`, cancelling the auto-resume.
    var hushed by remember { mutableStateOf(false) }
    LaunchedEffect(radioPause) {
        if (radioPause) {
            val playingNow = if (vlcMode) vlcBackup.player?.isPlaying == true else exo.isPlaying
            if (playingNow) {
                hushed = true
                try { if (vlcMode) vlcBackup.player?.pause() else exo.pause() } catch (e: Exception) { }
            }
        } else if (hushed) {
            hushed = false
            try { if (vlcMode) vlcBackup.player?.play() else exo.play() } catch (e: Exception) { }
        }
    }
    var position by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var index by remember { mutableStateOf(startIndex) }
    var speed by remember { mutableStateOf(1.0f) }
    var showControls by remember { mutableStateOf(true) }
    var seeking by remember { mutableStateOf(false) }
    var sliderPos by remember { mutableStateOf(0f) }
    var fullscreen by remember { mutableStateOf(false) }
    BackHandler(enabled = fullscreen) { fullscreen = false }
    var sleepChoice by remember { mutableStateOf(0) }
    var sleepLeftMs by remember { mutableStateOf(0L) }
    var hasCc by remember { mutableStateOf(false) }
    var ccOn by remember { mutableStateOf(true) }
    var marks by remember { mutableStateOf<List<Long>>(emptyList()) }
    var showMarks by remember { mutableStateOf(false) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var locked by remember { mutableStateOf(false) }
    var gestureMsg by remember { mutableStateOf<String?>(null) }
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    LaunchedEffect(gestureMsg) {
        if (gestureMsg != null) { delay(900); gestureMsg = null }
    }

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
            hushed = false
            try { if (vlcMode) vlcBackup.player?.pause() else exo.pause() } catch (e: Exception) { }
            sleepChoice = 0
            sleepLeftMs = 0L
        }
    }

    LaunchedEffect(ccOn) {
        exo.trackSelectionParameters = exo.trackSelectionParameters.buildUpon()
            .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !ccOn).build()
    }
    // Backup-engine plumbing (May, 2026-11-10): transport calls
    // that work whichever engine owns the file, a way back to the
    // main engine for the next file, and the backup's own events.
    fun playAny() {
        if (vlcMode) { try { vlcBackup.player?.play() } catch (e: Exception) { } }
        else { try { exo.play() } catch (e: Exception) { } }
    }
    fun pauseAny() {
        if (vlcMode) { try { vlcBackup.player?.pause() } catch (e: Exception) { } }
        else { try { exo.pause() } catch (e: Exception) { } }
    }
    fun seekAny(ms: Long) {
        if (vlcMode) { try { vlcBackup.player?.time = ms.coerceAtLeast(0L) } catch (e: Exception) { } }
        else { try { exo.seekTo(ms) } catch (e: Exception) { } }
    }
    fun stopVlc() {
        vlcBackup.stopPlayer()
        vlcMode = false
    }
    fun startVlcHere() {
        val v = queue.getOrNull(index) ?: return
        val startAt = try { exo.currentPosition } catch (e: Exception) { 0L }
        vlcMode = true
        hasCc = false
        playerError = null
        vlcBackup.play(v.uri, startAt, speed)
    }
    fun goToItem(i: Int) {
        if (i !in queue.indices) return
        if (vlcMode) stopVlc()
        index = i
        playerError = null
        try { exo.seekTo(i, 0L); exo.prepare(); exo.play() } catch (e: Exception) { }
    }
    DisposableEffect(vlcBackup) {
        vlcBackup.onEnded = {
            if (index < queue.lastIndex) {
                goToItem(index + 1)
            } else {
                queue.getOrNull(index)?.let { v ->
                    prefs.edit().remove("pos_${v.id}").putBoolean("watched_${v.id}", true).apply()
                }
                showControls = true
            }
        }
        vlcBackup.onError = {
            playerError = "This file wouldn't play — its format may not be supported."
            showControls = true
        }
        vlcBackup.onAspect = { a -> PipControl.aspect = a.coerceIn(0.42f, 2.39f) }
        onDispose { vlcBackup.releaseAll() }
    }
    LaunchedEffect(index) {
        loadMarks()
        showMarks = false
        playerError = null
    }

    // Picture-in-picture: while an episode is playing, leaving the app pops
    // the video out into a floating window on top of everything else.
    val pipAspect = PipControl.aspect
    LaunchedEffect(isPlaying, pipAspect) {
        PipControl.shouldEnter = isPlaying
        PipControl.playing = isPlaying
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.setPictureInPictureParams(buildPipParams(context, isPlaying))
        }
    }
    DisposableEffect(exo) {
        PipControl.togglePlay = { if (vlcMode) { val p = vlcBackup.player; if (p?.isPlaying == true) p.pause() else p?.play() } else if (exo.isPlaying) exo.pause() else exo.play() }
        onDispose { PipControl.togglePlay = null }
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
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.height > 0 && videoSize.width > 0) {
                    PipControl.aspect = (videoSize.width * videoSize.pixelWidthHeightRatio / videoSize.height)
                        .coerceIn(0.42f, 2.39f)
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                // Some files the main engine simply can't open
                // (.avi & friends) — hand those to the backup
                // engine once; only if it also fails is the file
                // truly unplayable here.
                if (!vlcMode && vlcTriedFor != index) {
                    vlcTriedFor = index
                    startVlcHere()
                } else {
                    playerError = "This file wouldn't play — its format may not be supported."
                    showControls = true
                }
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
            if (vlcMode) {
                val vp = vlcBackup.player
                if (vp != null) {
                    position = try { vp.time.coerceAtLeast(0L) } catch (e: Exception) { position }
                    duration = try { vp.length.coerceAtLeast(0L) } catch (e: Exception) { duration }
                    isPlaying = try { vp.isPlaying } catch (e: Exception) { isPlaying }
                }
            } else {
                position = exo.currentPosition.coerceAtLeast(0)
                duration = exo.duration.coerceAtLeast(0)
                isPlaying = exo.isPlaying
                index = exo.currentMediaItemIndex
                hasCc = exo.currentTracks.groups.any { it.type == C.TRACK_TYPE_TEXT }
            }
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
            // Gestures first-swipe even mid-video (May, 2026-11-10):
            // only the status bar hides; the nav bar stays up so
            // back/home gestures work without a reveal swipe first.
            controller.hide(WindowInsetsCompat.Type.statusBars())
            controller.show(WindowInsetsCompat.Type.navigationBars())
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            activity?.let { act ->
                act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                val c = WindowCompat.getInsetsController(act.window, act.window.decorView)
                c.hide(WindowInsetsCompat.Type.statusBars())
                c.show(WindowInsetsCompat.Type.navigationBars())
            }
        }
    }

    val current = queue.getOrNull(index)
    val playerBox: @Composable (Modifier) -> Unit = { mod ->
        Box(mod.background(Color.Black)) {
            if (vlcMode) {
                AndroidView(
                    factory = { ctx ->
                        android.view.TextureView(ctx).also { vlcBackup.attach(it) }
                    },
                    onRelease = { vlcBackup.detach() },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
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
            }
            // One combined gesture layer (May, 2026-11-10): taps and
            // vertical swipes used to live in two stacked layers, and
            // the swipe layer starved the taps — once the controls
            // faded, tapping couldn't bring them back. One handler now
            // sorts it out: quick tap = show/hide controls, quick
            // double-tap on a side = skip 10 seconds, vertical drag =
            // brightness (left half) / volume (right half).
            Box(
                Modifier.fillMaxSize().pointerInput(Unit) {
                    var lastTapMs = 0L
                    var lastTapLeft = false
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val left = down.position.x < size.width / 2
                        val dragVolume = !left
                        var startFraction = if (dragVolume) {
                            val maxV = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                            if (maxV > 0) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / maxV else 0f
                        } else {
                            val b = activity?.window?.attributes?.screenBrightness ?: -1f
                            if (b >= 0f) b else 0.5f
                        }
                        var dragging = false
                        var upMs = 0L
                        while (true) {
                            val ev = awaitPointerEvent()
                            val ch = ev.changes.firstOrNull() ?: break
                            if (ch.pressed) {
                                val dy = ch.position.y - down.position.y
                                val dx = ch.position.x - down.position.x
                                if (!dragging && kotlin.math.abs(dy) > viewConfiguration.touchSlop &&
                                    kotlin.math.abs(dy) > kotlin.math.abs(dx)
                                ) dragging = true
                                if (dragging && !locked) {
                                    val frac = (startFraction - dy / (size.height * 0.75f)).coerceIn(0f, 1f)
                                    if (dragVolume) {
                                        val maxV = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                        audioManager.setStreamVolume(
                                            AudioManager.STREAM_MUSIC,
                                            (frac * maxV).roundToInt().coerceIn(0, maxV), 0
                                        )
                                        gestureMsg = "Volume ${(frac * 100).roundToInt()}%"
                                    } else {
                                        activity?.let { act ->
                                            val attrs = act.window.attributes
                                            attrs.screenBrightness = frac.coerceIn(0.03f, 1f)
                                            act.window.attributes = attrs
                                        }
                                        gestureMsg = "Brightness ${(frac * 100).roundToInt()}%"
                                    }
                                    ch.consume()
                                }
                            } else {
                                upMs = ch.uptimeMillis
                                break
                            }
                        }
                        if (!dragging) {
                            if (upMs - lastTapMs in 1..320 && lastTapLeft == left) {
                                if (left) seekAny((position - 10_000).coerceAtLeast(0))
                                else seekAny(position + 10_000)
                                showControls = true
                                lastTapMs = 0L
                            } else {
                                showControls = !showControls
                                lastTapMs = upMs
                                lastTapLeft = left
                            }
                        }
                    }
                }
            )
            if (showControls && !locked && !PipControl.inPip) {
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
                            try { vlcBackup.player?.setRate(speed) } catch (e: Exception) { }
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
                            val t = position
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
                            savePos(index, position)
                            onShrink(index)
                        }) {
                            Icon(Icons.Filled.PictureInPictureAlt, "Mini player", tint = Color.White)
                        }
                        IconButton(onClick = { applyFullscreen(!fullscreen) }) {
                            Icon(if (fullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                "Fullscreen", tint = Color.White)
                        }
                        IconButton(onClick = { locked = true; showControls = false }) {
                            Icon(Icons.Filled.LockOpen, "Lock controls", tint = Color.White)
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
                    if (vlcMode && playerError == null) {
                        Text(
                            "Backup player — this file's format needs it", color = Color.White, fontSize = 12.sp,
                            modifier = Modifier.padding(start = 12.dp, top = 8.dp, end = 12.dp)
                                .clip(RoundedCornerShape(10.dp)).background(Color(0xB33A2A5A))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
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
                                            .combinedClickable(onClick = { seekAny(t); playAny() })
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
                        IconButton(onClick = { goToItem(index - 1) },
                            modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Filled.SkipPrevious, "Previous", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        IconButton(onClick = { seekAny((position - 10_000).coerceAtLeast(0)) },
                            modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Filled.Replay10, "Back 10 seconds", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        IconButton(
                            onClick = { hushed = false; if (isPlaying) pauseAny() else playAny() },
                            modifier = Modifier.size(68.dp).clip(CircleShape).background(RoomOrange)
                        ) {
                            Icon(if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                "Play or pause", tint = Color.White, modifier = Modifier.size(38.dp))
                        }
                        IconButton(onClick = { seekAny(position + 10_000) },
                            modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Filled.Forward10, "Forward 10 seconds", tint = Color.White, modifier = Modifier.size(30.dp))
                        }
                        IconButton(onClick = { goToItem(index + 1) },
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
                                if (duration > 0) seekAny((sliderPos * duration).toLong())
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
            if (gestureMsg != null) {
                Text(
                    gestureMsg!!, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 58.dp)
                        .clip(RoundedCornerShape(10.dp)).background(Color(0xB310131A))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
            val nextVideo = queue.getOrNull(index + 1)
            val remainingMs = duration - position
            if (!locked && nextVideo != null && duration > 0 && remainingMs in 1..15_000) {
                Row(
                    Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = 86.dp)
                        .clip(RoundedCornerShape(12.dp)).background(Color(0xE61D2130))
                        .clickable { goToItem(index + 1) }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Up next in ${remainingMs / 1000 + 1}s", color = RoomYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(nextVideo.title, color = Color.White, fontSize = 13.sp, maxLines = 1,
                            overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 230.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Icon(Icons.Filled.SkipNext, "Play next now", tint = RoomOrange, modifier = Modifier.size(26.dp))
                }
            }
            if (locked) {
                Box(Modifier.fillMaxSize()) {
                    IconButton(
                        onClick = { locked = false },
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                            .clip(CircleShape).background(Color(0x59000000))
                    ) {
                        Icon(Icons.Filled.Lock, "Unlock controls", tint = Color.White.copy(alpha = 0.9f))
                    }
                }
            }
        }
    }

    if (PipControl.inPip) {
        // In the floating window outside the app, ONLY the video
        // shows (May, 2026-11-10): the full player layout used to
        // come along — no pause button, and a slice of the
        // what's-next list bleeding into the window's bottom.
        playerBox(Modifier.fillMaxSize())
    } else if (fullscreen) {
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
                                        .combinedClickable(onClick = { goToItem(i) })
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
                                    .combinedClickable(onClick = { goToItem(i) })
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

// ---------- Nightwave Plaza pop-up: a small draggable player card ----------
// Moved by its title bar, just like the video pop-up. Closing it
// doesn't stop the stream — reopen it from the Plaza ring to stop.
@Composable
fun RadioPlayerBox(
    playing: Boolean,
    buffering: Boolean,
    onToggle: () -> Unit,
    onClose: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val maxWPx = with(density) { maxWidth.toPx() }
        val maxHPx = with(density) { maxHeight.toPx() }
        var widthDpF by RadioBoxState.widthDpF
        DisposableEffect(Unit) {
            PopRects.lastGood["radio"] = widthDpF
            onDispose { PopRects.radio = null }
        }
        val widthDp = widthDpF.dp
        val widthPx = with(density) { widthDp.toPx() }
        val heightPx = widthPx * (268f / 292f) + with(density) { 30.dp.toPx() }
        var offX by RadioBoxState.offX
        var offY by RadioBoxState.offY
        val x = (offX ?: (maxWPx - widthPx) / 2f).coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
        val y = (offY ?: (maxHPx - heightPx - with(density) { 118.dp.toPx() }))
            .coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))
        Column(
            Modifier
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .width(widthDp)
                .onGloballyPositioned { c ->
                    val r = Rect(c.positionInRoot(), ComposeSize(c.size.width.toFloat(), c.size.height.toFloat()))
                    PopRects.radio = r
                    popSizeGuard("radio", r, widthDpF) { widthDpF = it }
                    val n = popNudge(r, PopRects.others("radio"))
                    if (n != Offset.Zero) {
                        offX = (r.left + n.x).coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
                        offY = (r.top + n.y).coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))
                    }
                }
        ) {
            // Telescopic antenna, tipped up from the cabinet's corner.
            Canvas(
                Modifier.width(120.dp).height(26.dp).align(Alignment.End)
            ) {
                val ink = Color(0xFF10131A)
                val base = Offset(size.width * 0.06f, size.height)
                val mid = Offset(size.width * 0.52f, size.height * 0.45f)
                val tip = Offset(size.width * 0.96f, size.height * 0.08f)
                drawLine(ink, base, mid, strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                drawLine(ink, mid, tip, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                drawCircle(ink, radius = 3.5.dp.toPx(), center = mid)
                drawCircle(Color(0xFFFF6EC7), radius = 4.dp.toPx(), center = tip)
            }
            // The cabinet (May, 2026-11-10): the stream player is a
            // real little radio now — brand plate, tuning dial,
            // speaker grille, knobs — the way the video pop-up is a TV.
            Column(
                Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF2A1245))
                    .border(3.5.dp, Color(0xFF10131A), RoundedCornerShape(18.dp))
                    .padding(12.dp)
                    // Pinch to resize (May, 2026-11-10).
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            if (zoom != 1f) widthDpF = (widthDpF * zoom).coerceIn(150f, 480f)
                        }
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().pointerInput(Unit) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            val curX = offX ?: (maxWPx - widthPx) / 2f
                            val curY = offY ?: (maxHPx - heightPx - with(density) { 118.dp.toPx() })
                            val d = PopRects.radio?.let { slideDelta(it, drag.x, drag.y, PopRects.others("radio")) } ?: Offset(drag.x, drag.y)
                            offX = (curX + d.x).coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
                            offY = (curY + d.y).coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))
                        }
                    }
                ) {
                    Box(
                        Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFF3E0))
                            .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("NIGHTWAVE PLAZA", color = Color(0xFF10131A), fontWeight = FontWeight.Bold, fontSize = 12.sp,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.width(6.dp))
                    Box(
                        Modifier.size(9.dp).clip(CircleShape)
                            .background(if (playing) Color(0xFFFF5252) else Color(0xFF5A2A35))
                    )
                    if (widthDpF >= 200f) {
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "ON AIR",
                            color = if (playing) Color(0xFFFF8A80) else Color(0xFF8E7CC3),
                            fontSize = 9.5.sp, fontWeight = FontWeight.Bold
                        )
                    }
Icon(
                        Icons.Filled.OpenInFull, "Resize player", tint = Color(0xFFFF6EC7),
                        modifier = Modifier.size(19.dp).pointerInput(Unit) {
                            coroutineScope {
                                launch {
                                detectTapGestures {
                                    widthDpF = when {
                                        widthDpF < 200f -> 240f
                                        widthDpF < 280f -> 320f
                                        widthDpF < 380f -> 420f
                                        else -> 180f
                                    }
                                }
                            }
                            launch {
                                detectDragGestures { change, drag ->
                                    change.consume()
                                    widthDpF = (widthDpF + drag.x / density.density).coerceIn(150f, 480f)
                                }
                                }
                            }
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.Close, "Close radio player", tint = Color(0xFFB39DDB),
                        modifier = Modifier.size(18.dp).clickable { onClose() }
                    )
                }
                Spacer(Modifier.height(10.dp))
                // The tuning dial: cream glass, FM numbers, one pink needle.
                Box(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFF3E0))
                        .border(2.dp, Color(0xFF10131A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("88", "92", "96", "100", "104", "108").forEach {
                                Text(it, color = Color(0xFF10131A), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Box(Modifier.fillMaxWidth().height(14.dp)) {
                            Box(
                                Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center)
                                    .background(Color(0xFF10131A).copy(alpha = 0.35f))
                            )
                            Box(
                                Modifier.fillMaxHeight().width(3.dp)
                                    .align(Alignment.CenterStart)
                                    .offset(x = 148.dp)
                                    .background(Color(0xFFFF6EC7))
                            )
                        }
                        Text(
                            "FM · plaza.one — vaporwave, all day",
                            color = Color(0xFF10131A).copy(alpha = 0.7f), fontSize = 9.sp
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Speaker grille.
                    Box(
                        Modifier.size(72.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF1B1035))
                            .border(2.dp, Color(0xFF10131A), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            repeat(5) {
                                Box(
                                    Modifier.width(3.dp).height(48.dp).clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFFFF6EC7).copy(alpha = 0.55f))
                                )
                            }
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFFFF3E0))
                                .border(2.dp, Color(0xFF10131A), CircleShape),
                            contentAlignment = Alignment.TopCenter
                        ) { Box(Modifier.width(3.dp).height(10.dp).background(Color(0xFF10131A))) }
                        Text("VOL", color = Color(0xFFB39DDB), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFFFF3E0))
                                .border(2.dp, Color(0xFF10131A), CircleShape),
                            contentAlignment = Alignment.TopCenter
                        ) { Box(Modifier.width(3.dp).height(10.dp).background(Color(0xFF10131A))) }
                        Text("TUNE", color = Color(0xFFB39DDB), fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        when {
                            buffering -> "Connecting…"
                            playing -> "LIVE"
                            else -> "Off"
                        },
                        color = if (playing) Color(0xFFFF6EC7) else Color(0xFFB39DDB),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(12.dp))
                // One chunky power key, like pressing a real button.
                Row(
                    Modifier.fillMaxWidth().height(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFF6EC7))
                        .border(2.5.dp, Color(0xFF10131A), RoundedCornerShape(10.dp))
                        .clickable { onToggle() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        if (playing) "Stop the stream" else "Play the stream",
                        tint = Color(0xFF10131A), modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (playing) "STOP" else "PLAY", color = Color(0xFF10131A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
    onClose: () -> Unit,
    radioPause: Boolean
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
    // Backup engine (May, 2026-11-10): same arrangement as the
    // full player — a file ExoPlayer can't open (.avi & friends)
    // is handed to the VLC-core backup on this TV too.
    var vlcMode by remember { mutableStateOf(false) }
    val vlcBackup = remember { VlcBackup(context) }
    fun startVlcHereMini() {
        val v = queue.getOrNull(index) ?: return
        val startAt = try { exo.currentPosition } catch (e: Exception) { 0L }
        vlcMode = true
        vlcBackup.play(v.uri, startAt, 1.0f)
    }
    fun miniToggle() {
        if (vlcMode) {
            val p = vlcBackup.player
            if (p?.isPlaying == true) p.pause() else p?.play()
        } else if (exo.isPlaying) exo.pause() else exo.play()
    }
    fun miniGo(i: Int) {
        if (i !in queue.indices) return
        index = i
        if (vlcMode) {
            vlcBackup.play(queue[i].uri, 0L, 1.0f)
        } else {
            try { exo.seekTo(i, 0L); exo.play() } catch (e: Exception) { }
        }
    }
    var isPlaying by remember { mutableStateOf(true) }
    // Same takeover as the full player, two-way like it: hushed by
    // the radio/a song, auto-resumes when the takeover clears,
    // unless she paused it herself.
    var hushed by remember { mutableStateOf(false) }
    LaunchedEffect(radioPause) {
        if (radioPause) {
            val playingNow = if (vlcMode) vlcBackup.player?.isPlaying == true else exo.isPlaying
            if (playingNow) {
                hushed = true
                try { if (vlcMode) vlcBackup.player?.pause() else exo.pause() } catch (e: Exception) { }
            }
        } else if (hushed) {
            hushed = false
            try { if (vlcMode) vlcBackup.player?.play() else exo.play() } catch (e: Exception) { }
        }
    }
    // The pop-up TV takes the video's own shape (May, 2026-11-10):
    // a 4:3 episode gets a 4:3 TV, a 16:9 movie a wide one.
    var videoAspect by MiniTvState.aspect
    DisposableEffect(vlcBackup) {
        vlcBackup.onEnded = {
            if (index < queue.lastIndex) miniGo(index + 1) else isPlaying = false
        }
        vlcBackup.onError = { isPlaying = false }
        vlcBackup.onAspect = { a -> videoAspect = a.coerceIn(0.5f, 2.39f) }
        vlcBackup.onPlayState = { playingNow -> isPlaying = playingNow }
        onDispose { vlcBackup.releaseAll() }
    }
    // The shrink-in entrance plays on a genuinely new open only;
    // a TV restored from a menu trip is simply back (May,
    // 2026-11-10 — it kept coming back from Settings smaller).
    val appear = remember { Animatable(if (MiniTvState.everOpened.value) 1f else 0f) }
    LaunchedEffect(Unit) {
        if (!MiniTvState.everOpened.value) {
            MiniTvState.everOpened.value = true
            appear.animateTo(1f, spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMedium))
        }
    }

    fun saveNow() {
        val v = queue.getOrNull(if (vlcMode) index else exo.currentMediaItemIndex) ?: return
        val pos = if (vlcMode) (try { vlcBackup.player?.time ?: 0L } catch (e: Exception) { 0L }) else exo.currentPosition
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
            override fun onPlayerError(error: PlaybackException) {
                if (!vlcMode) startVlcHereMini()
            }
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.height > 0 && videoSize.width > 0) {
                    videoAspect = (videoSize.width * videoSize.pixelWidthHeightRatio / videoSize.height)
                        .coerceIn(0.5f, 2.39f)
                }
            }
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
        var widthDp by MiniTvState.widthDp
        DisposableEffect(Unit) {
            // Returning is not resizing (May, 2026-11-10): seed the
            // collision guard with the restored width, so a stale
            // smaller size from earlier can't snap the TV back
            // down when it reappears next to its neighbors.
            PopRects.lastGood["tv"] = widthDp.value
            onDispose { PopRects.tv = null }
        }
        var offX by MiniTvState.offX
        var offY by MiniTvState.offY
        val widthPx = with(density) { widthDp.toPx() }
        val heightPx = widthPx / videoAspect + with(density) { 98.dp.toPx() }
        val x = (offX ?: (maxWPx - widthPx - with(density) { 12.dp.toPx() }))
            .coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
        val y = (offY ?: with(density) { 96.dp.toPx() })
            .coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))

        Column(
            Modifier
                .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                .width(widthDp)
                .onGloballyPositioned { c ->
                    val r = Rect(c.positionInRoot(), ComposeSize(c.size.width.toFloat(), c.size.height.toFloat()))
                    PopRects.tv = r
                    popSizeGuard("tv", r, widthDp.value) { widthDp = it.dp }
                    val n = popNudge(r, PopRects.others("tv"))
                    if (n != Offset.Zero) {
                        offX = (r.left + n.x).coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
                        offY = (r.top + n.y).coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))
                    }
                }
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
                Modifier.fillMaxWidth().aspectRatio(videoAspect)
                    .clip(RoundedCornerShape(8.dp)).background(Color.Black)
                    .border(3.5.dp, Color(0xFF10131A), RoundedCornerShape(8.dp))
            ) {
                if (vlcMode) {
                    AndroidView(
                        factory = { ctx ->
                            android.view.TextureView(ctx).also { vlcBackup.attach(it) }
                        },
                        onRelease = { vlcBackup.detach() },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
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
                }
                Box(
                    Modifier.fillMaxSize().pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { hushed = false; miniToggle() }
                        )
                    }
                )
                Box(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(3.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xB310131A))
                        .border(1.5.dp, Color(0xFF10131A), CircleShape)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                // Tap the corner arrows to step the TV
                                // through sizes (May, 2026-11-10 — the
                                // arrows "did nothing" because they only
                                // ever dragged).
                                val steps = listOf(180.dp, 240.dp, 300.dp, 360.dp)
                                widthDp = steps.firstOrNull { it > widthDp + 12.dp } ?: 180.dp
                            }
                        }
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
                        Icons.Filled.OpenInFull, "Resize — tap for the next size, or drag",
                        tint = Color.White, modifier = Modifier.size(15.dp)
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
                            val curY = offY ?: with(density) { 96.dp.toPx() }
                            val d = PopRects.tv?.let { slideDelta(it, drag.x, drag.y, PopRects.others("tv")) } ?: Offset(drag.x, drag.y)
                            offX = (curX + d.x)
                                .coerceIn(0f, (maxWPx - widthPx).coerceAtLeast(0f))
                            offY = (curY + d.y)
                                .coerceIn(0f, (maxHPx - heightPx).coerceAtLeast(0f))
                        }
                    }
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.SkipPrevious, "Previous", tint = Color.White,
                    modifier = Modifier.size(18.dp).combinedClickable(
                        onClick = { hushed = false; miniGo(index - 1) }
                    )
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    "Play or pause", tint = RoomYellow,
                    modifier = Modifier.size(18.dp).combinedClickable(
                        onClick = { hushed = false; miniToggle() }
                    )
                )
                Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.Filled.SkipNext, "Next", tint = Color.White,
                    modifier = Modifier.size(18.dp).combinedClickable(
                        onClick = { hushed = false; miniGo(index + 1) }
                    )
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Filled.Fullscreen, "Full screen", tint = Color.White,
                    modifier = Modifier.size(18.dp).combinedClickable(
                        onClick = { saveNow(); onExpand(if (vlcMode) index else exo.currentMediaItemIndex) }
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


// ---------- Her music: library, full player, pop-up mini player ----------
// (May, 2026-11-10) The joystick ring on the rug opens a music
// player for the audio files on her phone, built to the video
// player's standard: search, favorites, shuffle, repeat, speed,
// sleep timer, resume positions, favorite-moment bookmarks — plus a
// draggable pop-up player, and background play through MusicService.

// The music player's color (May, 2026-11-10): soft violet — the
// pop-up wore it first, then she had the whole player match it.
private val MusicAccent = Color(0xFFB388FF)

// Playlists live in prefs (May, 2026-11-10 — created and filled
// from inside the player itself): "mpl_names" holds the names
// joined by "|||", and each "mpl_list_<name>" holds that list's
// song ids, comma-joined.
private fun playlistNames(prefs: SharedPreferences): List<String> =
    (prefs.getString("mpl_names", "") ?: "").split("|||").filter { it.isNotBlank() }

private fun playlistIds(prefs: SharedPreferences, name: String): List<Long> =
    (prefs.getString("mpl_list_$name", "") ?: "").split(",").mapNotNull { it.toLongOrNull() }

private fun writePlaylist(prefs: SharedPreferences, name: String, ids: List<Long>) {
    val names = playlistNames(prefs)
    val ed = prefs.edit()
    if (name !in names) ed.putString("mpl_names", (names + name).joinToString("|||"))
    ed.putString("mpl_list_$name", ids.joinToString(","))
    ed.apply()
}

private fun deletePlaylist(prefs: SharedPreferences, name: String) {
    val names = playlistNames(prefs).filter { it != name }
    prefs.edit()
        .putString("mpl_names", names.joinToString("|||"))
        .remove("mpl_list_$name")
        .apply()
}

private fun addToPlaylist(prefs: SharedPreferences, name: String, songId: Long) {
    val ids = playlistIds(prefs, name)
    if (songId !in ids) writePlaylist(prefs, name, ids + songId)
}

@Composable
private fun PlaylistChooserDialog(
    prefs: SharedPreferences,
    songTitle: String,
    onPick: (String) -> Unit,
    onNew: () -> Unit,
    onDismiss: () -> Unit
) {
    val names = playlistNames(prefs)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RoomCard,
        title = { Text("Add to playlist", color = Color.White) },
        text = {
            Column {
                Text(songTitle, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(10.dp))
                if (names.isEmpty()) {
                    Text("No playlists yet — make the first one.", color = RoomTextDim)
                }
                names.forEach { n ->
                    Row(
                        Modifier.fillMaxWidth().clickable { onPick(n) }.padding(vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PlaylistPlay, null, tint = MusicAccent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(9.dp))
                        Text(n, color = Color.White, fontSize = 15.5.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        Text("${playlistIds(prefs, n).size}", color = RoomTextDim, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(6.dp))
                TextButton(onClick = onNew) {
                    Icon(Icons.Filled.Add, null, tint = MusicAccent, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("New playlist…", color = MusicAccent, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = { },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun NewPlaylistDialog(
    onCreate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RoomCard,
        title = { Text("New playlist", color = Color.White) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Playlist name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onCreate(name.trim()) },
                enabled = name.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SongRow(
    s: SongItem,
    prefs: SharedPreferences,
    onPlay: () -> Unit,
    onToggleFav: () -> Unit,
    onLongPress: () -> Unit = { },
    onRemove: (() -> Unit)? = null,
    trackLabel: String? = null
) {
    val fav = prefs.getBoolean("music_fav_${s.id}", false)
    val isCurrent = MusicState.hasMedia && MusicState.current?.id == s.id
    Row(
        Modifier.fillMaxWidth()
            .combinedClickable(onClick = onPlay, onLongClick = onLongPress)
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(42.dp).clip(CircleShape)
                .background(if (isCurrent) MusicAccent.copy(alpha = 0.2f) else Color(0xFF232A33))
                .border(1.5.dp, if (isCurrent) MusicAccent else Color(0xFF39424E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (trackLabel != null) {
                Text(
                    trackLabel,
                    color = if (isCurrent) MusicAccent else RoomTextDim,
                    fontSize = if (trackLabel.length > 2) 10.5.sp else 14.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Icon(
                    Icons.Filled.MusicNote, null,
                    tint = if (isCurrent) MusicAccent else RoomTextDim,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(
                s.title, color = if (isCurrent) MusicAccent else Color.White,
                fontSize = 15.5.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Text(
                "${s.artist} • ${fmtDur(s.durationMs)}",
                color = RoomTextDim, fontSize = 12.5.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        if (onRemove != null) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Close, "Remove from playlist", tint = RoomTextDim)
            }
        } else {
            IconButton(onClick = onToggleFav) {
                Icon(
                    if (fav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    if (fav) "Unfavorite" else "Favorite",
                    tint = if (fav) Color(0xFFF50057) else RoomTextDim
                )
            }
        }
    }
}

@Composable
fun MusicLibraryScreen(
    prefs: SharedPreferences,
    onPlaySong: (List<SongItem>, Int) -> Unit,
    onOpenPlayer: () -> Unit,
    onRoom: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var audioGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, audioPermission()) == PackageManager.PERMISSION_GRANTED
        )
    }
    var songs by remember { mutableStateOf<List<SongItem>>(emptyList()) }
    var query by MusicLibState.query
    var tab by MusicLibState.tab // 0 Songs, 1 Albums, 2 Artists, 3 Favorites, 4 Playlists
    var albumPick by MusicLibState.albumPick
    var artistPick by MusicLibState.artistPick
    var playlistPick by MusicLibState.playlistPick
    var favTick by remember { mutableStateOf(0) }
    var addToSong by remember { mutableStateOf<SongItem?>(null) }
    var showNewPlaylist by remember { mutableStateOf(false) }
    var pendingAdd by remember { mutableStateOf<SongItem?>(null) }
    var deleteAsk by remember { mutableStateOf<String?>(null) }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        audioGranted = ok
    }
    // Subscribes this screen to prefs-side changes (hearts, playlist
    // edits): those live in prefs, not state, so the tick is what
    // recomposes the lists.
    val favTickSnapshot = favTick
    LaunchedEffect(audioGranted) {
        if (audioGranted) songs = withContext(Dispatchers.IO) { loadSongs(context.contentResolver) }
    }
    // Back steps out one level at a time (May, 2026-11-10): an
    // album opened from inside an artist returns to that artist's
    // album list, not all the way out to the tabs.
    fun stepBackOneLevel() {
        if (albumPick != null && artistPick != null) {
            albumPick = null
        } else {
            albumPick = null
            artistPick = null
            playlistPick = null
        }
    }
    BackHandler(enabled = albumPick != null || artistPick != null || playlistPick != null) {
        stepBackOneLevel()
    }
    fun isFav(s: SongItem) = prefs.getBoolean("music_fav_${s.id}", false)
    fun matches(s: SongItem) =
        query.isBlank() || s.title.contains(query, true) || s.artist.contains(query, true) ||
            s.album.contains(query, true)
    val albums = songs.groupBy { it.album }.entries.sortedBy { it.key.lowercase() }
    val artists = songs.groupBy { it.artist }.entries.sortedBy { it.key.lowercase() }
    val inDetail = albumPick != null || artistPick != null || playlistPick != null
    BackHandler(enabled = inDetail) {
        stepBackOneLevel()
    }

    if (addToSong != null) {
        val target = addToSong!!
        PlaylistChooserDialog(
            prefs = prefs,
            songTitle = target.title,
            onPick = { name ->
                addToPlaylist(prefs, name, target.id)
                addToSong = null
                favTick++
            },
            onNew = {
                pendingAdd = target
                addToSong = null
                showNewPlaylist = true
            },
            onDismiss = { addToSong = null }
        )
    }
    if (showNewPlaylist) {
        NewPlaylistDialog(
            onCreate = { name ->
                writePlaylist(prefs, name, emptyList())
                pendingAdd?.let { addToPlaylist(prefs, name, it.id) }
                pendingAdd = null
                showNewPlaylist = false
                favTick++
            },
            onDismiss = {
                showNewPlaylist = false
                pendingAdd = null
            }
        )
    }
    if (deleteAsk != null) {
        val name = deleteAsk!!
        AlertDialog(
            onDismissRequest = { deleteAsk = null },
            containerColor = RoomCard,
            title = { Text("Delete playlist?", color = Color.White) },
            text = { Text("\"$name\" will be removed. The songs themselves stay on your phone.", color = RoomTextDim) },
            confirmButton = {
                TextButton(onClick = {
                    deletePlaylist(prefs, name)
                    if (playlistPick == name) playlistPick = null
                    deleteAsk = null
                    favTick++
                }) { Text("Delete", color = Color(0xFFFF5252)) }
            },
            dismissButton = { TextButton(onClick = { deleteAsk = null }) { Text("Cancel") } }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        val bannerM = rememberRoomBitmapForWindow(RoomNightState.phase)
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(14.dp))
        ) {
            if (bannerM != null) {
                Image(bannerM.asImageBitmap(), null, contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(), alignment = Alignment.TopCenter)
                Box(Modifier.matchParentSize().background(Color(0xB310131A)))
            }
            Row(
                Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
            IconButton(onClick = {
                if (inDetail) stepBackOneLevel() else onBack()
            }) {
                Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text(
                when {
                    albumPick != null -> albumPick!!
                    artistPick != null -> artistPick!!
                    playlistPick != null -> playlistPick!!
                    else -> "Music"
                },
                color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (!inDetail) {
                Text(
                    "${songs.size} songs • ${albums.size} albums • ${artists.size} artists",
                    color = Color(0xFFC9D4E3), fontSize = 12.sp
                )
            }
            RoomXButton(onRoom)
            }
        }
        if (!audioGranted) {
            Spacer(Modifier.height(24.dp))
            Text(
                "The music player needs permission to see the audio files on your phone.",
                color = RoomTextDim, fontSize = 15.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = { permLauncher.launch(audioPermission()) }) { Text("Allow music access") }
            return@Column
        }
        if (MusicState.hasMedia && !inDetail) {
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(MusicAccent.copy(alpha = 0.12f))
                    .border(1.5.dp, MusicAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .clickable { onOpenPlayer() }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.MusicNote, null, tint = MusicAccent, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(9.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        MusicState.current?.title ?: "Now playing",
                        color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (MusicState.playing) "Playing — tap to open the player" else "Paused — tap to open the player",
                        color = RoomTextDim, fontSize = 12.sp
                    )
                }
                IconButton(onClick = { musicAction(context, MusicService.ACTION_TOGGLE) }) {
                    Icon(
                        if (MusicState.playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        if (MusicState.playing) "Pause" else "Play",
                        tint = MusicAccent
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search songs, albums, artists", color = RoomTextDim) },
            leadingIcon = { Icon(Icons.Filled.Search, null, tint = MusicAccent) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MusicAccent,
                unfocusedBorderColor = Color(0xFF39424E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = MusicAccent,
                focusedContainerColor = Color(0xFF232A33),
                unfocusedContainerColor = Color(0xFF232A33)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        if (artistPick != null && albumPick == null) {
            // An artist opens onto their albums first (May,
            // 2026-11-10): pick the record, then its songs — in
            // track order, numbers showing. Play and Shuffle here
            // still cover everything the artist has.
            val artistSongs = songs.filter { it.artist == artistPick }
            val artistAlbums = artistSongs.groupBy { it.album }.entries.sortedBy { it.key.lowercase() }
            val playOrder = artistSongs.sortedWith(compareBy<SongItem> { it.album.lowercase() }.then(byTrackOrder()))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { if (playOrder.isNotEmpty()) onPlaySong(playOrder, 0) },
                    colors = ButtonDefaults.buttonColors(containerColor = MusicAccent, contentColor = Color(0xFF10131A))
                ) {
                    Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Play", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { if (artistSongs.isNotEmpty()) onPlaySong(artistSongs.shuffled(), 0) },
                    colors = ButtonDefaults.buttonColors(containerColor = MusicAccent, contentColor = Color(0xFF10131A))
                ) {
                    Icon(Icons.Filled.Shuffle, null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Shuffle", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Text("${artistAlbums.size} albums \u2022 ${artistSongs.size} songs", color = RoomTextDim, fontSize = 13.sp)
            }
            Spacer(Modifier.height(6.dp))
            LazyColumn(Modifier.fillMaxSize(), state = MusicLibState.artistAlbumsList) {
                items(artistAlbums, key = { it.key }) { (name, list) ->
                    Row(
                        Modifier.fillMaxWidth().clickable { albumPick = name }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(46.dp).clip(RoundedCornerShape(9.dp))
                                .background(Color(0xFF232A33))
                                .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Album, null, tint = MusicAccent, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(11.dp))
                        Column {
                            Text(
                                name, color = Color.White, fontSize = 15.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${list.size} songs",
                                color = RoomTextDim, fontSize = 12.5.sp,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            return@Column
        }
        if (inDetail) {
            val rawDetail = when {
                albumPick != null -> songs.filter { it.album == albumPick }
                artistPick != null -> songs.filter { it.artist == artistPick }
                else -> playlistIds(prefs, playlistPick!!).mapNotNull { id -> songs.firstOrNull { it.id == id } }
            }
            // Inside an album the songs run in track order and
            // carry their numbers (May, 2026-11-10); playlists
            // keep the order she built them in.
            val detailSongs = (if (albumPick != null) rawDetail.sortedWith(byTrackOrder()) else rawDetail)
                .filter { matches(it) }
            val multiDisc = rawDetail.map { trackDisc(it.track) }.distinct().size > 1
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { if (detailSongs.isNotEmpty()) onPlaySong(detailSongs, 0) },
                    colors = ButtonDefaults.buttonColors(containerColor = MusicAccent, contentColor = Color(0xFF10131A))
                ) {
                    Icon(Icons.Filled.PlayArrow, null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Play", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = { if (detailSongs.isNotEmpty()) onPlaySong(detailSongs.shuffled(), 0) },
                    colors = ButtonDefaults.buttonColors(containerColor = MusicAccent, contentColor = Color(0xFF10131A))
                ) {
                    Icon(Icons.Filled.Shuffle, null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Shuffle", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Text("${detailSongs.size} songs", color = RoomTextDim, fontSize = 13.sp)
                if (playlistPick != null) {
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { deleteAsk = playlistPick }) {
                        Icon(Icons.Filled.Delete, "Delete playlist", tint = RoomTextDim)
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
            if (playlistPick != null && detailSongs.isEmpty()) {
                Text(
                    "This playlist is empty. Long-press any song anywhere in the library to add it here.",
                    color = RoomTextDim, fontSize = 14.sp
                )
            }
            LazyColumn(Modifier.fillMaxSize(), state = MusicLibState.detailList) {
                items(detailSongs, key = { it.id }) { s ->
                    SongRow(
                        s = s, prefs = prefs,
                        onPlay = {
                            val idx = detailSongs.indexOf(s)
                            if (idx >= 0) onPlaySong(detailSongs, idx)
                        },
                        onToggleFav = {
                            prefs.edit().putBoolean("music_fav_${s.id}", !isFav(s)).apply()
                            favTick++
                        },
                        onLongPress = { addToSong = s },
                        onRemove = if (playlistPick != null) {
                            {
                                val name = playlistPick!!
                                writePlaylist(prefs, name, playlistIds(prefs, name).filter { it != s.id })
                                favTick++
                            }
                        } else null,
                        trackLabel = if (albumPick != null) trackLabel(s.track, multiDisc) else null
                    )
                }
            }
            return@Column
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { if (songs.isNotEmpty()) onPlaySong(songs.shuffled(), 0) },
                colors = ButtonDefaults.buttonColors(containerColor = MusicAccent, contentColor = Color(0xFF10131A))
            ) {
                Icon(Icons.Filled.Shuffle, null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                Text("Shuffle all", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Songs", "Albums", "Artists", "Favorites", "Playlists").forEachIndexed { i, label ->
                Box(
                    Modifier.clip(RoundedCornerShape(50))
                        .background(if (tab == i) MusicAccent else Color(0xFF232A33))
                        .border(1.5.dp, if (tab == i) Color(0xFF10131A) else Color(0xFF39424E), RoundedCornerShape(50))
                        .clickable { tab = i }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        label,
                        color = if (tab == i) Color(0xFF10131A) else RoomTextDim,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp
                    )
                }
            }
        }
        if (songs.isEmpty()) {
            Text("No music files found on this phone yet.", color = RoomTextDim, fontSize = 15.sp)
        }
        when (tab) {
            0, 3 -> {
                val shown = songs.filter { s -> (tab == 0 || isFav(s)) && matches(s) }
                LazyColumn(Modifier.fillMaxSize(), state = MusicLibState.songsList) {
                    items(shown, key = { it.id }) { s ->
                        SongRow(
                            s = s, prefs = prefs,
                            onPlay = {
                                val idx = shown.indexOf(s)
                                if (idx >= 0) onPlaySong(shown, idx)
                            },
                            onToggleFav = {
                                prefs.edit().putBoolean("music_fav_${s.id}", !isFav(s)).apply()
                                favTick++
                            },
                            onLongPress = { addToSong = s }
                        )
                    }
                }
            }
            1 -> {
                val shownAlbums = albums.filter { (name, list) ->
                    query.isBlank() || name.contains(query, true) || list.any { matches(it) }
                }
                LazyColumn(Modifier.fillMaxSize(), state = MusicLibState.albumsList) {
                    items(shownAlbums, key = { it.key }) { (name, list) ->
                        Row(
                            Modifier.fillMaxWidth().clickable { albumPick = name }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(46.dp).clip(RoundedCornerShape(9.dp))
                                    .background(Color(0xFF232A33))
                                    .border(1.5.dp, Color(0xFF10131A), RoundedCornerShape(9.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Album, null, tint = MusicAccent, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(11.dp))
                            Column {
                                Text(
                                    name, color = Color.White, fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${list.firstOrNull()?.artist ?: ""} • ${list.size} songs",
                                    color = RoomTextDim, fontSize = 12.5.sp,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            2 -> {
                val shownArtists = artists.filter { (name, _) ->
                    query.isBlank() || name.contains(query, true)
                }
                LazyColumn(Modifier.fillMaxSize(), state = MusicLibState.artistsList) {
                    items(shownArtists, key = { it.key }) { (name, list) ->
                        Row(
                            Modifier.fillMaxWidth().clickable { artistPick = name }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(46.dp).clip(CircleShape)
                                    .background(Color(0xFF232A33))
                                    .border(1.5.dp, Color(0xFF10131A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Person, null, tint = MusicAccent, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(11.dp))
                            Column {
                                Text(
                                    name, color = Color.White, fontSize = 15.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${list.size} songs • ${list.map { it.album }.distinct().size} albums",
                                    color = RoomTextDim, fontSize = 12.5.sp
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                val names = playlistNames(prefs)
                Column(Modifier.fillMaxSize()) {
                    Button(
                        onClick = { showNewPlaylist = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MusicAccent, contentColor = Color(0xFF10131A))
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("New playlist", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    if (names.isEmpty()) {
                        Text(
                            "No playlists yet. Make one, then long-press songs to fill it.",
                            color = RoomTextDim, fontSize = 14.sp
                        )
                    }
                    LazyColumn(Modifier.fillMaxSize(), state = MusicLibState.playlistsList) {
                        items(names, key = { it }) { name ->
                            Row(
                                Modifier.fillMaxWidth().clickable { playlistPick = name }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(46.dp).clip(RoundedCornerShape(9.dp))
                                        .background(Color(0xFF232A33))
                                        .border(1.5.dp, Color(0xFF39424E), RoundedCornerShape(9.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.PlaylistPlay, null, tint = MusicAccent, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(11.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        name, color = Color.White, fontSize = 15.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "${playlistIds(prefs, name).size} songs",
                                        color = RoomTextDim, fontSize = 12.5.sp
                                    )
                                }
                                IconButton(onClick = { deleteAsk = name }) {
                                    Icon(Icons.Filled.Delete, "Delete playlist", tint = RoomTextDim)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MusicPlayerScreen(
    prefs: SharedPreferences,
    onBack: () -> Unit,
    onShrink: () -> Unit
) {
    val context = LocalContext.current
    val song = MusicState.current
    var scrub by remember { mutableStateOf<Float?>(null) }
    var markTick by remember { mutableStateOf(0) }
    // Subscribes this screen to bookmark changes (they live in prefs).
    val markTickSnapshot = markTick
    var showAddTo by remember { mutableStateOf(false) }
    var showNewList by remember { mutableStateOf(false) }
    if (showAddTo && song != null) {
        PlaylistChooserDialog(
            prefs = prefs,
            songTitle = song.title,
            onPick = { name ->
                addToPlaylist(prefs, name, song.id)
                showAddTo = false
            },
            onNew = {
                showAddTo = false
                showNewList = true
            },
            onDismiss = { showAddTo = false }
        )
    }
    if (showNewList && song != null) {
        NewPlaylistDialog(
            onCreate = { name ->
                writePlaylist(prefs, name, listOf(song.id))
                showNewList = false
            },
            onDismiss = { showNewList = false }
        )
    }
    Column(
        Modifier.fillMaxSize().background(RoomDark).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "Back to the music library", tint = RoomTextDim)
            }
            Text("Now Playing", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onShrink) {
                Icon(Icons.Filled.PictureInPictureAlt, "Pop-up player", tint = MusicAccent)
            }
        }
        if (song == null) {
            Spacer(Modifier.height(40.dp))
            Text("Nothing playing — pick a song from the library.", color = RoomTextDim, fontSize = 15.sp)
            return@Column
        }
        Spacer(Modifier.weight(1f))
        // The disc turns as the song plays (a half-turn every six
        // seconds) and freezes when paused — its angle is the play
        // position itself, so no separate animation state can drift.
        val angle = (MusicState.positionMs / 1000f * 30f) % 360f
        Box(
            Modifier.size(238.dp).shadow(12.dp, CircleShape).clip(CircleShape)
                .background(Color(0xFF1A2118))
                .border(3.dp, MusicAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier.size(196.dp).clip(CircleShape)
                    .border(1.5.dp, MusicAccent.copy(alpha = 0.35f), CircleShape)
            )
            Icon(
                Icons.Filled.MusicNote, null, tint = MusicAccent,
                modifier = Modifier.size(106.dp).rotate(angle)
            )
            Box(
                Modifier.size(30.dp).clip(CircleShape).background(RoomDark)
                    .border(2.dp, MusicAccent, CircleShape)
            )
        }
        Spacer(Modifier.height(22.dp))
        Text(
            song.title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold,
            maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center
        )
        Text(song.artist, color = RoomTextDim, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(8.dp))
        val dur = if (MusicState.durationMs > 0) MusicState.durationMs else song.durationMs
        Slider(
            value = scrub ?: MusicState.positionMs.toFloat(),
            onValueChange = { scrub = it },
            onValueChangeFinished = {
                scrub?.let { musicAction(context, MusicService.ACTION_SEEK) { putExtra(MusicService.EXTRA_POS, it.toLong()) } }
                scrub = null
            },
            valueRange = 0f..(dur.toFloat().coerceAtLeast(1f)),
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth()) {
            Text(fmtDur((scrub?.toLong()) ?: MusicState.positionMs), color = RoomTextDim, fontSize = 13.sp)
            Spacer(Modifier.weight(1f))
            Text(fmtDur(dur), color = RoomTextDim, fontSize = 13.sp)
        }
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                musicAction(context, MusicService.ACTION_SET_SHUFFLE) {
                    putExtra(MusicService.EXTRA_BOOL, !MusicState.shuffle)
                }
            }) {
                Icon(
                    Icons.Filled.Shuffle, "Shuffle",
                    tint = if (MusicState.shuffle) MusicAccent else RoomTextDim
                )
            }
            IconButton(onClick = { musicAction(context, MusicService.ACTION_PREV) }) {
                Icon(Icons.Filled.SkipPrevious, "Previous", tint = Color.White, modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = {
                val p = (MusicState.positionMs - 10_000).coerceAtLeast(0L)
                musicAction(context, MusicService.ACTION_SEEK) { putExtra(MusicService.EXTRA_POS, p) }
            }) {
                Icon(Icons.Filled.Replay10, "Back 10 seconds", tint = Color.White)
            }
            Box(
                Modifier.size(66.dp).shadow(6.dp, CircleShape).clip(CircleShape)
                    .background(MusicAccent)
                    .clickable { musicAction(context, MusicService.ACTION_TOGGLE) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (MusicState.playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    if (MusicState.playing) "Pause" else "Play",
                    tint = Color(0xFF10131A), modifier = Modifier.size(36.dp)
                )
            }
            IconButton(onClick = {
                val p = MusicState.positionMs + 10_000
                musicAction(context, MusicService.ACTION_SEEK) { putExtra(MusicService.EXTRA_POS, p) }
            }) {
                Icon(Icons.Filled.Forward10, "Forward 10 seconds", tint = Color.White)
            }
            IconButton(onClick = { musicAction(context, MusicService.ACTION_NEXT) }) {
                Icon(Icons.Filled.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(32.dp))
            }
            IconButton(onClick = {
                val next = (MusicState.repeatMode + 1) % 3
                musicAction(context, MusicService.ACTION_SET_REPEAT) { putExtra(MusicService.EXTRA_INT, next) }
            }) {
                Icon(
                    if (MusicState.repeatMode == 2) Icons.Filled.RepeatOne else Icons.Filled.Repeat,
                    "Repeat",
                    tint = if (MusicState.repeatMode > 0) MusicAccent else RoomTextDim
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = {
                val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 2f)
                val next = speeds.firstOrNull { it > MusicState.speed } ?: speeds.first()
                musicAction(context, MusicService.ACTION_SET_SPEED) { putExtra(MusicService.EXTRA_FLOAT, next) }
            }) {
                Text("Speed ${MusicState.speed}×", color = RoomTextDim, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = {
                val opts = listOf(0, 15, 30, 45, 60)
                val next = opts.firstOrNull { it > MusicState.sleepMinutes } ?: 0
                musicAction(context, MusicService.ACTION_SET_SLEEP) { putExtra(MusicService.EXTRA_INT, next) }
            }) {
                Icon(Icons.Filled.Bedtime, null, tint = RoomTextDim, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(5.dp))
                Text(
                    if (MusicState.sleepMinutes > 0) "Sleep ${MusicState.sleepMinutes}m" else "Sleep off",
                    color = RoomTextDim, fontWeight = FontWeight.Bold
                )
            }
            TextButton(onClick = {
                val key = "mmarks_${song.id}"
                val cur = prefs.getString(key, "")?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
                val updated = (cur + MusicState.positionMs).distinct().sorted()
                prefs.edit().putString(key, updated.joinToString(",")).apply()
                markTick++
            }) {
                Icon(Icons.Filled.BookmarkAdd, null, tint = RoomTextDim, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(5.dp))
                Text("Mark this spot", color = RoomTextDim, fontWeight = FontWeight.Bold)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { showAddTo = true }) {
                Icon(Icons.Filled.PlaylistAdd, null, tint = RoomTextDim, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(5.dp))
                Text("Add to playlist", color = RoomTextDim, fontWeight = FontWeight.Bold)
            }
        }
        val hasMarks = (prefs.getString("mmarks_${song.id}", "") ?: "").isNotBlank()
        if (!hasMarks) {
            // Most songs have no favorite moments, so nothing
            // fills the bottom of the screen — let the empty
            // space sit under the controls instead of everything
            // crowding into the top half (May, 2026-11-10).
            Spacer(Modifier.weight(1f))
        }
        val marks = prefs.getString("mmarks_${song.id}", "")?.split(",")
            ?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        if (marks.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text("Favorite moments", color = MusicAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
                items(marks, key = { it }) { m ->
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            musicAction(context, MusicService.ACTION_SEEK) { putExtra(MusicService.EXTRA_POS, m) }
                            if (!MusicState.playing) musicAction(context, MusicService.ACTION_TOGGLE)
                        }.padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Bookmarks, null, tint = MusicAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(9.dp))
                        Text(fmtDur(m), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = {
                            val key = "mmarks_${song.id}"
                            val updated = marks.filter { it != m }
                            prefs.edit().putString(key, updated.joinToString(",")).apply()
                            markTick++
                        }) {
                            Icon(Icons.Filled.Delete, "Delete mark", tint = RoomTextDim)
                        }
                    }
                }
            }
        }
    }
}

// One reel of the cassette pop-up: a cream spool with three
// spokes, turned by the song.
@Composable
private fun CassetteReel(angle: Float, modifier: Modifier = Modifier) {
    Canvas(modifier.graphicsLayer { rotationZ = angle }) {
        val ink = Color(0xFF10131A)
        drawCircle(Color(0xFFFFF3E0), radius = size.minDimension / 2f)
        drawCircle(ink, radius = size.minDimension / 2f, style = Stroke(width = 2.5.dp.toPx()))
        for (k in 0..2) {
            val a = Math.toRadians((k * 120).toDouble())
            drawLine(
                ink,
                Offset(center.x, center.y),
                Offset(
                    center.x + (size.minDimension * 0.38f) * Math.cos(a).toFloat(),
                    center.y + (size.minDimension * 0.38f) * Math.sin(a).toFloat()
                ),
                strokeWidth = 3.5.dp.toPx(), cap = StrokeCap.Round
            )
        }
        drawCircle(ink, radius = 4.dp.toPx(), center = center)
    }
}

@Composable
fun MusicMiniBox(
    onExpand: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val song = MusicState.current ?: return
    var offX by MusicBoxState.offX
    var offY by MusicBoxState.offY
    var widthDpF by MusicBoxState.widthDpF
    DisposableEffect(Unit) {
        PopRects.lastGood["music"] = widthDpF
        onDispose { PopRects.music = null }
    }
    // The reels turn while the song plays and stop with it
    // (May, 2026-11-10: the music pop-up is a cassette player).
    var spin by remember { mutableStateOf(0f) }
    LaunchedEffect(MusicState.playing) {
        while (MusicState.playing) {
            spin = (spin + 4f) % 360f
            delay(33)
        }
    }
    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier.align(Alignment.BottomEnd)
                .padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = maxOf(14.dp, navBarInsetDp() + 6.dp))
                .offset { IntOffset(offX.toInt(), offY.toInt()) }
                .width(widthDpF.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1D1430))
                .border(2.5.dp, Color(0xFF10131A), RoundedCornerShape(16.dp))
                .onGloballyPositioned { c ->
                    val r = Rect(c.positionInRoot(), ComposeSize(c.size.width.toFloat(), c.size.height.toFloat()))
                    PopRects.music = r
                    popSizeGuard("music", r, widthDpF) { widthDpF = it }
                    val n = popNudge(r, PopRects.others("music"))
                    if (n != Offset.Zero) { offX += n.x; offY += n.y }
                }
                .padding(12.dp)
                // Pinch to resize (May, 2026-11-10).
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        if (zoom != 1f) widthDpF = (widthDpF * zoom).coerceIn(150f, 480f)
                    }
                }
        ) {
            Row(
                Modifier.fillMaxWidth().pointerInput(Unit) {
                    detectDragGestures { change, drag ->
                        change.consume()
                        val d = PopRects.music?.let { slideDelta(it, drag.x, drag.y, PopRects.others("music")) } ?: Offset(drag.x, drag.y)
                        offX += d.x
                        offY += d.y
                    }
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MUSIC · SIDE A", color = MusicAccent, fontSize = 10.5.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(
                    Icons.Filled.OpenInFull, "Resize player", tint = MusicAccent,
                    modifier = Modifier.size(19.dp).pointerInput(Unit) {
                        coroutineScope {
                            launch {
                            detectTapGestures {
                                widthDpF = when {
                                    widthDpF < 200f -> 240f
                                    widthDpF < 280f -> 320f
                                    widthDpF < 380f -> 420f
                                    else -> 180f
                                }
                            }
                        }
                        launch {
                            detectDragGestures { change, drag ->
                                change.consume()
                                widthDpF = (widthDpF + drag.x / density).coerceIn(150f, 480f)
                            }
                            }
                        }
                    }
                )
                    Spacer(Modifier.width(10.dp))
                Icon(
                    Icons.Filled.Close, "Hide pop-up player", tint = RoomTextDim,
                    modifier = Modifier.size(18.dp).clickable { onClose() }
                )
            }
            Spacer(Modifier.height(8.dp))
            // The cassette itself: her handwriting on the label,
            // the reels showing through the window.
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF120B1E))
                    .border(2.dp, Color(0xFF10131A), RoundedCornerShape(12.dp))
                    .clickable { onExpand() }
                    .padding(10.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(Color(0xFFFFF3E0))
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Text(
                        song.title, fontFamily = HandwrittenFont, color = Color(0xFF10131A),
                        fontSize = 19.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        song.artist, fontFamily = HandwrittenFont, color = Color(0xFF10131A).copy(alpha = 0.7f),
                        fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(9.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CassetteReel(spin, Modifier.size(52.dp))
                    Box(Modifier.width(56.dp).height(7.dp).background(Color(0xFF4A2C14)))
                    CassetteReel(spin * 0.8f, Modifier.size(52.dp))
                }
            }
            Spacer(Modifier.height(9.dp))
            val dur = if (MusicState.durationMs > 0) MusicState.durationMs else song.durationMs
            LinearProgressIndicator(
                progress = if (dur > 0) (MusicState.positionMs.toFloat() / dur).coerceIn(0f, 1f) else 0f,
                color = MusicAccent,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth()) {
                Text(fmtDur(MusicState.positionMs), color = RoomTextDim, fontSize = 11.5.sp)
                Spacer(Modifier.weight(1f))
                Text(fmtDur(dur), color = RoomTextDim, fontSize = 11.5.sp)
            }
            Spacer(Modifier.height(8.dp))
            // Chunky Walkman keys.
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2E2140))
                        .border(2.dp, Color(0xFF10131A), RoundedCornerShape(8.dp))
                        .clickable { musicAction(context, MusicService.ACTION_PREV) },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.SkipPrevious, "Previous", tint = Color.White, modifier = Modifier.size(21.dp)) }
                Box(
                    Modifier.weight(1.3f).height(42.dp).clip(RoundedCornerShape(8.dp)).background(MusicAccent)
                        .border(2.dp, Color(0xFF10131A), RoundedCornerShape(8.dp))
                        .clickable { musicAction(context, MusicService.ACTION_TOGGLE) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (MusicState.playing) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        if (MusicState.playing) "Pause" else "Play",
                        tint = Color(0xFF10131A), modifier = Modifier.size(24.dp)
                    )
                }
                Box(
                    Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2E2140))
                        .border(2.dp, Color(0xFF10131A), RoundedCornerShape(8.dp))
                        .clickable { musicAction(context, MusicService.ACTION_NEXT) },
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Filled.SkipNext, "Next", tint = Color.White, modifier = Modifier.size(21.dp)) }
            }
        }
    }
}
