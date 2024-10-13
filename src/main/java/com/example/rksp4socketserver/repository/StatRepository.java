package com.example.rksp4socketserver.repository;


import com.example.rksp4socketserver.entity.MatchStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatRepository extends JpaRepository<MatchStats,Long> {
    MatchStats findStatById(Long id);
}
