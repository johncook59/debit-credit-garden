package zarg.debitcredit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "account", indexes = {@Index(name = "idx_account_bid", columnList = "bid")})
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @Column(name = "id", columnDefinition = "serial", nullable = false, updatable = false)
    @GeneratedValue
    private Long id;

    @Column(name = "bid", length = 12, updatable = false, insertable = false)
    @ColumnDefault("concat('A', lpad(nextval('hibernate_sequence'::regclass)::text, 8, '0'))")
    @Generated(GenerationTime.INSERT)
    private String bid;

    @Version
    @Builder.Default
    private int version = 0;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(length = 20, nullable = false)
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
