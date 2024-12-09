package com.msa.hub.domain.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_hub_route")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubRoute extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    @Column(name="id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_hub_id")
    private Hub sourceHubId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_hub_id")
    private Hub destinationHubId;

    @Column(name="distance", nullable=false)
    private Double distance;

    @Column(name="duration", nullable=false)
    private Long duration;

    @Builder(access = AccessLevel.PRIVATE)
    private HubRoute(Hub sourceHubId, Hub destinationHubId, Double distance, Long duration) {
        this.sourceHubId = sourceHubId;
        this.destinationHubId = destinationHubId;
        this.distance = distance;
        this.duration = duration;
    }
}
