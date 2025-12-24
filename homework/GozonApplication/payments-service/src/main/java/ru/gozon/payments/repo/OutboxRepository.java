package ru.gozon.payments.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.gozon.payments.outbox.OutboxMessage;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    @Query(value = "select * from outbox where sent_at is null order by created_at limit :limit for update skip locked",
           nativeQuery = true)
    List<OutboxMessage> lockNextUnsent(@Param("limit") int limit);
}
