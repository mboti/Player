package com.mboti.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import com.mboti.player.model.Music
import com.mboti.player.model.playList
import com.mboti.player.ui.theme.PlayerTheme


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

    private fun SongScreen(playList: List<Music>) {

    }
}
