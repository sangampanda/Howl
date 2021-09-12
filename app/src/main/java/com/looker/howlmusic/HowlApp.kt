package com.looker.howlmusic

import android.app.WallpaperManager
import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.looker.components.HowlSurface
import com.looker.components.rememberDominantColorState
import com.looker.howlmusic.ui.components.*
import com.looker.howlmusic.ui.theme.HowlMusicTheme
import com.looker.howlmusic.ui.theme.WallpaperTheme
import com.looker.onboarding.OnBoardingPage
import com.looker.ui_player.MiniPlayer
import com.looker.ui_player.components.PlaybackControls

@Composable
fun App() {
    val context = LocalContext.current
    var canReadStorage by remember { mutableStateOf(checkReadPermission(context)) }
    val wallpaperManager = WallpaperManager.getInstance(context)

    HowlMusicTheme {
        if (canReadStorage) {
            val wallpaperBitmap = wallpaperManager.drawable.toBitmap()
            AppTheme(wallpaperBitmap)
        } else OnBoardingPage { canReadStorage = it }
    }
}

@Composable
fun AppTheme(wallpaper: Bitmap? = null) {
    val dominantColor = rememberDominantColorState()

    LaunchedEffect(wallpaper) {
        dominantColor.updateColorsFromBitmap(wallpaper)
    }

    WallpaperTheme(dominantColor) {
        ProvideWindowInsets {
            HowlSurface(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) {
                AppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AppContent(viewModel: HowlViewModel = viewModel()) {
    val items = listOf(
        HomeScreens.SONGS,
        HomeScreens.ALBUMS
    )
    val navController = rememberNavController()
    val backdropState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    Scaffold(
        bottomBar = {
            BottomAppBar(
                navController = navController,
                items = items
            )
        }
    ) {
        val currentFraction = backdropState.currentFraction
        Backdrop(
            modifier = Modifier.padding(it),
            state = backdropState,
            currentFraction = currentFraction,
            playing = viewModel.playing.value,
            header = {
                Column {
                    Icon(
                        modifier = Modifier
                            .statusBarsHeight()
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = "Pull Down"
                    )
                    MiniPlayer(
                        modifier = Modifier.padding(20.dp),
                        songName = "Name",
                        artistName = "Name",
                        albumArt = "https://picsum.photos/400/300",
                        icon = viewModel.shufflePlay(currentFraction),
                        toggled = viewModel.toggle(currentFraction),
                        toggleAction = { viewModel.onToggle(currentFraction) }
                    )
                }
            },
            frontLayerContent = { HomeNavGraph(navController = navController) },
            backLayerContent = {
                Column {
                    PlaybackControls(
                        playIcon = viewModel.playIcon,
                        progressValue = viewModel.progress.value,
                        onPlayPause = { viewModel.onPlayPause() },
                        onSeek = { seekTo -> viewModel.onSeek(seekTo) }
                    )
                    Spacer(Modifier.statusBarsHeight())
                }
            }
        )
    }
}