package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, String> {
    List<Rating> findByRater_UserId(Long userId);
    List<Rating> findByRated_UserId(Long userId);
}
