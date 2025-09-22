package com.example.p2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.p2.ui.theme.P2Theme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            P2Theme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MusicPlayerScreen()
                }
            }
        }
    }
}



@Composable
fun MusicPlayerScreen() {
    val context = LocalContext.current

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var currentName by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf(0) }
    var currentPosition by remember { mutableStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var albumArt by remember { mutableStateOf<Bitmap?>(null) }
    var artist by remember { mutableStateOf<String?>(null) }
    var album by remember { mutableStateOf<String?>(null) }

    val openFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, it)
                    setOnPreparedListener { mp ->
                        duration = mp.duration
                        mp.start()
                        isPlaying = true
                    }
                    setOnErrorListener { _, what, extra ->
                        errorMessage = "Error: $what, $extra"
                        true
                    }
                    prepareAsync()
                }

                currentUri = it
                currentName = uri.lastPathSegment
                errorMessage = null

                // Obtener metadatos
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, it)

                artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)

                val artBytes = retriever.embeddedPicture
                albumArt = if (artBytes != null) {
                    BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size)
                } else null

                retriever.release()

            } catch (e: Exception) {
                errorMessage = "Error al reproducir: ${e.message}"
            }
        }
    }

    // Actualiza posición actual de la canción
    LaunchedEffect(isPlaying, mediaPlayer) {
        while (isPlaying && mediaPlayer != null) {
            currentPosition = mediaPlayer?.currentPosition ?: 0
            delay(500)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎧 Tu Reproductor", color = Color.White, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(20.dp))

        if (albumArt != null) {
            Image(
                bitmap = albumArt!!.asImageBitmap(),
                contentDescription = "Carátula",
                modifier = Modifier.size(220.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Default Cover",
                modifier = Modifier.size(220.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        currentName?.let {
            Text(it, color = Color.White, style = MaterialTheme.typography.titleMedium)
        }

        artist?.let {
            Text("👤 $it", color = Color.Gray)
        }

        album?.let {
            Text("💿 $it", color = Color.Gray)
        }

        if (errorMessage != null) {
            Text("⚠️ $errorMessage", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Slider(
            value = currentPosition.toFloat(),
            onValueChange = {},
            valueRange = 0f..(duration.toFloat().coerceAtLeast(1f)),
            enabled = false,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.Green
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(currentPosition), color = Color.Gray)
            Text(formatTime(duration), color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {
                mediaPlayer?.pause()
                isPlaying = false
            }) {
                Icon(Icons.Default.Pause, contentDescription = "Pausa", tint = Color.White)
            }

            IconButton(onClick = {
                mediaPlayer?.start()
                isPlaying = true
            }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Reproducir", tint = Color.White)
            }

            IconButton(onClick = {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
                currentUri = null
                currentName = null
                albumArt = null
                artist = null
                album = null
                duration = 0
                currentPosition = 0
                isPlaying = false
            }) {
                Icon(Icons.Default.Stop, contentDescription = "Detener", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            openFileLauncher.launch(arrayOf("audio/*"))
        }) {
            Text("🎵 Buscar MP3")
        }
    }
}

fun formatTime(ms: Int): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) % 60
    return String.format("%02d:%02d", minutes, seconds)
}