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
            case "report:expire" -> onExpire(message);
            default -> {}
        }
    }

    private void onCreate(String reportId) {
        reportManager.get(reportId).thenAccept(report -> {
            if (report == null) return;

            String targetName = Converter.getBestName(report.getTarget());
            String reporterName = Converter.getBestName(report.getReporter());

            String message = "<yellow>" + targetName + " <red>has been reported by <yellow>" + reporterName + " <red>for <gold>" + report.getReason() + "<red>.";
            Bukkit.getScheduler().runTask(instance, () -> broadcastStaff(message));
        });
    }

    private void onResolve(String reportId) {
        reportManager.get(reportId).thenAccept(report -> {
            if (report == null) return;

            Player player = Bukkit.getPlayer(report.getReporter());

            if  (player != null) {
                String message = "<green>A report against a player you reported has been resolved.";
                Bukkit.getScheduler().runTask(instance, () -> Message.sendMessage(player, message));
            }
        });

        reportManager.delete(reportId);
    }

    private void onExpire(String reportId) {
        reportManager.get(reportId).thenAccept(report -> {
            if (report == null) return;

            String targetName = Converter.getBestName(report.getTarget());
            String message = "<red>A report has expired that was made against <yellow>" + targetName + "<red>.";

            Bukkit.getScheduler().runTask(instance, () -> broadcastStaff(message));
        });

        reportManager.delete(reportId);
    }

    private void broadcastStaff(String message) {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("melody.staff"))
                .forEach(player -> Message.sendMessage(player, message));
    }
}
