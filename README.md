A simple MVProtoype implementation of a Media Player.

As a prototype of functionality, the UI primarily will let you know something has been activated rather than explicitly update.

A number of assumptions were made regarding it, and as such functionality is documented below:

### Shuffle - 
  This will cause the next song to be random, with no guarantee it will reach all songs an even number of times. It's only guarantee is that so long as there's more than one song to play, it will play something different next.
  
  This applies to "Next" as well, under the assumption that this button would be pressed simply to get away from the current song.
  
  This also applies to "Previous". This is somewhat less intuitive, but the most intuitive option of returning to the previous song that had been randomly played would require either a much more complicated implementation, 
    or a list dialing back all the songs we had played, which would eventually run out of entries and need to randomize anyway.
  
  Arguably, I'd prefer an implementation that would shuffle all entries into a new list, providing all entries a dependable "forward" and "backward" path, as well as ensuring everything gets equal play eventually. However, this would
    not allow for the requested sample order of "B, C, A, C, B, C, A, B,"
  
  In accordance with the above sample order, Shuffle will also cause the list to repeat

### Repeat All -
  Normally, when the last track finishes, Playback should end. Turning this on will cause it to loop back to the beginning instead.
  
  "Next" and "Previous" will always loop around the list, as there was nothing documenting how this should work at the ends of the list. This is simply because it makes looping around the list easier and it's generally what I'd want the buttons to do

### Repeat One -
  When the song ends, it will reselect itself. This reselects rather than using the built in loop flag from the media player, as this makes the flags more consistent and would make future improvements potentially easier to implement by streamlining
    all song plays through one function.
  
  "Next" and "Previous" will turn this off. (Deactivation is silent, as the Repeat One state will always be "False" after pressing the button, while toggles issue toasts for clarity)

### Previous -
  While not shuffling, plays the song immediately preceding the current song in the list. Loops from first element back to last

### Play -
  Plays the currently selected song. If the list has naturally concluded or hasn't been played yet, it will play the first song.

### Next -
  While not shuffling, plays the song immediately after the current song in the list. Loops from last element to first.
