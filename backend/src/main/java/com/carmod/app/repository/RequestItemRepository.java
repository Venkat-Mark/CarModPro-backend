package com.carmod.app.repository;

import com.carmod.app.entity.RequestItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {

    List<RequestItem> findByModificationRequestId(Long requestId);
}
