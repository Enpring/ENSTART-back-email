package com.example.enstart.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class SubscriptionId implements Serializable {

    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Column(name = "service_id")
    private Long serviceId;
}
