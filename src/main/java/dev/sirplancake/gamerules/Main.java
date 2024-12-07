package dev.sirplancake.gamerules;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class Main extends JavaPlugin {
    private Logger Logger;
    private Server Server;

    public Main() {
        this.Logger = getSLF4JLogger();
        this.Server = getServer();
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        applyGameRules();
        Logger.info("Successfully enabled.");
    }

    @Override
    public void onDisable() {
        Logger.info("Successfully disabled.");
    }

    private void applyGameRules() {
        FileConfiguration Config = getConfig();
        for (World World : Bukkit.getWorlds()) {
            String Dimension = getDimension(World);
            for (String RuleName : Config.getKeys(false)) {
                try {
                    GameRule<?> Rule =  getGameRule(RuleName);
                    if (Rule != null) {
                        Object Value = Config.get(RuleName + "." + Dimension);
                        if (Value instanceof Boolean && Rule.getType() == Boolean.class) {
                            World.setGameRule((GameRule<Boolean>) Rule, (Boolean) Value);
                        } else if (Value instanceof Integer && Rule.getType() == Integer.class) {
                            World.setGameRule((GameRule<Integer>) Rule, (Integer) Value);
                        } else {
                            Logger.warn("Invalid value type for gamerule: " + RuleName);
                        }

                        if (Dimension.equals("overworld")) {
                            Logger.info("Loaded gamerule '" + RuleName + "' with the value of '" + Value.toString().toUpperCase() + "'.");
                        }
                    } else {
                        Logger.warn("Invalid gamerule: " + RuleName);
                    }
                } catch (Exception Error) {
                    Logger.warn("There was an error setting a gamerule: " + RuleName + " -> " + Error.getMessage());
                }
            }
        }
    }

    private GameRule<?> getGameRule(String Name) {
        for (GameRule<?> Rule : GameRule.values()) {
            if (Rule.getName().equalsIgnoreCase(Name)) {
                return Rule;
            }
        }

        return null;
    }

    private String getDimension(World World) {
        return switch (World.getEnvironment()) {
            case NORMAL -> "overworld";
            case NETHER -> "nether";
            case THE_END -> "end";
            default -> "unknown";
        };
    }
}
