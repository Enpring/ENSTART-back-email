package com.example.enstart.repository;

import com.example.enstart.model.Homepage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HomepageRepository extends JpaRepository<Homepage, Integer> {
    Optional<Homepage> findByDomain(String domain);
}
