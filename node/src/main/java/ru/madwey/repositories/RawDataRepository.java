package ru.madwey.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.madwey.entities.RawData;

@Repository
public interface RawDataRepository extends JpaRepository<RawData, Long> {
}
