package com.carmod.app.service;

import com.carmod.app.entity.Feedback;
import com.carmod.app.entity.ModificationRequest;
import com.carmod.app.entity.ModificationRequest.RequestStatus;
import com.carmod.app.entity.User;
import com.carmod.app.repository.FeedbackRepository;
import com.carmod.app.repository.ModificationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {

    @Autowired private FeedbackRepository feedbackRepository;
    @Autowired private ModificationRequestRepository requestRepository;

    public Feedback submitFeedback(Long requestId, Long customerId, Integer rating, String comments) {
        ModificationRequest request = requestRepository.findByIdAndCustomerId(requestId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found or access denied"));

        if (request.getStatus() != RequestStatus.COMPLETED) {
            throw new IllegalStateException("Feedback can only be submitted for completed requests");
        }

        if (feedbackRepository.existsByRequestId(requestId)) {
            throw new IllegalStateException("Feedback has already been submitted for this request");
        }

        Feedback feedback = new Feedback();
        feedback.setCustomer(request.getCustomer());
        feedback.setRequest(request);
        feedback.setRating(rating);
        feedback.setComments(comments);

        return feedbackRepository.save(feedback);
    }

    @Transactional(readOnly = true)
    public Optional<Feedback> findByRequestId(Long requestId) {
        return feedbackRepository.findByRequestId(requestId);
    }

    @Transactional(readOnly = true)
    public List<Feedback> getCustomerFeedbacks(Long customerId) {
        return feedbackRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional(readOnly = true)
    public Page<Feedback> getAllFeedbacks(Pageable pageable) {
        return feedbackRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Double getAverageRating() {
        return feedbackRepository.calculateAverageRating();
    }

    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }
}
