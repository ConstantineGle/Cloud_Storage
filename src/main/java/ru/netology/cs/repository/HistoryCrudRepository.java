package ru.netology.cs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cs.model.history.History;

@Repository
public interface HistoryCrudRepository extends JpaRepository<History, Long> {
}