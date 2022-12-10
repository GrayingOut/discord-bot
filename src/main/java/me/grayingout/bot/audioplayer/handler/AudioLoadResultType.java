package me.grayingout.bot.audioplayer.handler;

public enum AudioLoadResultType {
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
