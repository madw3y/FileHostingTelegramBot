package ru.madwey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.madwey.entity.RawData;

@Repository
public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
