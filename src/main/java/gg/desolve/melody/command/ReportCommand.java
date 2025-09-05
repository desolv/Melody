package gg.desolve.melody.command;

import gg.desolve.melody.common.Message;
import gg.desolve.melody.inventory.ReportInventory;
import gg.desolve.melody.manager.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

import static gg.desolve.melody.Melody.instance;

public class ReportCommand {

    @Dependency
    private ReportManager reportManager;

    @Command("report")
    @CommandPermission("melody.report")
    public void report(Player sender, @Named("player") Player target) {
        if (sender == target) {
            Message.sendMessage(sender, instance.getMessageConfig().report_self);
            return;
        }

        reportManager.getReportsByReporter(target.getUniqueId()).thenAccept(reports -> {
            boolean hasActiveCooldown = reports.stream()
                    .filter(Objects::nonNull)
                    .filter(report -> report.getTarget().equals(target.getUniqueId()))
                    .anyMatch(report -> !report.isExpired());

            if (hasActiveCooldown) {
                Bukkit.getScheduler().runTask(instance, () ->
                        Message.sendMessage(sender, instance.getMessageConfig().report_cooldown)
                );
                return;
            }

            Bukkit.getScheduler().runTask(instance, () -> new ReportInventory(sender, target));
        });

    }

}
