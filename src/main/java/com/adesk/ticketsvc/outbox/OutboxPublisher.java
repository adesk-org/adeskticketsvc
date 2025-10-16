package com.adesk.ticketsvc.outbox;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OutboxPublisher {

    private final KafkaTemplate<String, Object> kafka;
    private final OutboxRepository repo;

    @Scheduled(fixedDelayString = "PT1S")
    @Transactional
    public void publishUnsent() {
        List<OutboxEntity> batch = repo.findUnpublished(PageRequest.of(0, 200));
        for (OutboxEntity o : batch) {
            try {
                ProducerRecord<String, Object> record =
                        new ProducerRecord<>(o.getTopic(), o.getRecordKey(), o.getPayload());
                record.headers().add("x-tenant-id",
                        o.getTenantId().toString().getBytes(StandardCharsets.UTF_8));
                kafka.send(record).join();
                repo.markPublished(o.getId());
            } catch (CompletionException e) {
                log.error("Failed to publish outbox {}", o.getId(), e);
            }
        }
    }
}
