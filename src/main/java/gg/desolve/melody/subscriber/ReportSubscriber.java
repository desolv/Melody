package gg.desolve.melody.subscriber;

import gg.desolve.melody.common.Converter;
import gg.desolve.melody.common.Message;
import gg.desolve.melody.manager.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import static gg.desolve.melody.Melody.instance;

public class ReportSubscriber extends JedisPubSub {

    private final ReportManager reportManager;

    public ReportSubscriber(ReportManager reportManager) {
        this.reportManager = reportManager;
    }

    @Override
    public void onMessage(String channel, String message) {
        switch (channel) {
            case "report:create" -> onCreate(message);
            case "report:resolve" -> onResolve(message);
            default -> {}
        }
    }

    private void onCreate(String reportId) {
        reportManager.get(reportId).thenAccept(report -> {
            if (report == null) return;

            String targetName = Converter.getBestName(report.getTarget());
            String reporterName = Converter.getBestName(report.getReporter());

            String message = instance.getMessageConfig().report_created_staff
                    .replace("target%", targetName)
                    .replace("reporter%", reporterName)
                    .replace("reason%", report.getReason());
            Bukkit.getScheduler().runTask(instance, () -> broadcastStaff(message));
        });
    }

    private void onResolve(String reportId) {
        reportManager.get(reportId).thenAccept(report -> {
            if (report == null) return;

            Player player = Bukkit.getPlayer(report.getReporter());

            if  (player != null) {
                String message = instance.getMessageConfig().report_resolved;
                Bukkit.getScheduler().runTask(instance, () -> Message.sendMessage(player, message));
            }
        });

        reportManager.delete(reportId);
    }

    private void broadcastStaff(String message) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("melody.staff"))
                .forEach(player -> Message.sendMessage(player, message));
    }
}
