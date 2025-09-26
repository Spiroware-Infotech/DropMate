package com.dropmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dropmate.entity.Settlement;
import com.dropmate.enums.PayoutStatus;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, String> {
	List<Settlement> findByUser_UserId(Long userId);

	List<Settlement> findByPayoutStatus(PayoutStatus status);
}
