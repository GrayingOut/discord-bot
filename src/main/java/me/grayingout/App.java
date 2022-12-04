package me.grayingout;

import io.github.cdimascio.dotenv.Dotenv;
import me.grayingout.bot.Bot;
import me.grayingout.database.guildconfig.ConfigDatabase;
import me.grayingout.database.warnings.WarningsDatabase;

/**
 * The main class
 */
public final class App {

    /**
     * The {@code Dotenv} instance
     */
    public static final Dotenv env = Dotenv.configure().ignoreIfMissing().load();

    /**
     * The {@code Bot} instance
     */
    private static Bot bot;

    /**
     * The main method
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        /* Attempt to get discord token from a .env file
         * or the system env if not available
         */
        String token = env.get("DISCORD_BOT_TOKEN") != null ? env.get("DISCORD_BOT_TOKEN") : System.getenv("DISCORD_BOT_TOKEN");

        if (token == null) {
            System.err.println("Failed to locate discord bot token. Please make sure there is a .env file in the same directory, or it is added to the system env, under the name 'DISCORD_BOT_TOKEN'");
            return;
        }

        System.out.println("Starting bot with token: " + token);

        /* Initialise databases */
        WarningsDatabase.connect();
        if (!WarningsDatabase.isConnected()) {
            System.err.println("Failed to connect to warnings DB");
            return;
        }
        ConfigDatabase.connect();
        if (!ConfigDatabase.isConnected()) {
            System.err.println("Failed to connect to configurations DB");
            return;
        }

        /* Create a new bot */
        bot = new Bot(token);
    }

    /**
     * Gets the {@code Bot} instance
     * 
     * @return The bot
     */
    public static final Bot getBot() {
        return bot;
    }
}
