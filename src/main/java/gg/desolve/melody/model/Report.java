package gg.desolve.melody.model;

import lombok.Data;
import org.bson.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static gg.desolve.melody.Melody.instance;

@Data
public class Report {

    private String id;
    private UUID reporter;
    private UUID target;
    private String reason;
    private Instant createdAt;
    private String server;
    private ReportType state;
    private UUID handledBy;
    private Instant handledAt;

    public Report(String id, UUID reporter, UUID target, String reason) {
        this.id = id;
        this.reporter = reporter;
        this.target = target;
        this.reason = reason;
        this.createdAt = Instant.now();
        this.server = instance.getMelodyConfig().server_name;
        this.state = ReportType.ACTIVE;
    }

    public Report(String id, UUID reporter, UUID target, String reason, Instant createdAt, String server, ReportType state, UUID handledBy, Instant handledAt) {
        this.id = id;
        this.reporter = reporter;
        this.target = target;
        this.reason = reason;
        this.createdAt = createdAt;
        this.server = server;
        this.state = state;
        this.handledBy = handledBy;
        this.handledAt = handledAt;
    }

    public boolean isExpired() {
        Duration duration = Duration.ofMinutes(5);

        if (this.createdAt == null)
            return true;

        return Instant.now().isAfter(this.createdAt.plus(duration));
    }

    public static Document toDocument(Report report) {
        return new Document()
                .append("_id", report.getId())
                .append("reporter", report.getReporter())
                .append("target", report.getTarget())
                .append("reason", report.getReason())
                .append("createdAt", report.getCreatedAt())
                .append("server", report.getServer())
                .append("state", report.getState().name())
                .append("handledBy", report.getHandledBy())
                .append("handledAt", report.getHandledAt());
    }

    public static String toJson(Report report) {
        return new Document()
                .append("_id", report.getId())
                .append("reporter", report.getReporter())
                .append("target", report.getTarget())
                .append("reason", report.getReason())
                .append("createdAt", report.getCreatedAt().toEpochMilli())
                .append("server", report.getServer())
                .append("state", report.getState().name())
                .append("handledBy", report.getHandledBy())
                .append("handledAt", report.getHandledAt())
                .toJson();
    }

    public static Report fromDocument(Document document) {
        return new Report(
                document.getString("_id"),
                document.get("reporter", UUID.class),
                document.get("target", UUID.class),
                document.getString("reason"),
                toInstant(document.get("createdAt")),
                document.getString("server"),
                ReportType.valueOf(document.getString("state")),
                document.get("handledBy", UUID.class),
                toInstant(document.get("handledAt"))
        );
    }

    private static Instant toInstant(Object object) {
        if (object == null) return null;

        if (object instanceof Instant i) return i;
        if (object instanceof Date d) return d.toInstant();
        if (object instanceof Number n) return Instant.ofEpochMilli(n.longValue());

        try {
            return Instant.parse(object.toString());
        } catch (Exception ex) {
            return null;
        }
    }
}
