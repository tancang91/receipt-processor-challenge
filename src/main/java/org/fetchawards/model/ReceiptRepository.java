package org.fetchawards.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Receipt findByUuid(String uuid);
}
