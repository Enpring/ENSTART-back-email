package com.example.enstart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription {

    @EmbeddedId
    private SubscriptionId id;

    @ManyToOne
    @MapsId("subscriberId")
    @JoinColumn(name = "subscriber_id")
    private Subscriber subscriber;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private Service service;

    @Column(nullable = false)
    private String status = "active";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;
}
