package br.com.saldoamigo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private double value;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountModel account;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private GroupModel group;

    @PrePersist
    protected void onCreate() {
            this.date = LocalDate.now();
    }
}
