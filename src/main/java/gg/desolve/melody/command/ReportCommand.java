package gg.desolve.melody.command;

import gg.desolve.melody.inventory.ReportInventory;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;

public class ReportCommand {

    @Command("report")
    public void report(Player sender, @Named("player") Player target) {
        new ReportInventory(target).open(sender);
    }

}
