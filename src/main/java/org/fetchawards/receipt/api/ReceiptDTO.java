package org.fetchawards.receipt.api;

import lombok.*;
import org.fetchawards.receipt.model.Item;
import org.fetchawards.receipt.model.Receipt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ReceiptDTO {
    private String retailer;
    private String purchaseDate;
    private String purchaseTime;
    private Set<ItemDTO> items;
    private BigDecimal total;

    public ReceiptDTO(Receipt receipt) {
        this.retailer = receipt.getRetailer();
        this.items = receipt.getItems().stream()
                .map(ItemDTO::new)
                .collect(Collectors.toSet());

        var purchaseDateTime = receipt.getPurchaseDateTime();
        this.purchaseDate = purchaseDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.purchaseTime = purchaseDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public Receipt toReceipt() {
        var receipt = new Receipt();
        receipt.setRetailer(this.getRetailer());

        var purcharseDate_dt = LocalDateTime.parse(this.getPurchaseDate() + " " + this.getPurchaseTime()
                , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        receipt.setPurchaseDateTime(purcharseDate_dt);

        var itemEntities = this.items.stream()
                .map(itemDTO -> {
                    var i = new Item();
                    i.setPrice(itemDTO.getPrice());
                    i.setShortDescription(itemDTO.getShortDescription());
                    return i;
                })
                .collect(Collectors.toSet());
        receipt.setItems(itemEntities);
        return receipt;
    }

    public BigDecimal getTotal() {
        if (this.items == null) {
            return BigDecimal.ZERO;
        }
        return this.items.stream()
                .map(ItemDTO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
