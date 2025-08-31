package gg.desolve.melody.inventory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import gg.desolve.melody.common.Converter;
import gg.desolve.melody.common.Message;
import gg.desolve.melody.manager.ReportManager;
import gg.desolve.melody.model.Report;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

import static gg.desolve.melody.Melody.instance;

public class ReportInventory {

    public ReportInventory(Player viewer, Player target) {
        Gui gui = Gui.gui()
                .title(Component.text(Message.translateLegacy(
                        instance.getMessageConfig().report_gui.title
                                .replace("target%", target.getName()))))
                .rows(2)
                .create();

        gui.setDefaultClickAction(e -> e.setCancelled(true));

        gui.getFiller().fillTop(
                ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                        .name(Component.text(" "))
                        .asGuiItem());


        ReportManager reportManager = instance.getReportManager();

        AtomicInteger slot = new AtomicInteger(1);
        instance.getMessageConfig().report_gui.categories.stream()
                .limit(9)
                .forEach(category -> {
                    ItemBuilder item = ItemBuilder.from(Material.valueOf(category.material))
                            .name(Message.translate(category.display))
                            .lore(category.description.stream()
                                    .map(Message::translate)
                                    .toList());

                    gui.setItem(2, slot.getAndIncrement(), item.asGuiItem(e -> {
                        reportManager.create(new Report(
                                Converter.generateId(),
                                target.getUniqueId(),
                                target.getUniqueId(),
                                category.name
                        ));

                        viewer.closeInventory();
                        Message.sendMessage(viewer,
                                instance.getMessageConfig().report_created.replace("target%", target.getName()));
                    }));
                });

        gui.open(viewer);
    }
}
