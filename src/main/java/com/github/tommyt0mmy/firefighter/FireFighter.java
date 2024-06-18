package com.github.tommyt0mmy.firefighter;

import com.github.tommyt0mmy.firefighter.commands.*;
import com.github.tommyt0mmy.firefighter.events.FireExtinguisherActivation;
import com.github.tommyt0mmy.firefighter.events.FireFighterChatListener;
import com.github.tommyt0mmy.firefighter.events.FiresetWand;
import com.github.tommyt0mmy.firefighter.model.MissionManager;
import com.github.tommyt0mmy.firefighter.model.MissionStorage;
import com.github.tommyt0mmy.firefighter.tabcompleters.FiresetTabCompleter;
import com.github.tommyt0mmy.firefighter.tabcompleters.HelpTabCompleter;
import com.github.tommyt0mmy.firefighter.utility.Configs;
import com.github.tommyt0mmy.firefighter.utility.Messages;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import org.bukkit.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class FireFighter extends JavaPlugin {

    private static FireFighter instance;
    public File dataFolder = getDataFolder();
    public final String version = this.getDescription().getVersion();
    public boolean startedMission = false;
    public boolean missionsIntervalState = false;
    public boolean programmedStart = false;
    public long nextMissionStart;
    public HashMap<UUID, Integer> PlayerContribution = new HashMap<>();
    public String missionName = "";
    public HashMap<UUID, Location> fireset_first_position = new HashMap<>();
    public HashMap<UUID, Location> fireset_second_position = new HashMap<>();
    public Logger console = getLogger();
    public Messages messages = null;
    public Configs configs = null;
    private static ItemStack fireHose;

    public static FireFighter getInstance()
    {
        return instance;
    }

    public void onEnable() {
        //Licence
        //Date date = new Date();
        //if (date.getDate()>=14 || date.getDate()<12) {
        //    console.info("Licence has been expired! disabling....");
        //    try {getServer().getPluginManager().disablePlugin(Objects.requireNonNull(getServer().getPluginManager().getPlugin("MoameleJobs")));
        //    }catch (Exception ignored){}
        //    getServer().getPluginManager().disablePlugin(this);
        //    return;
        //}
        //priority 1
        instance = this;

        //priority 2
        configs = new Configs();
        messages = new Messages();
        nextMissionStart = System.currentTimeMillis() + ((configs.getConfig().getInt("missions_interval")) * 1000L);

        //priority 3
        loadEvents();
        loadCommands();
        loadWand();
        loadFireHose();
        loadShomareMobile();

        //priority 4
        MissionManager.loadMissions(Objects.requireNonNull(getConfig().getConfigurationSection("missions")));
        MissionStorage.initialize(getConfig());

        //priority 5
        @SuppressWarnings("unused")
        BukkitTask task = new MissionsHandler().runTaskTimer(this, 0, 20);


        console.info("FireFighter v" + version + " enabled successfully [Forked by EhsanMNA]");
    }

    private void loadShomareMobile() {
        Shomare.notifSound = Sound.valueOf(getConfig().getString("125Sound.name"));
        Shomare.notifSoundV = getConfig().getInt("125Sound.value");
        Shomare.notifSoundF = (float) getConfig().getDouble("125Sound.pitch");
    }

    public void onDisable()
    {
        console.info("FireFighter v" + version + " disabled successfully");
    }

    private void loadEvents() {
        this.getServer().getPluginManager().registerEvents(new FireExtinguisherActivation(), this);
        this.getServer().getPluginManager().registerEvents(new FiresetWand(), this);
        this.getServer().getPluginManager().registerEvents(new FireFighterChatListener(), this);
    }

    private void loadCommands() {
        getCommand("firefighter").setExecutor(new Help());
        getCommand("fireset").setExecutor(new Fireset());
        getCommand("firefighterchat").setExecutor(new ChatToggle());
        getCommand("125").setExecutor(new Shomare());
        getCommand("firetool").setExecutor(new FireTool());
        getCommand("firefighter").setTabCompleter(new HelpTabCompleter());
        getCommand("fireset").setTabCompleter(new FiresetTabCompleter());
    }

    public void loadFireHose(){
        fireHose = XMaterial.valueOf(getConfig().getString("FireHose.material")).parseItem();
        ItemMeta meta = fireHose.getItemMeta();
        meta.setDisplayName(colorize(getConfig().getString("FireHose.name")));
        List<String> lore = new ArrayList<>();
        lore.add(messages.getMessage("fire_extinguisher"));
        lore.add(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + messages.getMessage("hold_right_click"));
        meta.setLore(colorize(lore));
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        fireHose.setItemMeta(meta);
    }

    public static String colorize(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static List<String> colorize(List<String> list){
        List<String> newList = new ArrayList<>();
        for(String string : list) newList.add(colorize(string));
        return newList;
    }

    public ItemStack getFireExtinguisher() {
        if (fireHose == null) return new ItemStack(Material.STONE);
        return fireHose;
    }

    public ItemStack loadWand(){
        ItemStack wand = getConfig().getItemStack("fireset.wand");
        FiresetWand.wand = wand;
        return wand;
    }

}
