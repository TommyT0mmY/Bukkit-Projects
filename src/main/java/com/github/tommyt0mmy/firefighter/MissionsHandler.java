package com.github.tommyt0mmy.firefighter;

import com.github.tommyt0mmy.firefighter.model.Mission;
import com.github.tommyt0mmy.firefighter.model.MissionManager;
import com.github.tommyt0mmy.firefighter.model.MissionStorage;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import com.github.tommyt0mmy.firefighter.utility.titles.ActionBar;
import com.github.tommyt0mmy.firefighter.utility.titles.Titles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MissionsHandler extends BukkitRunnable {

    private FireFighter FireFighterClass = FireFighter.getInstance();

    public MissionsHandler()
    {
        config = FireFighterClass.getConfig();
    }

    private Boolean firstRun = true;
    private FileConfiguration config;
    private List<Block> setOnFire = new ArrayList<>();
    static Material fire = XMaterial.FIRE.parseMaterial();
    static int fireKeepTimer = 0;

    public static HashMap<UUID,Integer> delays = new HashMap<>();

    @Override
    public void run() {
        for (UUID id : delays.keySet()){
            int i = delays.get(id)-1;
            if (i == 0) delays.remove(id);
            else delays.put(id,i);
        }

        if (firstRun) {
            firstRun = false;
            return;
        }
        if (MissionManager.getMissions().isEmpty()) {
            FireFighterClass.console.info("There are no missions! Start setting up new missions by typing in-game '/firefighter fireset 2'");
            return;
        }

        // when mission starts -> programStart
        int fire_lasting_ticks = MissionStorage.fire_lasting_seconds * 20;
        Random random = new Random();
        Mission mission = MissionManager.getMissions().get(random.nextInt(MissionManager.getMissions().size()));

        if (FireFighterClass.startedMission) {
            //keeping the fire on
            fireKeepTimer++;
            if ((fireKeepTimer % 5) ==0){
                if (fireKeepTimer >= fire_lasting_ticks / 100) {
                    //TURNING OFF THE MISSION
                    fireKeepTimer = 0;
                    Bukkit.getWorld(mission.getWorldName()).setGameRule(GameRule.DO_FIRE_TICK,true);
                    turnOffInstructions();
                    //cancel();
                    return;
                }
                else for (int i = 0; i < setOnFire.size(); i++) {
                    Block currBlock = setOnFire.get(i);
                    if (currBlock.getType().equals(fire) && !currBlock.getType().equals(Material.AIR)) continue;
                    if (random.nextInt(2) == 1) {
                        //randomizing the respawn of the fire
                        setOnFire.remove(i);
                        continue;
                    }
                    currBlock.setType(fire);
                }
            }
        }
        if (System.currentTimeMillis() < FireFighterClass.nextMissionStart && !FireFighterClass.programmedStart) return;
        if (!FireFighterClass.missionsIntervalState && !FireFighterClass.programmedStart) return;

        // Start mission
        FireFighterClass.startedMission = true;
        //selecting random mission
        if (MissionManager.getMissions().size() < 1) {
            FireFighterClass.console.info("There are no missions! Start setting up new missions by typing in-game '/firefighter fireset 2'");
            return;
        }
        if (!FireFighterClass.programmedStart) {
            //if started randomly
            FireFighterClass.missionName = mission.getId();
            FireFighterClass.nextMissionStart = System.currentTimeMillis() + (MissionStorage.missions_interval * 1000L) + (MissionStorage.fire_lasting_seconds * 1000L);
        } //if programmed with /fireset startmission
        else FireFighterClass.programmedStart = false;
        
        FireFighterClass.PlayerContribution.clear();
        //broadcast message
        if (mission.getWorldName() == null){
            turnOffInstructions();
            cancel();
        }
        World world = Bukkit.getWorld(mission.getWorldName());
        //avoids NPE
        if (world == null) return;

        String title = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_title")
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));
        String subtitle = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_subtitle")
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));
        String hotBar = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_hotbar")
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));
        String chat = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_chat")
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));

        Broadcast(world, title, subtitle, hotBar, Permissions.ON_DUTY.getNode());
        Broadcast(world, chat, Permissions.ON_DUTY.getNode());

        FireFighterClass.console.info("[" + world.getName() + "] Started '" + mission.getId() + "' mission");
        Bukkit.getWorld(mission.getWorldName()).setGameRule(GameRule.DO_FIRE_TICK,false);
        //starting fire
        int y = mission.getAltitude();
        int x1 = mission.getFirstX();
        int z1 = mission.getFirstZ();
        int x2 = mission.getSecondX();
        int z2 = mission.getSecondZ();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                Location currLocation = new Location(world, x, y, z);
                int n = 0;
                while (!currLocation.getBlock().getType().equals(Material.AIR)) {
                    currLocation.add(0, 1, 0);
                    n++;
                    if (n==30) break;
                }

                //randomizing the spawning of the fire
                if (random.nextBoolean()) continue;
                
                currLocation.subtract(0, 1, 0);
                if (currLocation.getBlock().getType().equals(Material.AIR)) continue;
                
                currLocation.add(0, 1, 0);
                Block currBlock = currLocation.getBlock();
                assert fire != null;
                currBlock.setType(fire);
                setOnFire.add(currBlock);
            }
    }

    private void Broadcast(World w, String title, String subtitle, String hotbar, String permission) {
        //avoids NPE
        if (w == null) return;

        for (Player dest : w.getPlayers()) {
            if (!dest.hasPermission(permission)) continue;
            Titles.sendTitle(dest,10,100,20,title,subtitle);
            try {
                new BukkitRunnable() {
                    int timer = 0;

                    public void run() {
                        timer++;
                        ActionBar.sendActionBar(dest,hotbar);
                        if (timer >= 4) cancel();
                    }
                }.runTaskTimer(FireFighterClass, 0, 50);
            } catch (Exception ignored) {}
        }
    }

    private void Broadcast(World w, String message, String permission) {
        //avoids NPE
        if (w == null) return;
        for (Player dest : w.getPlayers())
            if (dest.hasPermission(permission))
                dest.sendMessage(message);
    }

    private String getMediumCoord(String missionName) {
        //returns the medium position of the mission
        String res = "";
        String missionPath = "missions." + missionName;
        res += (((Integer.parseInt(config.get(missionPath + ".first_position.x").toString()) + Integer.parseInt(config.get(missionPath + ".second_position.x").toString())) / 2) + ""); //X
        res += " ";
        res += (config.get(missionPath + ".altitude").toString()); // Y
        res += " ";
        res += (((Integer.parseInt(config.get(missionPath + ".first_position.z").toString()) + Integer.parseInt(config.get(missionPath + ".second_position.z").toString())) / 2) + ""); // Z
        return res;
    }

    private void turnOffInstructions() {
        FireFighterClass.console.info("Mission ended");
        FireFighterClass.startedMission = false;
        FireFighterClass.missionName = "";
        setOnFire.clear();
        FireFighterClass.PlayerContribution.clear();
    }
}