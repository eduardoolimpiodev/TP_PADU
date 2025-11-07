package com.userprocessor.repository;

import com.userprocessor.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findBySource(String source);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.source = :source")
    Page<User> findBySource(@Param("source") String source, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.source = :source")
    long countBySource(@Param("source") String source);

    @Query("SELECT u.source, COUNT(u) FROM User u GROUP BY u.source")
    List<Object[]> countUsersBySource();
}
