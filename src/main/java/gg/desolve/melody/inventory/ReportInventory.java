package gg.desolve.melody.inventory;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import gg.desolve.melody.common.Converter;
import gg.desolve.melody.common.Message;
import gg.desolve.melody.manager.ReportManager;
import gg.desolve.melody.model.Report;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

import static gg.desolve.melody.Melody.instance;

public class ReportInventory extends FastInv {

    public ReportInventory(ReportManager reportManager, Player target) {

        super(18, Message.translateLegacy(
                instance.getMessageConfig().report_gui.title
                        .replace("target%", target.getName())
        ));

        AtomicInteger slot = new AtomicInteger(9);
        instance.getMessageConfig().report_gui.categories.stream()
                .limit(9)
                .forEach(category -> {
                    ItemStack item = new ItemBuilder(Material.valueOf(category.material))
                            .name(Message.translateLegacy(category.display))
                            .lore(category.description.stream()
                                    .map(Message::translateLegacy)
                                    .toList())
                            .build();

                    setItem(slot.getAndIncrement(), item, e -> {
                        reportManager.create(
                                new Report(
                                        Converter.generateId(),
                                        e.getWhoClicked().getUniqueId(),
                                        target.getUniqueId(),
                                        category.name
                                ));

                        Player player = (Player) e.getWhoClicked();
                        player.closeInventory();

                        Message.sendMessage(player, "<green>A report has been submitted to staff.");
                    });
                });

        setItems(0, 9, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
    }
}
