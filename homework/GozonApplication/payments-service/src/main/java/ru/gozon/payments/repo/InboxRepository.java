package ru.gozon.payments.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gozon.payments.domain.InboxMessage;

import java.util.UUID;

public interface InboxRepository extends JpaRepository<InboxMessage, UUID> {}
