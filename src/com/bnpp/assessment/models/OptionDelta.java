package com.bnpp.assessment.models;

import javax.persistence.*;

@Entity
@Table(name = "option_delta")
@SequenceGenerator(
        name = "option_delta_seq",
        sequenceName =
                "option_delta_sequence",
        allocationSize = 1
)
public class OptionDelta {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "option_delta_seq"
    )
    private Long id;

    @OneToOne
    @JoinColumn(name = "option_strike_id")
    private OptionStrike optionStrike;

    @Column(name = "call_delta")
    private Double callDelta;

    @Column(name = "put_delta")
    private Double putDelta;

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public OptionStrike getOptionStrike() {
        return optionStrike;
    }

    public void setOptionStrike(
            OptionStrike optionStrike
    ) {
        this.optionStrike = optionStrike;
    }

    public Double getCallDelta() {
        return callDelta;
    }

    public void setCallDelta(
            Double callDelta
    ) {
        this.callDelta = callDelta;
    }

    public Double getPutDelta() {
        return putDelta;
    }

    public void setPutDelta(
            Double putDelta
    ) {
        this.putDelta = putDelta;
    }
}