package com.carmod.app.repository;

import com.carmod.app.entity.ModificationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModificationCategoryRepository extends JpaRepository<ModificationCategory, Long> {

    List<ModificationCategory> findByActiveTrue();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
