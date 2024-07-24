package com.example.scoutplayer.playerScreen

import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

//This has some hardcoded strings, which I generally dislike having,
// but as this is a prototype they aren't being fixed at the moment
class PlayerViewModel : ViewModel() {
    val title = mutableStateOf("")
    val artist = mutableStateOf("")

    fun updateFieldsFromMetadata(assetFileDescriptor: AssetFileDescriptor){
        val metadataReader = MediaMetadataRetriever()

        metadataReader.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )

        title.value = metadataReader.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE) ?: "No Title"
        artist.value = metadataReader.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST) ?: "No Artist"

        metadataReader.release()
    }
}
