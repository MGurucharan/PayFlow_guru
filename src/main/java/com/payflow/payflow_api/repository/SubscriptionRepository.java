package com.payflow.payflow_api.repository;

import com.payflow.payflow_api.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
}
