package gg.desolve.melody.inventory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import gg.desolve.melody.common.Converter;
import gg.desolve.melody.common.EnumCycle;
import gg.desolve.melody.common.Message;
import gg.desolve.melody.model.Report;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static gg.desolve.melody.Melody.instance;

public class ReportsInventory {

    public ReportsInventory(ReportSortBy sortBy, Player viewer) {
        PaginatedGui paginatedGui = Gui.paginated()
                .title(Message.translate(
                        instance.getMessageConfig().reports_gui.title
                                .replace("total%", "<gray>Loading...")
                ))
                .rows(3)
                .create();

        paginatedGui.setDefaultClickAction(e -> e.setCancelled(true));

        GuiItem AIR = ItemBuilder.from(Material.AIR).asGuiItem();
        GuiItem GLASS_PANE = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .name(Component.text(" "))
                .asGuiItem();

        paginatedGui.getFiller().fillBetweenPoints(1, 2, 3, 2, GLASS_PANE);
        paginatedGui.getFiller().fillBetweenPoints(1, 8, 3, 8, GLASS_PANE);

        paginatedGui.setItem(1, 1, AIR);
        paginatedGui.setItem(3, 1, AIR);

        paginatedGui.setItem(
                2, 9,
                ItemBuilder.from(Material.MELON_SLICE)
                        .name(Message.translate("<green>Scroll Up"))
                        .asGuiItem(_ -> paginatedGui.previous()));

        paginatedGui.setItem(
                3, 9,
                ItemBuilder.from(Material.MELON_SLICE)
                        .name(Message.translate("<green>Scroll Down"))
                        .asGuiItem(_ -> paginatedGui.next()));

        paginatedGui.setItem(2, 1,
                ItemBuilder.from(Material.HOPPER)
                        .name(Message.translate("<green>Sort By"))
                        .lore(Stream.of(
                                (sortBy == ReportSortBy.NEWEST_FIRST ? "<green><bold>" : "<gray>") + "➜ Newest",
                                        (sortBy == ReportSortBy.OLDEST_FIRST ? "<green><bold>" : "<gray>") + "➜ Oldest",
                                        "<gray>",
                                        "<yellow>Click switch between options!"
                                ).map(Message::translate)
                                .toList())
                        .asGuiItem(_ -> new ReportsInventory(EnumCycle.next(ReportSortBy.class, sortBy), viewer)));

        AtomicInteger reportCounter = new AtomicInteger();
        instance.getReportManager().getAll()
                .thenApply(reports -> reports.stream()
                        .sorted(switch (sortBy) {
                            case OLDEST_FIRST -> Comparator.comparing(Report::getCreatedAt);
                            default -> Comparator.comparing(Report::getCreatedAt).reversed();
                        })
                        .map(report -> {
                            reportCounter.getAndIncrement();
                            return ItemBuilder.from(Material.PAPER)
                                    .name(Message.translate(instance.getMessageConfig().reports_gui.name.replace("id%", report.getId())))
                                    .lore(instance.getMessageConfig().reports_gui.lore.stream()
                                            .map(desc -> desc
                                                            .replace("id%", report.getId())
                                                            .replace("time%", Converter.formatInstant(report.getCreatedAt()))
                                                            .replace("reporter%", Converter.getBestName(report.getReporter()))
                                                            .replace("reported%", Converter.getBestName(report.getTarget()))
                                                            .replace("reason%", report.getReason())
                                                            .replace("server%", report.getServer()))
                                            .map(Message::translate)
                                            .toList()).asGuiItem(_ -> {
                                                instance.getReportManager().resolve(report, viewer.getUniqueId());

                                                Message.sendMessage(viewer, instance.getMessageConfig().report_resolved_staff.replace("id%", report.getId()));
                                                new ReportsInventory(sortBy, viewer);
                                            });
                        })
                        .toList())
                .thenAccept(items -> Bukkit.getScheduler().runTask(instance, () -> {
                    items.forEach(paginatedGui::addItem);

                    paginatedGui.updateTitle(Message.translateLegacy(
                            instance.getMessageConfig().reports_gui.title
                                    .replace("total%", String.valueOf(reportCounter.get()))
                    ));

                    paginatedGui.setItem(
                            1, 9,
                            ItemBuilder.from(Material.COD)
                                    .name(Message.translate("<green>Metadata"))
                                    .lore(Stream.of(
                                                    "<gray>Current Page: <white>" + paginatedGui.getCurrentPageNum(),
                                                    "<gray>Total Pages: <white>" + paginatedGui.getPagesNum(),
                                                    "<gray>Reports: <white>" + reportCounter.get()
                                            ).map(Message::translate)
                                            .toList())
                                    .asGuiItem(_ -> paginatedGui.previous()));

                    paginatedGui.open(viewer);
                }))
                .exceptionally(ex -> {
                    Bukkit.getScheduler().runTask(instance, () ->
                            viewer.sendMessage(Message.translateLegacy("<red>Failed to load reports.")));
                    ex.getMessage();
                    return null;
                });
    }

    public enum ReportSortBy {
        NEWEST_FIRST,
        OLDEST_FIRST
    }
}
