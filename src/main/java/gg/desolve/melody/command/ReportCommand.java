package gg.desolve.melody.command;

import gg.desolve.melody.common.Message;
import gg.desolve.melody.inventory.ReportInventory;
import gg.desolve.melody.manager.ReportManager;
import gg.desolve.melody.model.Report;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Named;

import java.util.Objects;

import static gg.desolve.melody.Melody.instance;

public class ReportCommand {

    @Dependency
    private ReportManager  reportManager;

    @Command("report")
    public void report(Player sender, @Named("player") Player target) {
        reportManager.getReportsByReporter(target.getUniqueId()).thenAccept(reports -> {
            boolean hasActiveCooldown = reports.stream()
                    .filter(Objects::nonNull)
                    .filter(report -> report.getTarget().equals(target.getUniqueId()))
                    .anyMatch(report -> !report.isExpired());

            if (hasActiveCooldown) {
                Bukkit.getScheduler().runTask(instance, () ->
                        Message.sendMessage(sender, "<red>Please wait 5 minutes before reporting this player again.")
                );
                return;
            }

            // Move to its own method - that runs every x minutes for all global reports
            reports.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> r.getTarget().equals(target.getUniqueId()))
                    .filter(Report::isExpired)
                    .forEach(reportManager::expire);

            Bukkit.getScheduler().runTask(instance, () ->
                    new ReportInventory(instance.getReportManager(), target).open(sender)
            );
        });

    }

}
