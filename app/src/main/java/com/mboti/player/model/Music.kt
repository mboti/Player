package com.mboti.player.model


import com.mboti.player.R


/***
 * Data class to represent a music in the list
 */
data class Music(
    val name: String,
    val artist: String,
    val music: Int,
)



/***
 * Return a play list of type Music data class
 */

val playList: List<Music> = mutableListOf(
        Music(
            name = "Master Of Puppets",
            artist = "Metallica",
            music = R.raw.master_of_puppets
        ),
        Music(
            name = "Everyday Normal Guy 2",
            artist = "Jon Lajoie",
            music = R.raw.everyday_normal_guy_2
        ),
        Music(
            name = "Lose Yourself",
            artist = "Eminem",
            music = R.raw.lose_yourself
        ),
       Music(
            name = "Crazy",
            artist = "Gnarls Barkley",
            music = R.raw.crazy
        ),
        Music(
            name = "Till I Collapse",
            artist = "Eminem",
            music = R.raw.till_i_collapse
        ),
)
