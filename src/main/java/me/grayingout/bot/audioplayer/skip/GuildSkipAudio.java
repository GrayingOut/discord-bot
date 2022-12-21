package me.grayingout.bot.audioplayer.skip;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Manages the vote skipping of audio in a guild
 */
public final class GuildSkipAudio extends ListenerAdapter {

    /**
     * The guild is {@code GuildSkipAudio} belongs to
     */
    private final Guild guild;

    /**
     * Stores the current count of members in the bot's audio
     * channel
     */
    private int audioChannelMemberCount;

    /**
     * Stores the members that have voted to skip
     */
    private List<Long> skipVoteMembers;

    /**
     * The threshold to confirm a skip on
     * initial skip
     */
    private int voteSkipThreshold;

    /**
     * Whether a vote skip is active
     */
    private boolean voteSkipActive;

    /**
     * Create a new {@code GuildSkipAudio} for a guild
     * 
     * @param guild The guild to create it for
     */
    public GuildSkipAudio(Guild guild) {
        this.guild = guild;
        audioChannelMemberCount = 0;
        skipVoteMembers = new ArrayList<>();
        voteSkipThreshold = 0;
        voteSkipActive = false;
    }

    /**
     * Returns whether there is ana active vote skip
     * 
     * @return If there is an active vote skip
     */
    public final boolean isVoteSkipActive() {
        return voteSkipActive;
    }

    /**
     * Gets the guild this {@code GuildSkipAudio} is for
     * 
     * @return The guild
     */
    public final Guild getGuild() {
        return guild;
    }

    /**
     * Gets the current number of votes to skip
     * 
     * @return The current skip vote count
     */
    public final int getCurrentVoteSkips() {
        return skipVoteMembers.size();
    }

    /**
     * Get the number of votes needed for a skip
     * to pass
     * 
     * @return The number of votes needed
     */
    public final int getVoteSkipsThreshold() {
        return voteSkipThreshold;
    }

    /**
     * Resets the number of vote skips and
     * threshold
     */
    public final void resetVoteSkips() {
        skipVoteMembers.clear();
        voteSkipThreshold = 0;
        voteSkipActive = false;
    }

    /**
     * Returns if a member has already voted to skip
     * 
     * @param member The member
     * @return If the member has already voted
     */
    public final boolean hasMemberAlreadyVotedToSkip(Member member) {
        return skipVoteMembers.contains(member.getIdLong());
    }

    /**
     * Adds a vote to skip and returns if the threshold
     * has been reached
     * 
     * @param member The member that voted
     * @return If the threshold has been reached
     */
    public final boolean addVoteSkip(Member member) {
        /* Check if first vote */
        if (skipVoteMembers.size() == 0) {
            /* Set the threshold (1 vote for up to 3 members, then 50% after - up to max of 20) */
            voteSkipThreshold = (audioChannelMemberCount-1) <= 3 ? 1 : Math.min(audioChannelMemberCount/2, 20);
            voteSkipActive = true;
        }

        skipVoteMembers.add(member.getIdLong());
        if (skipVoteMembers.size() >= voteSkipThreshold) {
            return true;
        }
        return false;
    }

    /**
     * Called when a guild member joins the bot's
     * audio channel
     */
    public final void memberJoined() {
        audioChannelMemberCount++;
    }

    /**
     * Called when a guild member leaves the bot's audio
     * channel
     */
    public final void memberLeft() {
        audioChannelMemberCount--;
    }

    /**
     * Called when the bot left an audio chanel
     */
    public final void botLeftChannel() {
        audioChannelMemberCount = 0;
        skipVoteMembers.clear();
        voteSkipThreshold = 0;
        voteSkipActive = false;
    }

    /**
     * Called when the bot joins an audio chanel
     */
    public final void botJoinedChannel(AudioChannel channel) {
        audioChannelMemberCount = channel.getMembers().size();
    }
}
