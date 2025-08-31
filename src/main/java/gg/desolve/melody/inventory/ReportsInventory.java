package gg.desolve.melody.inventory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import gg.desolve.melody.common.Converter;
import gg.desolve.melody.common.Message;
import gg.desolve.melody.manager.ReportManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.stream.Stream;

import static gg.desolve.melody.Melody.instance;

public class ReportsInventory {

    public ReportsInventory(Player viewer) {
        PaginatedGui paginatedGui = Gui.paginated()
                .title(Message.translate(instance.getMessageConfig().reports_gui.title))
                .rows(3)
                .pageSize(18)
                .create();

        paginatedGui.setDefaultClickAction(e -> e.setCancelled(true));

        paginatedGui.getFiller().fillTop(
                ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                        .name(Component.text(" "))
                        .asGuiItem());

        ReportManager reportManager = instance.getReportManager();

        reportManager.getAll()
                .thenApply(reports -> reports.stream().map(report ->
                        ItemBuilder.from(Material.PAPER)
                                .name(Message.translate(instance.getMessageConfig().reports_gui.name.replace("id%", report.getId())))
                                .lore(instance.getMessageConfig().reports_gui.lore.stream()
                                        .map(line -> line
                                                        .replace("id%", report.getId())
                                                        .replace("time%", Converter.formatInstant(report.getCreatedAt()))
                                                        .replace("reporter%", Converter.getBestName(report.getReporter()))
                                                        .replace("reported%", Converter.getBestName(report.getTarget()))
                                                        .replace("reason%", report.getReason())
                                                        .replace("server%", report.getServer()))
                                        .map(Message::translate)
                                        .toList()).asGuiItem(e -> {
                                            reportManager.resolve(report, viewer.getUniqueId());

                                            Message.sendMessage(viewer, instance.getMessageConfig().report_resolved_staff.replace("id%", report.getId()));
                                            new ReportsInventory(viewer);
                                        }))
                        .toList())
                .thenAccept(items -> Bukkit.getScheduler().runTask(instance, () -> {
                    items.forEach(paginatedGui::addItem);
                    paginatedGui.open(viewer);

                }))
                .exceptionally(ex -> {
                    Bukkit.getScheduler().runTask(instance, () ->
                            viewer.sendMessage(Message.translateLegacy("<red>Failed to load reports.")));
                    ex.getMessage();
                    return null;
                });
    }
}
