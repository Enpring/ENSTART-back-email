package com.example.enstart.repository;

import com.example.enstart.model.Subscription;
import com.example.enstart.model.SubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionId> {
}
