package org.example.slaughterhouseparting.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PendingTrayRepository extends JpaRepository<PendingTray, UUID>
{
  List<PendingTray> findAllByOrderByCreatedAtAsc();
}
