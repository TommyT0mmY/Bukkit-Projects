package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Help implements CommandExecutor {
    /* firefighter command */
    private FireFighter fireFighterClass;
    public Help(FireFighter fireFighterClass) {
        this.fireFighterClass = fireFighterClass;
    }

    private String getUsage() {
        return ((String) fireFighterClass.getDescription().getCommands().get("com/github/tommyt0mmy/firefighter").get("usage")).replaceAll("<command>", "com/github/tommyt0mmy/firefighter");
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command cmd, String label, String[] args) {
        if (!(Sender instanceof Player)) {
            Sender.sendMessage(fireFighterClass.prefix + "Only players can execute this command!");
            return true;
        }
        Player p = (Player) Sender;
        if (!(p.hasPermission(fireFighterClass.getPermission("com/github/tommyt0mmy/firefighter")) || p.isOp())) {
            p.sendMessage(fireFighterClass.messages.get("invalid_permissions"));
            return true;
        }

        if (args.length == 0) {
            sendPage("1", p);
        } else if (args.length == 1) {
            sendPage(args[0], p);
        } else if (args.length == 2) {
            sendPage(args[0] + args[1], p);
        } else {
            p.sendMessage("§4" + getUsage()); //sending 'Usage: ' message
        }
        return true;
    }

    private String getCommandDescription(String commandName) {
        if (fireFighterClass.getDescription().getCommands().get(commandName).get("description") != null) {
            return (String) fireFighterClass.getDescription().getCommands().get(commandName).get("description");
        } else {
            return "No description";
        }
    }

    public void sendPage(String index, Player destination) {
        String beforeCommand = "§e§l►"; //UTF8 char
        String beforeParagraph = "§c§l►"; //UTF8 char
        String msg = "";

        switch (index) {
            default:
                destination.sendMessage(fireFighterClass.messages.get("page_not_found"));
                return;
            case "1": // PAGE 1 //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§cCommands overall\n");
                msg += ("§7§o/firefighter [command name] [page #] §e§oTo get a more detailed description of the command \n");
                msg += ("§7§o/firefighter permissions §e§oTo get a list of the permissions \n");
                msg += (beforeCommand + "§7fireset§e " + getCommandDescription("fireset") + "\n");
                msg += (beforeCommand + "§7firetool§e " + getCommandDescription("firetool") + "\n");
                msg += (beforeCommand + "§7firefighter [command|page] ...§e " + getCommandDescription("com/github/tommyt0mmy/firefighter") + "\n");
                //		(beforeCommand + "§7...§e ..."); //reference
                msg += ("§c§l+ - §r§epage 1/1§c§l- - - - - - - - - - - - +§r");
                break;
            case "fireset1":
            case "fireset": // FIRESET PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'fireset' Command\n");
                msg += (beforeCommand + "§eUsage: §7/fireset [setwand|addmission|editmission|deletemission] ...\n");
                msg += (beforeCommand + "§ePermission node: §7" + fireFighterClass.getPermission("fireset") + "\n");
                msg += (beforeCommand + "§eDescription: §7Sets a new point that will catch at a random time on fire, firefighters ");
                msg += ("§7should extinguish the fire to get a reward.§8 §oN.B. Only admins should have access to this command\n");
                msg += ("§7You can change the wand with §o/fireset setwand§7 with the permission " + fireFighterClass.getPermission("set_wand") + "\n");
                msg += ("§c§l+ - §r§epage 1/4§c§l- - - - - - - - - - - - +§r");
                break;
            case "fireset2": //adding a mission page
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'fireset' Command\n");
                msg += (beforeCommand + "§e - How to add a mission - \n");
                msg += ("§e1)§7Select with the wand (§o/fireset§7) the area of the mission that will be set on fire\n");
                msg += ("§e2)§7Create a new mission with §o/fireset addmission <name> [description]\n");
                msg += (beforeCommand + "§ename §7The name that identifies the mission\n");
                msg += (beforeCommand + "§edescription §7The message that will be broadcasted\n");
                msg += ("§c§l+ - §r§epage 2/4§c§l- - - - - - - - - - - - +§r");
                break;
            case "fireset3": //editing a mission page
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'fireset' Command\n");
                msg += (beforeCommand + "§e - How to edit a mission - \n");
                msg += ("§7§o/fireset editmission <name> <name|description> <new value>\n");
                msg += (beforeCommand + "§ename §7The name that identifies the mission\n");
                msg += (beforeCommand + "§ename|description §7The parameter that will be modified\n");
                msg += (beforeCommand + "§enew value §7The new value of the parameter\n");
                msg += ("§c§l+ - §r§epage 3/4§c§l- - - - - - - - - - - - +§r");
                break;
            case "fireset4": //rewards page
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'fireset' Command\n");
                msg += (beforeCommand + "§e - How to add/edit rewards of a mission - \n");
                msg += ("§7§o/fireset editmission <name> rewards\n");
                msg += (beforeCommand + "§ename §7The name that identifies the mission\n");
                msg += (beforeCommand + "§7The GUI that contains rewards will open, ");
                msg += ("§7if there are no rewards set for that specific mission the GUI will be empty\n");
                msg += ("§c§l+ - §r§epage 4/4§c§l- - - - - - - - - - - - +§r");
            	break;
            case "firetool1":
            case "firetool": // FIRETOOL PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'firetool' Command\n");
                msg += (beforeCommand + "§eUsage: §7/firetool\n");
                msg += (beforeCommand + "§ePermission node: §7" + fireFighterClass.getPermission("firetool_get") + "\n");
                msg += (beforeCommand + "§eDescription: §7Gives a fire extinguisher to the player. The fire extinguisher ");
                msg += ("§7can be used to extinguish a fire and in the firefighter missions. Hold right click to use the fire extinguisher. ");
                msg += ("§7To use the fire extinguisher the player needs the permission " + fireFighterClass.getPermission("firetool_use") + "\n");
                msg += ("§c§l+ - - - - - - - - - - - - - - - - - +§r");
                break;
            case "firefighter1":
            case "com/github/tommyt0mmy/firefighter": // FIREFIGHTER PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'firefirghter' Command\n");
                msg += (beforeCommand + "§eUsage: §7/firefighter [command|page] ...\n");
                msg += (beforeCommand + "§ePermission node: §7" + fireFighterClass.getPermission("com/github/tommyt0mmy/firefighter") + "\n");
                msg += (beforeCommand + "§eDescription: §7Shows the help menu, pressing tab while typing §o/firefighter §r");
                msg += ("§7will autocomplete the command showing every existent page that you can have access to.\n");
                msg += ("§c§l+ - - - - - - - - - - - - - - - - - +§r");
                break;
            case "permissions1":
            case "permissions": // PERMISSIONS PAGE //
                msg += ("§c§l+ - - - §e§l§oFireFighter help§c§l - - - +§r\n");
                msg += (beforeParagraph + "§c'Permissions list\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("com/github/tommyt0mmy/firefighter") + "§e To execute the §7§o/firefighter§e command\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("firetool_get") + "§e To get a fire extinguisher (§7§o/firetool§e)\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("firetool_use") + "§e To utilize a fire extinguisher\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("firetool.freeze-durability") + "§e To freeze the usage of fire extinguishers\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("fireset") + "§e To execute the §7§o/fireset§e command (§oadd/edit/delete missions§7)\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("rewardset") + "§e To edit the rewards list of a mission\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("set_wand") + "§e To execute the §7§o/firetool setwand§e command\n");
                msg += (beforeCommand + "§7" + fireFighterClass.getPermission("onduty") + "§e To receive missions\n");
                msg += ("§c§l+ - - - - - - - - - - - - - - - - - +§r");
        }
        destination.sendMessage(msg);
    }
}