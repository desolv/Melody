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
}
