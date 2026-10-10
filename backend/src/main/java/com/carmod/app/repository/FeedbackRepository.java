package com.carmod.app.repository;

import com.carmod.app.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByRequestId(Long requestId);

    boolean existsByRequestId(Long requestId);

    List<Feedback> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Page<Feedback> findAll(Pageable pageable);

    @Query("SELECT AVG(f.rating) FROM Feedback f")
    Double calculateAverageRating();
}
