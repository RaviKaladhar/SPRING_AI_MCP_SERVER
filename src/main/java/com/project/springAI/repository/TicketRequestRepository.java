package com.project.springAI.repository;

import com.project.springAI.entity.TicketRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRequestRepository extends JpaRepository<TicketRequest, UUID> {
    List<TicketRequest> findTicketsByUserName(String username);
}
