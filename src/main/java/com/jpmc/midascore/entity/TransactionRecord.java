package com.jpmc.midascore.entity;

import jakarta.persistence.*;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long recipientId;
    private float amount;

    private float incentive;

    public TransactionRecord() {}

    public TransactionRecord(Long senderId, Long recipientId, float amount,float incentiveAmount) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
        this.incentive = incentiveAmount;
    }

    // getters/setters
}
