package zarg.debitcredit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_account_bid", columnList = "account_bid")
})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @Column(name = "id", columnDefinition = "serial", nullable = false, updatable = false)
    @GeneratedValue(generator = "transaction-sequence-generator")
    @GenericGenerator(
            name = "transaction-sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "public.transaction_id_seq"),
                    @Parameter(name = "increment_size", value = "1")
            })
    private Long id;

    @Column(name = "bid", length = 12, updatable = false, insertable = false)
    @ColumnDefault("concat('T', lpad(nextval('hibernate_sequence'::regclass)::text, 10, '0'))")
    @Generated(GenerationTime.INSERT)
    private String bid;

    @Column(name = "user_bid", length = 12, nullable = false)
    private String userBid;

    @Version
    @Builder.Default
    private int version = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false)
    public TransactionDirection direction;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "account_bid", nullable = false)
    private String accountBid;

    @Column(name = "processed_dt", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private LocalDateTime processed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transaction that = (Transaction) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
