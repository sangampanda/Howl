package com.looker.player_service.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.net.Uri
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.looker.constants.Constants.NOTIFICATION_ID
import com.looker.data_music.data.Song
import com.looker.player_service.notification.NotificationBuilder

class PlayerService : Service() {

    private var player: SimpleExoPlayer? = null
    var currentSong: Song = Song("".toUri(), 3527531118039416256)

    private lateinit var mediaSession: MediaSession
    private lateinit var mediaSessionConnector: MediaSessionConnector
    private lateinit var notification: NotificationBuilder
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        val metadata by lazy {
            currentSong.duration.let {
                MediaMetadata.Builder()
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, it)
                    .build()
            }
        }

        mediaSession = MediaSession(this, "howlmusic")
        mediaSession.setMetadata(metadata)
        mediaSessionConnector =
            MediaSessionConnector(MediaSessionCompat.fromMediaSession(this, mediaSession))
        mediaSessionConnector.setPlayer(player)
        notificationManager =
            getSystemService(NotificationManager::class.java) as NotificationManager
        notification =
            NotificationBuilder.from(this, notificationManager, mediaSession, currentSong)
        startForeground(NOTIFICATION_ID, notification.build())
    }

    private fun initPlayer(exoPlayer: SimpleExoPlayer) {
        player = exoPlayer
    }

    fun initPlayer(exoPlayer: SimpleExoPlayer, songUri: Uri) {
        initPlayer(exoPlayer)
        clearQueue()
        setMediaItem(songUri)
        prepare()
        play()
    }

    fun clearQueue() {
        player?.clearMediaItems()
    }

    fun setMediaItem(songUri: Uri) {
        player?.setMediaItem(MediaItem.fromUri(songUri))
    }

    fun prepare() {
        player?.prepare()
    }

    fun play() {
        player?.play()
    }

    fun togglePlay() {
        if (player?.isPlaying == true) player?.pause()
        else player?.play()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY
    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        mediaSession.release()
    }
}