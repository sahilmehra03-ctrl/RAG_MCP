package com.example.support.repository;

import com.example.support.domain.Ticket;
import com.example.support.domain.TicketStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByPublicId(String publicId);

    List<Ticket> findByStatus(TicketStatus status);

    @Query("""
            SELECT t FROM Ticket t
            WHERE (:status IS NULL OR t.status = :status)
            AND (
                :q IS NULL OR :q = '' OR
                LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(t.publicId) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            ORDER BY t.updatedAt DESC
            """)
    List<Ticket> search(@Param("status") TicketStatus status, @Param("q") String q);
}
