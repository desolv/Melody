package gg.desolve.melody.command;

import gg.desolve.melody.inventory.ReportsInventory;
import gg.desolve.melody.manager.ReportManager;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;

public class ReportsCommand {

    @Dependency
    private ReportManager reportManager;

    @Command("reports")
    public void reports(Player sender) {
        new ReportsInventory(ReportsInventory.ReportSortBy.NEWEST_FIRST, sender);
    }

}
