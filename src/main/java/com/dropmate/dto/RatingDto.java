package com.dropmate.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {

	private Long id;
	private Long rideId;
	private Long raterId;
	private Long ratedUserId;
	private double ratingValue;
	private String comment;
	private LocalDateTime createdAt;

	// Optional fields for display
	private String raterName;
	private String ratedUserName;
	private String rideSource;
	private String rideDestination;

}
