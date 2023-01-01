package zarg.debitcredit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "customer",
        uniqueConstraints = @UniqueConstraint(columnNames = "email_address", name = "uk_email_address"),
        indexes = {
                @Index(name = "idx_customer_bid", columnList = "bid"),
                @Index(name = "idx_customer_email_address", columnList = "email_address")
        })
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @Column(name = "id", columnDefinition = "serial", nullable = false, updatable = false)
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "public.customer_id_seq"),
                    @Parameter(name = "increment_size", value = "1")
            })

    private Long id;

    @Column(name = "bid", length = 12, updatable = false, insertable = false)
    @ColumnDefault("concat('C', lpad(nextval('hibernate_sequence'::regclass)::text, 8, '0'))")
    @Generated(GenerationTime.INSERT)
    private String bid;

    @Version
    @Builder.Default
    private int version = 0;

    @Column(length = 20, nullable = false)
    private String password;

    @Column(name = "given_name", length = 40, nullable = false)
    private String givenName;

    @Column(length = 40, nullable = false)
    private String surname;

    @Column(name = "email_address", length = 60, nullable = false)
    private String emailAddress;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "customer_account",
            joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"),
            foreignKey = @ForeignKey(name = "fk_customer_id_customer_account"),
            inverseJoinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
            inverseForeignKey = @ForeignKey(name = "fk_account_id_customer_account"))
    @ToString.Exclude
    private List<Account> accounts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Customer customer = (Customer) o;
        return id != null && Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
