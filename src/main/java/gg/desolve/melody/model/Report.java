package gg.desolve.melody.model;

import lombok.Data;

import java.time.Duration;
import java.util.UUID;

@Data
public class Report {

    private String id;
    private UUID reporter;
    private UUID reported;
    private String reason;
    private Duration timestamp;
    private String server;
    private ReportType status;
    private UUID handled_by;

    public Report(String id, UUID reporter, UUID reported, String reason, Duration timestamp, String server, ReportType status, UUID handled_by) {
        this.id = id;
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.timestamp = timestamp;
        this.server = server;
        this.status = status;
        this.handled_by = handled_by;
    }
}
