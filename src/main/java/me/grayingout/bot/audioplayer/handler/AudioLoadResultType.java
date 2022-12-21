package me.grayingout.bot.audioplayer.handler;

public enum AudioLoadResultType {
    /**
     * The audio is already added
     */
    ALREADY_ADDED,

    /**
     * The track has been loaded
     */
    TRACK_LOADED,

    /**
     * No track was found
     */
    NO_MATCH,
    
    /**
     * An error occurred
     */
    ERROR
}
