package com.dropmate.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.dropmate.enums.PayoutStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "settlements")
public class Settlement {

	@Id
	@Column(columnDefinition = "CHAR(36)")
	private String id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "period_start")
	private LocalDateTime periodStart;

	@Column(name = "period_end")
	private LocalDateTime periodEnd;

	@Column(name = "total_earnings", precision = 12, scale = 2)
	private BigDecimal totalEarnings;

	@Column(name = "platform_fee", precision = 12, scale = 2)
	private BigDecimal platformFee;

	@Column(name = "payout_amount", precision = 12, scale = 2)
	private BigDecimal payoutAmount;

	@Enumerated(EnumType.STRING)
	private PayoutStatus payoutStatus = PayoutStatus.PENDING;

	@Column(columnDefinition = "TEXT")
	private String payoutReference;

	@CreationTimestamp
	private Timestamp createdAt;
}
