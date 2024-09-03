package com.rey.modular.payment.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "tr_order")
@Setter
@Getter
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "payer")
    private Integer payer;

    @Column(name = "payee")
    private Integer payee;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

}
