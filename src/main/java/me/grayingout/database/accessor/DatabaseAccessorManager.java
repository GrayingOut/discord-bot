package me.grayingout.database.accessor;

/**
 * Handles the {@code DatabaseAccessor} singletons
 */
public final class DatabaseAccessorManager {
    
    /**
     * The {@code WarningsDatabaseAccessor} singleton
     */
    private static WarningsDatabaseAccessor warningsDatabaseAccessor;

    /**
     * The {@code ConfigurationDatabaseAccessor} singleton
     */
    private static ConfigurationDatabaseAccessor configurationDatabaseAccessor;

    /**
     * The {@code LevellingDatabaseAccessor} singleton
     */
    private static LevellingDatabaseAccessor levellingDatabaseAccessor;

    /**
     * Gets the {@code WarningsDatabaseAccessor} singleton
     * 
     * @return The singleton
     */
    public static final WarningsDatabaseAccessor getWarningsDatabaseAccessor() {
        if (warningsDatabaseAccessor == null) {
            warningsDatabaseAccessor = new WarningsDatabaseAccessor();
        }

        return warningsDatabaseAccessor;
    }

    /**
     * Gets the {@code ConfigurationDatabaseAccessor} singleton
     * 
     * @return The singleton
     */
    public static final ConfigurationDatabaseAccessor getConfigurationDatabaseAccessor() {
        if (configurationDatabaseAccessor == null) {
            configurationDatabaseAccessor = new ConfigurationDatabaseAccessor();
        }

        return configurationDatabaseAccessor;
    }
    
    /**
     * Gets the {@code WarningsDatabaseAccessor} singleton
     * 
     * @return The singleton
     */
    public static final LevellingDatabaseAccessor getLevellingDatabaseAccessor() {
        if (levellingDatabaseAccessor == null) {
            levellingDatabaseAccessor = new LevellingDatabaseAccessor();
        }

        return levellingDatabaseAccessor;
    }
}
