package com.mboti.player

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults.filledTonalIconToggleButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import com.mboti.player.Commun.convertToText
import com.mboti.player.model.Music
import com.mboti.player.model.playList
import com.mboti.player.ui.theme.PlayerTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


/*--------------------------------------------------------
TODO Ajouter dans le manifeste.xml les deux lignes afin de
 contrer les problèmes d'allocation mémoire pour API24

 <application
        android:hardwareAccelerated="false"
        android:largeHeap="true"

        +

    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    implementation ("androidx.media3:media3-exoplayer:1.3.1")
 --------------------------------------------------------*/

class MainActivity : ComponentActivity() {

    lateinit var player: ExoPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()

        setContent {
            PlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InitProcessus(playList)
                }
            }
        }
    }




    @Composable
    fun VerticalSliderSound(progressValue: Int? = null, value: (Int) -> Unit) {

        val state = rememberComposeVerticalSliderState()

        ComposeVerticalSlider(
            state = state,
            enabled = state.isEnabled.value,
            progressValue = progressValue,
            width = 70.dp,
            height = 200.dp,
            radius = CornerRadius(20f, 20f),
            trackColor = MaterialTheme.colorScheme.tertiaryContainer,
            progressTrackColor = MaterialTheme.colorScheme.primary,
            onProgressChanged = {
                value(it)
            },
            onStopTrackingTouch = {
                value(it)
            }
        )
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {



            Spacer(modifier = Modifier.height(24.dp))


            val isPlaying = remember { mutableStateOf(false) }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Button(
                    onClick = {
                        isPlaying.value = false
                        player.setPlayWhenReady(false);
                        player.stop();
                        player.seekTo(0);
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        //contentColor = Color.White
                    ),
                    enabled = player.isPlaying
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_stop),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                ButtonControl(icon = R.drawable.ic_previous, size = 20.dp, onClick = {
                    player.seekToPreviousMediaItem()
                })

                Spacer(modifier = Modifier.width(10.dp))


                FilledTonalIconToggleButton(
                    checked = isPlaying.value,
                    onCheckedChange = {
                        isPlaying.value = it
                        if (isPlaying.value) {
                            player.play()
                        } else {
                            player.pause()
                        }
                    },
                    colors =filledTonalIconToggleButtonColors(if (isPlaying.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.size(80.dp),
                )
                {
                    if (isPlaying.value) {
                        //Icon(Icons.Filled.Lock, contentDescription = "Localized description", modifier = Modifier.size(50.dp))
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_pause),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_play),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }


                Spacer(modifier = Modifier.width(10.dp))

                ButtonControl(icon = R.drawable.ic_next, size = 20.dp, onClick = {
                    player.seekToNextMediaItem()
                })
            }



            Column (Modifier.fillMaxSize()){
                Row (Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween)
                {
                    VerticalSliderSound("ٱلْفَاتِحَةِ","Fatiha")
                    VerticalSliderSound("آيات","Ayat")
                    VerticalSliderSound("آحرون","Others")
                }
                Spacer(modifier = Modifier.width(10.dp))
                SwitchCountdown()
                Spacer(modifier = Modifier.width(10.dp))
                SwitchBeep()
                SliderSpeed()
            }





            TrackSliderPlayer(
                value = sliderPosition.longValue.toFloat(),
                onValueChange = {
                    sliderPosition.longValue = it.toLong()
                },
                onValueChangeFinished = {
                    currentPosition.longValue = sliderPosition.longValue
                    player.seekTo(sliderPosition.longValue)
                },
                songDuration = totalDuration.longValue.toFloat(),
                currentPosition,
                totalDuration,
            )

        }
    }

    @Composable
    fun SwitchCountdown() {
        val switchCountdownState = remember { mutableStateOf(false) }

        Row(
            Modifier
                .fillMaxWidth()
                /*.padding(start = 20.dp, end = 20.dp)*/,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween)
        {
            Text(text = "Countdown")
            Switch(
                checked = switchCountdownState.value,
                onCheckedChange = { isChecked ->
                    switchCountdownState.value = isChecked
                }
            )
        }
    }


    @Composable
    fun SwitchBeep() {
        val switchBeepState = remember { mutableStateOf(false) }

        Row(
            Modifier
                .fillMaxWidth()
            /*.padding(start = 20.dp, end = 20.dp)*/,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween)
        {
            Text(text = "Beeps")
            Switch(
                checked = switchBeepState.value,
                onCheckedChange = { isChecked ->
                    switchBeepState.value = isChecked
                }
            )
        }
    }

    @Composable
    fun ButtonControl(icon: Int, size: Dp, onClick: () -> Unit) {
        Button(
            onClick = {
                onClick()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                //contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(size)
            )
        }
    }


    /**
     * Tracks and visualizes the song playing actions.
     */
    @Composable
    fun TrackSliderPlayer(
        value: Float,
        onValueChange: (newValue: Float) -> Unit,
        onValueChangeFinished: () -> Unit,
        songDuration: Float,
        currentPosition: MutableLongState,
        totalDuration: MutableLongState
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
    }




    @Composable
    fun VerticalSliderSound(titleArabic:String, title:String){

        val steps = 100 // Number of steps in the SeekBar
        val divided = steps.toFloat()

        var sliderProgressValue by rememberSaveable { mutableIntStateOf(70) }
        Box {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomCenter){
                    Card(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        VerticalSliderSound(
                            progressValue = sliderProgressValue
                        ) {
                            sliderProgressValue = it

                            player.volume = (sliderProgressValue/divided)
                        }
                    }

                    val icoSpeaker = when (sliderProgressValue) {
                        in 80..100 ->  R.drawable.ico_speaker3
                        in 30..79 ->  R.drawable.ico_speaker2
                        in 1..29 ->  R.drawable.ico_speaker1
                        else -> R.drawable.ico_speaker0
                    }
                    Image (
                        painter = painterResource(id = icoSpeaker),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(5.dp))
                //Text("$sliderProgressValue", textAlign = TextAlign.Center, fontSize = 50.sp)
                Text(titleArabic, textAlign = TextAlign.Center, fontSize = 22.sp)
                Text(title, textAlign = TextAlign.Center, fontSize = 13.sp)
            }
        }
    }



    @Composable
    fun SliderSpeed() {
        var sliderSpeedPosition by remember { mutableFloatStateOf(1f) }
        Row(
            Modifier
                .fillMaxWidth()
            /*.padding(start = 20.dp, end = 20.dp)*/,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Speed")
            Spacer(modifier = Modifier.padding(40.dp))
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "x$sliderSpeedPosition")
                Slider(
                    value = sliderSpeedPosition,
                    onValueChange = {
                        sliderSpeedPosition = arroundValue(it)
                        val playbackParameters = PlaybackParameters(sliderSpeedPosition) // 1.5x speed
                        player.playbackParameters = playbackParameters
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    //steps = ,
                    valueRange = 0.5f..2f
                )
            }

        }
    }

    private fun arroundValue(v:Float):Float{
        return (v * 10.0).roundToInt() / 10.0F
    }


//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    fun ExposedDropdownMenuSample() {
//        val options = listOf("2", "1.75", "1.5", "1.25" , "1", "0.75", "0.5")
//        var expanded by remember { mutableStateOf(false) }
//        var selectedOptionText by remember { mutableStateOf(options[4]) }
//        // We want to react on tap/press on TextField to show menu
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = { expanded = it },
//            modifier = Modifier.widthIn(max = 150.dp)
//        ) {
//            TextField(
//                // The `menuAnchor` modifier must be passed to the text field for correctness.
//                modifier = Modifier.menuAnchor(),
//                readOnly = true,
//                value = selectedOptionText,
//                onValueChange = {},
//                label = { Text("Speed") },
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                colors = ExposedDropdownMenuDefaults.textFieldColors(),
//            )
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false },
//            ) {
//                options.forEach { selectionOption ->
//                    DropdownMenuItem(
//                        text = { Text(selectionOption) },
//                        onClick = {
//                            selectedOptionText = selectionOption
//                            expanded = false
//                        },
//                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
//                    )
//                }
//            }
//        }
//    }


}
