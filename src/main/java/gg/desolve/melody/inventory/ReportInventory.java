package gg.desolve.melody.inventory;

import fr.mrmicky.fastinv.FastInv;
import fr.mrmicky.fastinv.ItemBuilder;
import gg.desolve.melody.common.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

import static gg.desolve.melody.Melody.instance;

public class ReportInventory extends FastInv {

    public ReportInventory(Player target) {
        super(18, Message.translateLegacy(
                instance.getMelodyConfig().report.title
                        .replace("target%", target.getName())
        ));

        AtomicInteger slot = new AtomicInteger(9);
        instance.getMelodyConfig().report.categories.stream()
                .limit(9)
                .forEach(category -> {
                    ItemStack item = new ItemBuilder(Material.valueOf(category.material))
                            .name(Message.translateLegacy(category.display))
                            .lore(category.description.stream()
                                    .map(Message::translateLegacy)
                                    .toList())
                            .build();

                    setItem(slot.getAndIncrement(), item);
                });

        setItems(0, 9, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
    }
}
