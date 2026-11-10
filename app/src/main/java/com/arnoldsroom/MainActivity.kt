package com.arnoldsroom

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// ---------- Theme colours pulled from the room ----------
val RoomDark = Color(0xFF12141F)
val RoomTeal = Color(0xFF57C4C7)
val RoomSky = Color(0xFF35B6E8)
val RoomOrange = Color(0xFFF26D21)
val RoomYellow = Color(0xFFFFC93C)
val RoomCard = Color(0xFF1D2130)
val RoomTextDim = Color(0xFFB9C2CC)

// ---------- Data ----------
data class VideoItem(
    val id: Long,
    val name: String,
    val title: String,
    val uri: Uri,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAddedSec: Long,
    val folder: String
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
    object Search : Screen()
    object Settings : Screen()
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
            out.add(
                VideoItem(
                    id = id,
                    name = name,
                    title = name.substringBeforeLast('.', name),
                    uri = Uri.withAppendedPath(collection, id.toString()),
                    durationMs = c.getLong(durI),
                    sizeBytes = c.getLong(sizeI),
                    dateAddedSec = c.getLong(dateI),
                    folder = folder
                )
            )
        }
    }
    return out
}

fun videoPermission(): String =
    if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO
    else Manifest.permission.READ_EXTERNAL_STORAGE

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ArnoldsRoomApp() }
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
                Lifecycle.Event.ON_START -> if (screen is Screen.Room) startMusic()
                Lifecycle.Event.ON_STOP -> pauseMusic()
                else -> { }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(screen) {
        if (screen is Screen.Room) {
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) startMusic()
        } else pauseMusic()
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
    fun isFav(v: VideoItem) = prefs.getBoolean("fav_${v.id}", false)
    fun savedPos(v: VideoItem) = prefs.getLong("pos_${v.id}", 0L)

    fun filtered(filter: Filter): List<VideoItem> = when (filter) {
        Filter.ALL -> videos
        Filter.MOVIES -> videos.filter { catOf(it) == AssignCat.MOVIE }
        Filter.TV -> videos.filter { catOf(it) == AssignCat.TV }
        Filter.FAVORITES -> videos.filter { isFav(it) }
        Filter.UNSORTED -> videos.filter { catOf(it) == AssignCat.NONE }
        Filter.RECENT -> videos.take(30)
        Filter.CONTINUE -> videos.filter {
            val p = savedPos(it); it.durationMs > 0 && p > 10_000 && p < it.durationMs * 95 / 100
        }
    }

    BackHandler(enabled = screen !is Screen.Room) {
        screen = if (screen is Screen.Player) Screen.Library(Filter.ALL) else Screen.Room
    }

    Box(Modifier.fillMaxSize().background(RoomDark)) {
        when (val s = screen) {
            is Screen.Room -> RoomScreen(
                night = night,
                onToggleNight = { nightOverride = !night },
                onHotspot = { dest -> go(dest) },
                onShuffle = {
                    pauseMusic()
                    if (!granted) {
                        pendingDest = null
                        launcher.launch(videoPermission())
                    } else if (videos.isNotEmpty()) {
                        val idx = videos.indices.random()
                        screen = Screen.Player(videos, idx, "Shuffle")
                    }
                }
            )
            is Screen.Categories -> CategoriesScreen(
                counts = Filter.values().associateWith { filtered(it).size },
                onPick = { screen = Screen.Library(it) },
                onRoom = { screen = Screen.Room }
            )
            is Screen.Library -> LibraryScreen(
                title = s.filter.label,
                items = filtered(s.filter),
                prefs = prefs,
                catOf = ::catOf,
                isFav = ::isFav,
                savedPos = ::savedPos,
                onPlay = { queue, idx -> screen = Screen.Player(queue, idx, s.filter.label) },
                onRoom = { screen = Screen.Room }
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
            is Screen.Player -> PlayerScreen(
                queue = s.queue,
                startIndex = s.startIndex,
                fromLabel = s.fromLabel,
                prefs = prefs,
                onBack = { screen = Screen.Library(Filter.ALL) }
            )
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
                tall && night -> listOf("arnolds_room_tall_night", "arnolds_room_tall", "file_00000000266481f58cd2d715ef42fabe", "arnolds_room_night", "arnolds_room")
                tall -> listOf("arnolds_room_tall", "file_00000000266481f58cd2d715ef42fabe", "arnolds_room")
                night -> listOf("arnolds_room_night", "arnolds_room")
                else -> listOf("arnolds_room")
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
    val fx: Float, val fy: Float, val label: String, val icon: ImageVector, val dest: Screen?
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoomScreen(onHotspot: (Screen) -> Unit, onShuffle: () -> Unit, night: Boolean, onToggleNight: () -> Unit) {
    val spots = listOf(
        Hotspot(0.50f, 0.10f, "New", Icons.Filled.History, Screen.Library(Filter.RECENT)),
        Hotspot(0.13f, 0.44f, "Search", Icons.Filled.Search, Screen.Search),
        Hotspot(0.47f, 0.36f, "Shelves", Icons.Filled.Folder, Screen.Categories),
        Hotspot(0.635f, 0.395f, "TV", Icons.Filled.Tv, Screen.Library(Filter.ALL)),
        Hotspot(0.55f, 0.53f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE)),
        Hotspot(0.075f, 0.66f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES)),
        Hotspot(0.685f, 0.685f, "Shuffle", Icons.Filled.Shuffle, null),
        Hotspot(0.915f, 0.47f, "Settings", Icons.Filled.Settings, Screen.Settings)
    )
    val tallSpots = listOf(
        Hotspot(0.50f, 0.07f, "New", Icons.Filled.History, Screen.Library(Filter.RECENT)),
        Hotspot(0.11f, 0.39f, "Search", Icons.Filled.Search, Screen.Search),
        Hotspot(0.47f, 0.33f, "Shelves", Icons.Filled.Folder, Screen.Categories),
        Hotspot(0.615f, 0.35f, "TV", Icons.Filled.Tv, Screen.Library(Filter.ALL)),
        Hotspot(0.56f, 0.46f, "Continue", Icons.Filled.PlayCircleFilled, Screen.Library(Filter.CONTINUE)),
        Hotspot(0.075f, 0.57f, "Favorites", Icons.Filled.Favorite, Screen.Library(Filter.FAVORITES)),
        Hotspot(0.66f, 0.51f, "Shuffle", Icons.Filled.Shuffle, null),
        Hotspot(0.90f, 0.40f, "Settings", Icons.Filled.Settings, Screen.Settings)
    )
    BoxWithConstraints(Modifier.fillMaxSize().background(Color.Black)) {
        val maxWDp = maxWidth
        val maxHDp = maxHeight
        val tallMode = maxWDp / maxHDp < 0.62f
        val activeSpots = if (tallMode) tallSpots else spots
        val roomBitmap = rememberRoomBitmap(tallMode, night)
        val aspect = roomBitmap?.let { it.width.toFloat() / it.height.toFloat() }
            ?: if (tallMode) 875f / 1798f else 1704f / 2048f
        var drawnW = maxWDp
        var drawnH = maxWDp / aspect
        if (drawnH > maxHDp) { drawnH = maxHDp; drawnW = maxHDp * aspect }
        val offX = (maxWDp - drawnW) / 2
        val offY = (maxHDp - drawnH) / 2
        if (roomBitmap != null) {
            Image(
                bitmap = roomBitmap.asImageBitmap(),
                contentDescription = "Arnold's room",
                contentScale = ContentScale.Fit,
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
        activeSpots.forEach { spot ->
            val cx = offX + drawnW * spot.fx
            val cy = offY + drawnH * spot.fy
            Column(
                modifier = Modifier
                    .offset(x = cx - 27.dp, y = cy - 27.dp)
                    .combinedClickable(
                        onClick = { spot.dest?.let { onHotspot(it) } ?: onShuffle() }
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC10131A))
                        .border(1.5.dp, RoomSky, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(spot.icon, spot.label, tint = RoomYellow, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    spot.label,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xAA10131A))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
        }
        Text(
            "Tap something in the room",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x9910131A))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .combinedClickable(onClick = onToggleNight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC10131A))
                    .border(1.5.dp, RoomSky, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (night) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    if (night) "Day" else "Night",
                    tint = RoomYellow, modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                if (night) "Day" else "Night",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xAA10131A))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
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
    onLongPress: () -> Unit
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
            Text(v.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
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
fun CategoriesScreen(counts: Map<Filter, Int>, onPick: (Filter) -> Unit, onRoom: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("The Shelves — Categories", onRoom)
        LazyColumn(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
    onRoom: () -> Unit
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
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val exo = remember {
        ExoPlayer.Builder(context).build().apply {
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

    fun savePos(forIndex: Int, pos: Long) {
        queue.getOrNull(forIndex)?.let { v ->
            val ed = prefs.edit()
            if (v.durationMs > 0 && pos > v.durationMs * 95 / 100) ed.remove("pos_${v.id}")
            else ed.putLong("pos_${v.id}", pos)
            ed.apply()
        }
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
            if (showControls) {
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
                        IconButton(onClick = { applyFullscreen(!fullscreen) }) {
                            Icon(if (fullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                "Fullscreen", tint = Color.White)
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
