package com.example.enstart.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "homepages")
@Getter
@Setter
public class Homepage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String domain;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
}
