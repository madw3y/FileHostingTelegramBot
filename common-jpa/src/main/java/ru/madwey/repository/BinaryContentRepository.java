package ru.madwey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.madwey.entity.BinaryContent;

@Repository
public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
}
