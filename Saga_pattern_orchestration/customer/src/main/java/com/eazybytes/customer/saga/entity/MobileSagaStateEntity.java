package com.eazybytes.customer.saga.entity;

import com.eazybytes.customer.saga.state.MobileSagaState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mobile_saga_state")
@Getter
@Setter
public class MobileSagaStateEntity {

    @Id
    private String sagaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MobileSagaState state;

    @Column(nullable = false)
    private String currentMobileNumber;

    @Column(nullable = false)
    private String newMobileNumber;

    // Which services successfully UPDATED (so we know what to rollback)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mobile_saga_updated_steps", joinColumns = @JoinColumn(name = "saga_id"))
    @Column(name = "service_name")
    private Set<String> updatedSteps = new HashSet<>();

    private String error;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}