package com.example.scoutplayer.playerScreen

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scoutplayer.R
import com.example.scoutplayer.ui.theme.ScoutPlayerTheme
import kotlin.math.abs

class PlayerActivity : ComponentActivity() {
    private val viewModel: PlayerViewModel by viewModels()

    //Flags
    private var isShuffling = false
    private var isRepeatingSong = false
    private var isRepeatingList = false

    private var mediaPlayer: MediaPlayer? = null
    private val songList = listOf(
        R.raw.clair_de_lune,
        R.raw.jazz_de_luxe,
        R.raw.la_paloma,
        R.raw.m_appari_martha,
        R.raw.mary_had_a_little_lamb,
        R.raw.minuet_in_g_flat_major,
        R.raw.moonlight,
        R.raw.moonlight_bay,
        R.raw.narowode_melodye
    )

    //Set method is built to facilitate easy movement of the value in the positive or negative direction.
    private var songIndex = 0
        set(value) {
            field = if (value > songList.lastIndex) {
                0
            } else if (value < 0) {
                songList.lastIndex
            } else {
                value
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScoutPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        ControlPanel()
                        Text(
                            text = viewModel.title.value,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(
                                dimensionResource(id = R.dimen.label_padding)
                            )
                        )
                        Text(
                            text = viewModel.artist.value,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(
                                dimensionResource(id = R.dimen.label_padding)
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    @Preview(showBackground = true)
    @Composable
    fun PlayerButtonPreview() {
        ScoutPlayerTheme {
            PlayerButton(
                label = stringResource(id = R.string.play_button_label),
                description = stringResource(id = R.string.play_button_content_description),
                imageRes = R.drawable.outline_play_arrow_36
            ) {

            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ControlPanelPreview() {
        ScoutPlayerTheme {
            ControlPanel()
        }
    }

    @Composable
    fun ControlPanel() {
        //This modifier is distributed to all buttons to allow for uniform changes
        val generalButtonModifier = Modifier.padding(6.dp)
        Column {
            Row {
                //Shuffle Button
                PlayerButton(
                    label = stringResource(id = R.string.shuffle_button_label),
                    description = stringResource(id = R.string.shuffle_button_content_description),
                    imageRes = R.drawable.baseline_shuffle_36,
                    modifier = generalButtonModifier
                ) {
                    isShuffling = !isShuffling
                    Toast.makeText(
                        this@PlayerActivity,
                        if (isShuffling) R.string.shuffle_toggle_on_toast else R.string.shuffle_toggle_off_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                //Repeat All
                PlayerButton(
                    label = stringResource(id = R.string.repeat_all_button_label),
                    description = stringResource(id = R.string.repeat_all_button_content_description),
                    imageRes = R.drawable.baseline_repeat_36,
                    modifier = generalButtonModifier
                ) {
                    isRepeatingList = !isRepeatingList
                    Toast.makeText(
                        this@PlayerActivity,
                        if (isRepeatingList) R.string.repeat_all_toggle_on_toast else R.string.repeat_all_toggle_off_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                //Repeat One
                PlayerButton(
                    label = stringResource(id = R.string.repeat_one_button_label),
                    description = stringResource(id = R.string.repeat_one_button_content_description),
                    imageRes = R.drawable.baseline_repeat_one_36,
                    modifier = generalButtonModifier
                ) {
                    isRepeatingSong = !isRepeatingSong
                    Toast.makeText(
                        this@PlayerActivity,
                        if (isRepeatingSong) R.string.repeat_song_toggle_on_toast else R.string.repeat_song_toggle_off_toast,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            Row {
                //Previous Button
                PlayerButton(
                    label = stringResource(id = R.string.previous_button_label),
                    description = stringResource(id = R.string.previous_button_content_description),
                    imageRes = R.drawable.outline_skip_previous_36,
                    modifier = generalButtonModifier
                ) {
                    /*
                    This is in an odd state, as the previous function for shuffle or repeat
                    scenarios isn't precisely clear. Typically I'd ask product about this.
                    Personally, I generally use such functionality to quickly reach the end of the
                    list, and it seems odd to require the "Repeat All" function to be on for this.
                    Additionally, while we could try to back up to the last song we had on shuffle,
                    we'd need to make a call on where to cutoff the backup function.
                     */
                    changeSong(if (isShuffling) randomNewSongIndex() else songIndex - 1)
                }
                //Play Button
                PlayerButton(
                    label = stringResource(id = R.string.play_button_label),
                    description = stringResource(id = R.string.play_button_content_description),
                    imageRes = R.drawable.outline_play_arrow_36,
                    modifier = generalButtonModifier
                ) {
                    if (mediaPlayer?.isPlaying == true) {
                        //Pause music
                        mediaPlayer?.pause()
                    } else {
                        //Play music
                        if (mediaPlayer == null) {
                            changeSong(0)
                        } else {
                            mediaPlayer?.start()
                        }
                    }
                }
                //Next Button
                PlayerButton(
                    label = stringResource(id = R.string.next_button_label),
                    description = stringResource(id = R.string.next_button_content_description),
                    imageRes = R.drawable.outline_skip_next_36,
                    modifier = generalButtonModifier
                ) {
                    /*
                        How this should interact with "repeat all" could go either way I feel.
                        Personally, my main playlist use-case, YouTube, loops you back if you have
                        list looping enabled. If you don't... it sends you off to some "recommended"
                        video. I've opted for my personal preference, as if I just wanted to stop
                        the music I'd pause it.
                     */
                    changeSong(if (isShuffling) randomNewSongIndex() else songIndex + 1)
                }
            }
        }
    }


    @Composable
    fun PlayerButton(
        label: String,
        description: String,
        @DrawableRes imageRes: Int,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
            .padding(dimensionResource(id = R.dimen.button_padding))
            .clickable { onClick() }) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = description,
                modifier = Modifier
                    .border(
                        BorderStroke(
                            dimensionResource(id = R.dimen.border_thickness),
                            Color.Black
                        )
                    )
                    .size(dimensionResource(id = R.dimen.button_size))
            )

            Text(text = label, fontSize = 14.sp)
        }
    }

    /*
    On one hand, I'd like to move the below out of this file since it's not directly UI, but on the
    other hand, the entire thing should be in it's own service or something similar. As such, I'm
    fairly comfortable with it being here where it can deal with the context-requiring MediaPlayer
    for the purposes of a prototype.
     */
    private fun changeSong(nextSongIndex: Int, startImmediately: Boolean = true) {
        songIndex = nextSongIndex

        viewModel.updateFieldsFromMetadata(resources.openRawResourceFd(songList[songIndex]))

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, songList[songIndex])
        mediaPlayer?.setOnCompletionListener {
            onSongCompleted()
        }

        if (startImmediately) {
            mediaPlayer?.start()
        }
    }

    private fun onSongCompleted() {
        if (isRepeatingSong) { //Repeating song
            /*
                Media player does have a setting to loop a single track, however I've opted to
                make this a flag for two reasons. The first is to have consistency with the other two flags,
                while the second is to facilitate future improvements and ensure that all plays of a song
                go through the "changeSong" method.
                 */
            changeSong(songIndex)
        } else if (isShuffling) { //Continue to random track.
            changeSong(randomNewSongIndex())
        } else if (songIndex == songList.lastIndex && !isRepeatingList) { //Ending list, not repeating
            //List complete, set us to 0 and wait.
            changeSong(0, false)
        } else { //Continuing to next track
            changeSong(songIndex + 1)
        }
    }

    private fun randomNewSongIndex(): Int {
        if (songList.size < 2) {
            return 0
        } else if (songList.size == 2) {
            //If we only have two songs, skip the math and just swap 0 to 1 and vice versa
            return abs(songIndex - 1)
        }

        var newIndex = -1

        //This shouldn't be an issue, but it will prevent any weird infinite loops
        val attemptsPermitted = 10
        var attempts = 0

        while ((newIndex < 0 || newIndex == songIndex) && attempts < attemptsPermitted) {
            attempts++
            newIndex = (Math.random() * songList.size).toInt()
        }

        //This will technically create a slight bias, however the odds of landing the same index
        //10 times in a row is ~.0017% even for a list of 3 songs.
        if (newIndex == songIndex) {
            newIndex++
            if (newIndex == songList.size) {
                newIndex = 0
            }
        }

        return newIndex
    }
}
