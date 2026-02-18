package fun.kaituo.bingoVoicechatManager;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.PlayerConnectedEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import static fun.kaituo.bingoVoicechatManager.JoinLobbyGroup.LOBBY_GROUP_UUID;
import static fun.kaituo.bingoVoicechatManager.JoinLobbyGroup.LOBBY_JOIN_MESSAGE;

public class VoicechatManagerPlugin implements VoicechatPlugin {
    private final BingoVoicechatManager plugin;

    public static final String PLUGIN_ID = "bingovoicechatmanager";

    public static final int CLEAR_GROUPS_DELAY = 20; // In server ticks

    public VoicechatManagerPlugin(BingoVoicechatManager plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getPluginId() {
        return PLUGIN_ID;
    }

    public void onServerStarted(VoicechatServerStartedEvent vsse) {
        plugin.setApi(vsse.getVoicechat());
        Bukkit.getScheduler().runTaskLater(plugin,
                () -> JoinLobbyGroup.initializeGroups(plugin.getApi()),
                CLEAR_GROUPS_DELAY);
    }

    public void onPlayerConnected(PlayerConnectedEvent pce) {
        Player player = (Player) pce.getConnection().getPlayer().getPlayer();
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntityTeam(player);
        if (team != null) {
            return;
        }

        pce.getConnection().setGroup(pce.getVoicechat().getGroup(LOBBY_GROUP_UUID));
        player.sendMessage(LOBBY_JOIN_MESSAGE);
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(PlayerConnectedEvent.class, this::onPlayerConnected);
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public void createSignalListener() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            World world = Bukkit.getServer().getWorlds().getFirst();

            if (world.getBlockAt(187, 140, 79).getType().equals(Material.LIME_STAINED_GLASS)) {
                JoinLobbyGroup.initializeGroups(plugin.getApi());
                JoinLobbyGroup.allJoinLobby(plugin.getApi());
                world.getBlockAt(187, 140, 79).setType(Material.AIR);

            } else if (world.getBlockAt(187, 140, 78).getType().equals(Material.RED_STAINED_GLASS)) {
                JoinTeamGroups.initializeTeamGroups(plugin.getApi());
                JoinTeamGroups.formTeamGroups(plugin.getApi());
                world.getBlockAt(187, 140, 78).setType(Material.AIR);
            }
        }, 20, 1);
    }
}
