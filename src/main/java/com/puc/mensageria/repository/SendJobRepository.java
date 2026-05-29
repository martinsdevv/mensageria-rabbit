package com.puc.mensageria.repository;

import com.puc.mensageria.domain.SendJob;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SendJobRepository extends JpaRepository<SendJob, Long> {

    @Query("SELECT j FROM SendJob j JOIN FETCH j.message ORDER BY j.createdAt DESC")
    List<SendJob> findAllWithMessageOrderByCreatedAtDesc();

    @Query("SELECT j FROM SendJob j JOIN FETCH j.message WHERE j.id = :id")
    Optional<SendJob> findByIdWithMessage(Long id);
}
