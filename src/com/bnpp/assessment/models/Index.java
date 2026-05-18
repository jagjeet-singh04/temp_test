package com.bnpp.assessment.models;

import javax.persistence.*;

@Entity
@Table(name = "indices")
@SequenceGenerator(
        name = "index_seq",
        sequenceName = "index_sequence",
        allocationSize = 1
)
public class Index {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "index_seq"
    )
    private Long id;

    @Column(unique = true)
    private String symbol;

    @Column(name = "last_price")
    private Double lastPrice;

    @OneToOne(
            mappedBy = "index",
            cascade = CascadeType.ALL
    )
    private OptionChain optionChain;

    // =========================
    // GETTERS & SETTERS
    // =========================

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public OptionChain getOptionChain() {
        return optionChain;
    }

    public void setOptionChain(
            OptionChain optionChain
    ) {
        this.optionChain = optionChain;
    }
}