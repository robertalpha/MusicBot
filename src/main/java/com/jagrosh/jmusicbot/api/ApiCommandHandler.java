package com.jagrosh.jmusicbot.api;

import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.audio.AudioHandler;
import com.jagrosh.jmusicbot.audio.QueuedTrack;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ApiCommandHandler {

    private final Bot bot;

    private Guild guild;
    private Long guildId;

    public ApiCommandHandler(Bot bot, Long guildId){
        this.bot = bot;
        this.guildId = guildId;
        this.guild = bot.getJDA().getGuildById(guildId);
    }

    public void handlePayload(String payload) {
        if (this.guild == null) {
            guild = bot.getJDA().getGuildById(this.guildId);
        }
        VoiceChannel vc = bot.getSettingsManager().getSettings(guild).getVoiceChannel(guild);
        bot.getPlayerManager().setUpHandler(guild);
        if (vc != null) {
            guild.getAudioManager().openAudioConnection(vc);
        }

        bot.getPlayerManager().loadItemOrdered(guild, payload , new ApiResultHandler());
    }

    private class ApiResultHandler implements AudioLoadResultHandler {

        @Override
        public void trackLoaded(AudioTrack audioTrack) {
            AudioHandler handler = (AudioHandler)guild.getAudioManager().getSendingHandler();
            handler.stopAndClear();
            handler.addTrack(new QueuedTrack(audioTrack, null));
        }

        @Override
        public void playlistLoaded(AudioPlaylist audioPlaylist) {
            System.out.println("loaded");
        }

        @Override
        public void noMatches() {
            System.out.println("no matches");
        }

        @Override
        public void loadFailed(FriendlyException e) {
            System.out.println("load failed");
        }
    }
}
