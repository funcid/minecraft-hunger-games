package ru.func.hungergames;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Lobby {

    public static Location center;

    public static void waitLobby () {
        HungerGames plugin = HungerGames.getInstance();

        GameStatus.WAITING.setActive();
        center = new Location(
                Bukkit.getServer().getWorld(plugin.getConfig().getString("lobby.world")),
                plugin.getConfig().getInt("lobby.x") + 0.5F,
                plugin.getConfig().getInt("lobby.y") + 0F,
                plugin.getConfig().getInt("lobby.z") + 0.5F
        );

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            p.setGameMode(GameMode.SURVIVAL);
            p.teleport(center);
        }
        Bukkit.getWorld(plugin.getConfig().getString("lobby.world")).getWorldBorder().setCenter(center);
        Bukkit.getWorld(plugin.getConfig().getString("lobby.world")).getWorldBorder().setSize(plugin.getConfig().getInt("game.default_size"));

        new BukkitRunnable() {
            public int waitingTime = plugin.getConfig().getInt("waiting_time");
            @Override
            public void run ()
            {
                waitingTime = waitingTime - 1;
                HungerGames.updateScores(plugin, waitingTime, 0, 0);
                //Выдает уровень
                for (Player p : Bukkit.getOnlinePlayers())
                    p.setLevel(waitingTime);
                if (waitingTime == 0)
                {
                    GameStarter.startGame();
                    this.cancel();
                }
                else if (waitingTime <= plugin.getConfig().getInt("ready.time"))
                    Bukkit.broadcastMessage(plugin.getConfig().getString("ready.message") + waitingTime + ".");
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}