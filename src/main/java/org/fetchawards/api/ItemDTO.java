package org.fetchawards.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.fetchawards.model.Item;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ItemDTO {
    private BigDecimal price;
    private String shortDescription;

    public ItemDTO(Item item) {
        this.price = item.getPrice();
        this.shortDescription = item.getShortDescription();
    }
}
