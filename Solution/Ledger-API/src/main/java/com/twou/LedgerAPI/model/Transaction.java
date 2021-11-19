package com.twou.LedgerAPI.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@SQLDelete(sql = "UPDATE transaction SET soft_delete = true WHERE id = ?")
@Where(clause = "soft_delete = false")
public class Transaction implements Serializable {

    @Column(nullable = false, updatable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false)
    private String recipient;

    private Boolean softDelete = Boolean.FALSE;

    @Column(nullable = false)
    private BigDecimal transactionValue;

    public Long getId() {
        return id;
    }

    public BigDecimal getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(BigDecimal transactionValue) {
        this.transactionValue = transactionValue;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction transaction = (Transaction) o;
        return getId().equals(transaction.getId()) && getSender().equals(transaction.getSender())
                && getRecipient().equals(transaction.getRecipient())
                && getTransactionValue().equals(transaction.getTransactionValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSender(), getRecipient(), getTransactionValue());
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", transactionValue=" + transactionValue +
                '}';
    }
}
