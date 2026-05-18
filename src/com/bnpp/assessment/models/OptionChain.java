package com.bnpp.assessment.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "option_chain")
@SequenceGenerator(
        name = "option_chain_seq",
        sequenceName =
                "option_chain_sequence",
        allocationSize = 1
)
public class OptionChain {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "option_chain_seq"
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "index_id")
    private Index index;

    @Column(name = "spot_price")
    private Double spotPrice;

    @OneToMany(
            mappedBy = "optionChain",
            cascade = CascadeType.ALL
    )
    private List<OptionStrike> strikes =
            new ArrayList<>();

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public Double getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(
            Double spotPrice
    ) {
        this.spotPrice = spotPrice;
    }

    public List<OptionStrike> getStrikes() {
        return strikes;
    }

    public void setStrikes(
            List<OptionStrike> strikes
    ) {
        this.strikes = strikes;
    }
}