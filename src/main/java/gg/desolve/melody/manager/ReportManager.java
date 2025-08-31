package gg.desolve.melody.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import gg.desolve.melody.model.Report;
import gg.desolve.melody.model.ReportState;
import lombok.Getter;
import org.bson.Document;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static gg.desolve.melody.Melody.instance;

public class ReportManager {

    @Getter
    private final MongoCollection<Document> reportCollection = instance.getMongoManager().getMongoDatabase().getCollection("reports");

    public void create(Report report) {
        RedisManager redisManager = instance.getRedisManager();

        redisManager.withRedisAsync(j -> j.set("report:" + report.getId(), Report.toJson(report)));
        redisManager.publishAsync("report:create", report.getId());

        reportCollection.replaceOne(eq("_id", report.getId()), Report.toDocument(report), new ReplaceOptions().upsert(true));
    }

    public void resolve(Report report, UUID handledBy) {
        report.setState(ReportState.RESOLVED);
        report.setHandledBy(handledBy);
        report.setHandledAt(Instant.now());

        instance.getRedisManager().publishAsync("report:resolve", report.getId());
        reportCollection.replaceOne(eq("_id", report.getId()), Report.toDocument(report), new ReplaceOptions().upsert(true));
    }

    public void expire(Report report) {
        report.setState(ReportState.EXPIRED);
        report.setHandledAt(Instant.now());

        delete(report.getId());
        reportCollection.replaceOne(eq("_id", report.getId()), Report.toDocument(report), new ReplaceOptions().upsert(true));
    }

    public CompletableFuture<Report> get(String id) {
        return instance.getRedisManager().withRedisAsync(j -> j.get("report:" + id))
                .thenApply(json -> {
                    if (json == null) return null;
                    return Report.fromDocument(Document.parse(json));
                });
    }

    public CompletableFuture<List<Report>> getAll() {
        return instance.getRedisManager().withRedisAsync(j -> {
            Set<String> keys = j.keys("report:*");

            List<Report> reports = keys.stream()
                    .map(j::get)
                    .filter(Objects::nonNull)
                    .map(Document::parse)
                    .map(Report::fromDocument)
                    .toList();

            reports.stream()
                    .filter(Objects::nonNull)
                    .filter(Report::isExpired)
                    .forEach(this::expire);

            return reports.stream()
                    .filter(Objects::nonNull)
                    .filter(report -> !report.isExpired())
                    .collect(Collectors.toList());
        });
    }

    public CompletableFuture<List<Report>> getReportsByTarget(UUID target) {
        return getAll().thenApply(list ->
                list.stream()
                        .filter(report -> report.getTarget() != null && report.getTarget().equals(target))
                        .collect(Collectors.toList())
        );
    }

    public CompletableFuture<List<Report>> getReportsByReporter(UUID reporter) {
        return getAll().thenApply(list ->
                list.stream()
                        .filter(report -> report.getReporter() != null && report.getReporter().equals(reporter))
                        .collect(Collectors.toList())
        );
    }

    public void delete(String id) {
        instance.getRedisManager()
                .withRedisAsync(j -> j.del("report:" + id))
                .thenApply(result -> result > 0);
    }

}
