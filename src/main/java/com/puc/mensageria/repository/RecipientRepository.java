package com.puc.mensageria.repository;

import com.puc.mensageria.domain.Recipient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    boolean existsByEmailIgnoreCase(String email);

    Optional<Recipient> findByEmailIgnoreCase(String email);
}
