package com.dropmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
	
	@Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.ratedTo.userId = :userId")
	Double findAverageRatingByUserId(@Param("userId") Long userId);

	int countByRatedToUserId(Long userId);
}
