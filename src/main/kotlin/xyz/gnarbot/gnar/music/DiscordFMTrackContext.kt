package xyz.gnarbot.gnar.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import xyz.gnarbot.gnar.Bot
import xyz.gnarbot.gnar.utils.DiscordFM

class DiscordFMTrackContext(
        val station: String,
        requester: Long,
        requestedChannel: Long
) : TrackContext(requester, requestedChannel) {
    fun nextDiscordFMTrack(musicManager: MusicManager) {
        val randomSong = DiscordFM.getRandomSong(station) ?: return

        MusicManager.playerManager.loadItemOrdered(this, randomSong, object : AudioLoadResultHandler {
            override fun trackLoaded(track: AudioTrack) {
                track.userData = this@DiscordFMTrackContext
                musicManager.scheduler.queue(track)
            }

            override fun playlistLoaded(playlist: AudioPlaylist) {
                trackLoaded(playlist.tracks.first())
            }

            override fun noMatches() {
                // Already confirmed that the list is empty.
                Bot.getPlayers().destroy(musicManager.guild)
            }

            override fun loadFailed(exception: FriendlyException) {
                // Already confirmed that the list is empty.
                Bot.getPlayers().destroy(musicManager.guild)
            }
        })
    }
}