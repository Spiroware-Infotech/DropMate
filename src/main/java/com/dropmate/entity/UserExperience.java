package com.dropmate.entity;

import java.time.LocalDateTime;

import com.dropmate.enums.ExperienceLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_experience_level")
public class UserExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to your main User entity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false, unique = true)
    private DriverProfile driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", length = 50, nullable = false)
    private ExperienceLevel experienceLevel = ExperienceLevel.NEWCOMER;

    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "rides_completed", nullable = false)
    private Integer ridesCompleted = 0;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public UserExperience(DriverProfile driver) {
        this.driver = driver;
        this.experienceLevel = ExperienceLevel.NEWCOMER;
        this.totalPoints = 0;
        this.ridesCompleted = 0;
        this.averageRating = 0.0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // === Utility Methods ===
    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
