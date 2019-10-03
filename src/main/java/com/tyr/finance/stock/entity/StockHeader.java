package com.tyr.finance.stock.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name="stock_header")
public class StockHeader implements Serializable {
    private static final long serialVersionUID = -3658128927396776613L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer oid;
    @Column(name="code")
    private String code;
    @Column(name="name")
    private String name;
}
