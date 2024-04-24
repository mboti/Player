package com.mboti.player

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.mboti.player.Commun.convertToText
import com.mboti.player.model.Music
import com.mboti.player.model.playList
import com.mboti.player.ui.theme.PlayerTheme
import kotlinx.coroutines.delay


/*--------------------------------------------------------
TODO Ajouter dans le manifeste.xml les deux lignes afin de
 contrer les problèmes d'allocation mémoire pour API24

 <application
        android:hardwareAccelerated="false"
        android:largeHeap="true"
 --------------------------------------------------------*/


class MainActivity : ComponentActivity() {

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()

        setContent {
            PlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SongScreen(playList)
                }
            }
        }
    }


    @Composable
    private fun SongScreen(playList: List<Music>) {
        InitProcessus(playList)

    }


    @Composable
    private fun InitProcessus(playList: List<Music>) {
        val playingSongIndex = remember { mutableIntStateOf(0) }

        // currentMediaItemIndex : Returns the index of the current MediaItem in the timeline,
        // or the prospective index if the current timeline is empty.
        LaunchedEffect(player.currentMediaItemIndex) {
            playingSongIndex.intValue = player.currentMediaItemIndex
        }

        LaunchedEffect(Unit) {
            playList.forEach {
                val path = "android.resource://" + packageName + "/" + it.audioSelected
                val mediaItem = MediaItem.fromUri(Uri.parse(path))
                player.addMediaItem(mediaItem)
            }
        }

        player.prepare()

        val isPlaying = remember { mutableStateOf(false) }
        val currentPosition = remember { mutableLongStateOf(0) }

        LaunchedEffect(key1 = player.currentPosition, key2 = player.isPlaying) {
            delay(1000)
            currentPosition.longValue = player.currentPosition
        }


        val sliderPosition = remember { mutableLongStateOf(0) }
        LaunchedEffect(currentPosition.longValue) {
            sliderPosition.longValue = currentPosition.longValue
        }



        val totalDuration = remember { mutableLongStateOf(0) }
        LaunchedEffect(player.duration) {
            if (player.duration > 0) {
                totalDuration.longValue = player.duration
            }
        }


        /**
         *   UI
         */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TrackSlider(
                value = sliderPosition.longValue.toFloat(),
                onValueChange = {
                    sliderPosition.longValue = it.toLong()
                },
                onValueChangeFinished = {
                    currentPosition.longValue = sliderPosition.longValue
                    player.seekTo(sliderPosition.longValue)
                },
                songDuration = totalDuration.longValue.toFloat()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {

                Text(
                    text = (currentPosition.longValue).convertToText(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                val remainTime = totalDuration.longValue - currentPosition.longValue
                Text(
                    text = if (remainTime >= 0) remainTime.convertToText() else "",
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonControl(icon = R.drawable.ic_previous, size = 40.dp, onClick = {
                    player.seekToPreviousMediaItem()
                })
                Spacer(modifier = Modifier.width(20.dp))
                ButtonControl(
                    icon = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play,
                    size = 100.dp,
                    onClick = {
                        if (isPlaying.value) {
                            player.pause()
                        } else {
                            player.play()
                        }
                        isPlaying.value = player.isPlaying
                    })
                Spacer(modifier = Modifier.width(20.dp))
                ButtonControl(icon = R.drawable.ic_next, size = 40.dp, onClick = {
                    player.seekToNextMediaItem()
                })
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    val volume = 0.1f // 0.0f is silent, 1.0f is full volume
                    player.volume = volume
                }) {
                    Text("-")
                }
                Button(onClick = {
                    val volume = 1.0f // 0.0f is silent, 1.0f is full volume
                    player.volume = volume
                }) {
                    Text("+")
                }
            }


            SeekBarDemo()

            FiveStepSeekBarExample()
        }

    }


    /**
     * Tracks and visualizes the song playing actions.
     */
    @Composable
    fun TrackSlider(
        value: Float,
        onValueChange: (newValue: Float) -> Unit,
        onValueChangeFinished: () -> Unit,
        songDuration: Float
    ) {
        Slider(
            value = value,
            onValueChange = { onValueChange(it) },
            onValueChangeFinished = { onValueChangeFinished() },
            valueRange = 0f..songDuration,
            colors = SliderDefaults.colors(
                thumbColor = Color.Black,
                activeTrackColor = Color.DarkGray,
                inactiveTrackColor = Color.Gray,
            )
        )
    }

    @Composable
    fun ButtonControl(icon: Int, size: Dp, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .clickable {
                    onClick()
                }, contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(size / 1.5f),
                painter = painterResource(id = icon),
                tint = Color.Black,
                contentDescription = null
            )
        }
    }






    @Composable
    fun SeekBarDemo() {
        // Remembering the value of the SeekBar
        val sliderPosition = remember { mutableStateOf(0f) }

        // Compose UI
        Surface(color = Color.White) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = sliderPosition.value,
                    onValueChange = { newValue ->
                        sliderPosition.value = newValue
                    },
                    valueRange = 0f..100.0f,
                    steps = 20,
                    modifier = Modifier.width(300.dp)
                )

                // Display the current value of the SeekBar
                Text(
                    text = "Value: ${sliderPosition.value.toInt()}",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    @Preview
    @Composable
    fun PreviewSeekBarDemo() {
        SeekBarDemo()
    }



    @Composable
    fun FiveStepSeekBar(
        progress: Int,
        onProgressChanged: (Int) -> Unit
    ) {
        val steps = 10 // Number of steps in the SeekBar
        val diviseur = steps.toFloat()

        Slider(
            value = progress.toFloat() / steps,
            onValueChange = { value ->
                val newProgress = (value * steps).toInt()
                onProgressChanged(newProgress)
                player.volume = newProgress/diviseur
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }

    @Composable
    fun FiveStepSeekBarExample() {
        var progress by remember { mutableStateOf(0) }

        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Progress: $progress",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                FiveStepSeekBar(
                    progress = progress,
                    onProgressChanged = { newProgress ->
                        progress = newProgress
                    }
                )
            }
        }
    }

    @Preview
    @Composable
    fun PreviewFiveStepSeekBarExample() {
        FiveStepSeekBarExample()
    }
}
