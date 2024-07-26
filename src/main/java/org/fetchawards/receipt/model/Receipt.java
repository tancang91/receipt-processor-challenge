package org.fetchawards.receipt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Table(name = "receipt")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String uuid;
    private String retailer;

    @Column(name = "purchase_dt")
    private LocalDateTime purchaseDateTime;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_receipt_id", referencedColumnName = "id")
    private Set<Item> items;
}
