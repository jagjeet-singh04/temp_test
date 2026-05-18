package com.bnpp.assessment.models;

import javax.persistence.*;

@Entity
@Table(name = "option_strike")
@SequenceGenerator(
        name = "option_strike_seq",
        sequenceName =
                "option_strike_sequence",
        allocationSize = 1
)
public class OptionStrike {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "option_strike_seq"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_chain_id")
    private OptionChain optionChain;

    @Column(name = "strike_price")
    private Double strikePrice;

    @Column(name = "call_premium")
    private Double callPremium;

    @Column(name = "put_premium")
    private Double putPremium;

    @OneToOne(
            mappedBy = "optionStrike",
            cascade = CascadeType.ALL
    )
    private OptionDelta optionDelta;

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public OptionChain getOptionChain() {
        return optionChain;
    }

    public void setOptionChain(
            OptionChain optionChain
    ) {
        this.optionChain = optionChain;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(
            Double strikePrice
    ) {
        this.strikePrice = strikePrice;
    }

    public Double getCallPremium() {
        return callPremium;
    }

    public void setCallPremium(
            Double callPremium
    ) {
        this.callPremium = callPremium;
    }

    public Double getPutPremium() {
        return putPremium;
    }

    public void setPutPremium(
            Double putPremium
    ) {
        this.putPremium = putPremium;
    }

    public OptionDelta getOptionDelta() {
        return optionDelta;
    }

    public void setOptionDelta(
            OptionDelta optionDelta
    ) {
        this.optionDelta = optionDelta;
    }
}