package me.grayingout.database.accessors;

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
     * Initialises all {@code DatabaseAccessor}s
     */
    public static final void initDatabaseAccessors() {
        warningsDatabaseAccessor = new WarningsDatabaseAccessor();
        configurationDatabaseAccessor = new ConfigurationDatabaseAccessor();
        levellingDatabaseAccessor = new LevellingDatabaseAccessor();
    }

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
