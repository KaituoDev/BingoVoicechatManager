package fun.kaituo.bingoVoicechatManager;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BingoVoicechatManager extends JavaPlugin {

    private VoicechatServerApi api;

    public VoicechatServerApi getApi() {
        return api;
    }

    public void setApi(VoicechatServerApi api) {
        this.api = api;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (service == null) {
            getLogger().warning("BingoVoicechatManager failed to get bukkit voice chat service!");
            return;
        }

        VoicechatManagerPlugin voiceChatPlugin = new VoicechatManagerPlugin(this);
        service.registerPlugin(voiceChatPlugin);
        voiceChatPlugin.createSignalListener();
        getLogger().info("BingoVoicechatManager enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getScheduler().cancelTasks(this);
    }
}
