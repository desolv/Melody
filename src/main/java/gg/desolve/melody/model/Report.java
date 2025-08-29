package gg.desolve.melody.model;

import lombok.Data;
import org.bson.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
public class Report {

    private String id;
    private UUID reporter;
    private UUID target;
    private String reason;
    private Instant createdAt;
    private String server;
    private ReportState state;
    private UUID handledBy;
    private Instant handledAt;

    public Report(String id, UUID reporter, UUID target, String reason, Instant createdAt, String server, ReportState state, UUID handledBy, Instant handledAt) {
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
        Duration duration = Duration.ofMinutes(1);

        if (this.createdAt == null)
            return true;

        return Instant.now().isAfter(this.createdAt.plus(duration));
    }

    public static Document toDocument(Report report) {
        Document document = new Document();
        document.put("_id", report.getId());
        document.put("reporter", report.getReporter());
        document.put("target", report.getTarget());
        document.put("reason", report.getReason());
        document.put("createdAt", report.getCreatedAt());
        document.put("server", report.getServer());
        document.put("state", report.getState().name());
        document.put("handledBy", report.getHandledBy());
        document.put("handledAt", report.getHandledAt());
        return document;
    }

    public static Report fromDocument(Document document) {
        return new Report(
                document.getString("_id"),
                UUID.fromString(document.getString("reporter")),
                UUID.fromString(document.getString("target")),
                document.getString("reason"),
                Instant.parse(document.getString("createdAt")),
                document.getString("server"),
                ReportState.valueOf(document.getString("state")),
                UUID.fromString(document.getString("handledBy")),
                Instant.parse(document.getString("handledAt"))
        );
    }
}
