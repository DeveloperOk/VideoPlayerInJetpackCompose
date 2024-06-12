package com.enterprise.videoviewjetpackcompose

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.enterprise.videoviewjetpackcompose.ui.theme.VideoViewJetpackComposeTheme
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var mainViewModel: MainViewModel
    lateinit var playerView: PlayerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoUrl ="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        val mediaItem = MediaItem.fromUri(videoUrl)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.player = ExoPlayer.Builder(this).build()
        mainViewModel.player?.setMediaItem(mediaItem)
        playerView = PlayerView(this)
        playerView.player = mainViewModel.player

        enableEdgeToEdge()
        setContent {
            VideoViewJetpackComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .padding(10.dp, 10.dp)
                            .fillMaxSize()
                            ){
                        VideoPlayerDemo(mainViewModel, playerView)
                    }

                }
            }
        }
    }

}

@Composable
fun VideoPlayerDemo(mainViewModel: MainViewModel, playerView: PlayerView) {

    //Do not forget to add the following permissions to the manifest
    // <uses-permission android:name="android.permission.INTERNET" />
    // <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>

    val context = LocalContext.current
    val playWhenReady by rememberSaveable {
        mutableStateOf(true)
    }

    LaunchedEffect(mainViewModel.player) {

       mainViewModel.player?.prepare()
       mainViewModel.player?.playWhenReady = playWhenReady

        mainViewModel.lastPosition?.let{mainViewModel.player?.seekTo(it)}

    }

    val systemUiController: SystemUiController = rememberSystemUiController()
    disableStausBarAndNavigationBar(systemUiController)

    var backHandlingEnabled = remember { mutableStateOf(false) }
    BackHandler(!backHandlingEnabled.value) {
        disableStausBarAndNavigationBar(systemUiController)
    }

    AndroidView( factory = {

        playerView.apply {

            setFullscreenButtonClickListener { isFullScreen ->

                player = mainViewModel.player
                backHandlingEnabled.value = isFullScreen
                mainViewModel.lastPosition = (player as ExoPlayer).currentPosition

                with(context) {
                    if (isFullScreen) {
                        setScreenOrientation(
                            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        )

                    } else {
                        setScreenOrientation(
                            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        )

                    }
                }
            }
        }
    })

}

private fun disableStausBarAndNavigationBar(systemUiController: SystemUiController) {
    systemUiController.isStatusBarVisible = false // Status bar
    systemUiController.isNavigationBarVisible = false // Navigation bar
    systemUiController.isSystemBarsVisible = false // Status & Navigation bars
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity       -> this
    is ContextWrapper -> baseContext.findActivity()
    else              -> null
}

fun Context.setScreenOrientation(
    orientation: Int
) {

    val activity = this.findActivity() ?: return
    activity.requestedOrientation = orientation

}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VideoViewJetpackComposeTheme {
        Greeting("Android")
    }
}