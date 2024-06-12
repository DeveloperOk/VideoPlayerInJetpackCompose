package com.enterprise.videoviewjetpackcompose

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainViewModel: ViewModel() {

    var player: ExoPlayer? = null

    var lastPosition: Long? = null

}