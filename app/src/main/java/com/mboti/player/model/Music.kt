package com.mboti.player.model


import com.mboti.player.R


/***
 * Data class to represent a music in the list
 */
data class Music(
    val name: String,
    val artist: String,
    val audioSelected: Int,
)



/***
 * Return a play list of type Music data class
 */

val playList: List<Music> = mutableListOf(
        Music(
            name = "Master Of Puppets",
            artist = "Metallica",
            audioSelected = R.raw.master_of_puppets
        ),
        Music(
            name = "Everyday Normal Guy 2",
            artist = "Jon Lajoie",
            audioSelected = R.raw.everyday_normal_guy_2
        ),
        Music(
            name = "Lose Yourself",
            artist = "Eminem",
            audioSelected = R.raw.lose_yourself
        ),
       Music(
            name = "Crazy",
            artist = "Gnarls Barkley",
            audioSelected = R.raw.crazy
        ),
        Music(
            name = "Till I Collapse",
            artist = "Eminem",
            audioSelected = R.raw.till_i_collapse
        ),
)
