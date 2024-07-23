package org.fetchawards.api;

import lombok.RequiredArgsConstructor;
import org.fetchawards.model.Item;
import org.fetchawards.model.Receipt;
import org.fetchawards.model.ReceiptRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class ReceiptProcessorController {
    private final ReceiptRepository repo;
    private final Logger log = Logger.getLogger(ReceiptProcessorController.class.getName());

    @PostMapping(path = "/receipts/process", consumes = "application/json")
    public ResponseEntity<?> process(@RequestBody ReceiptDTO receipt) {
        log.info(receipt.toString());

        Receipt receiptEntity = receipt.toReceipt();
        receiptEntity.setUuid(UUID.randomUUID().toString());
        this.repo.save(receiptEntity);
        log.info(receiptEntity.toString());

        Map<String, String> result = new HashMap<>();
        result.put("id", receiptEntity.getUuid());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/receipts/{id}/points")
    public ResponseEntity<?> getPoints(@PathVariable(value = "id") String id) {
        var points = this.points(this.repo.findByUuid(id));
        Map<String, String> result = new HashMap<>();
        result.put("points", String.valueOf(points));
        return ResponseEntity.ok(result);
    }

    private int points(Receipt receipt) {
        int points = 0;

        if (receipt != null) {
            Set<Item> items = receipt.getItems();

            points += receipt.getRetailer()
                    .replaceAll("[^a-zA-Z]", "")
                    .length();

            BigDecimal total = receipt.getItems().stream()
                    .map(Item::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (total.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
                points += 50;
                points += 25;
            }
            else if (total.remainder(BigDecimal.valueOf(0.25)).compareTo(BigDecimal.ZERO) == 0) {
                points += 25;
            }

            points += (items.size() / 2) * 5;

            for (Item item : items) {
                BigDecimal price = item.getPrice();
                int descriptionLength = item.getShortDescription().strip().length();

                if (descriptionLength % 3 == 0) {
                    points += price.multiply(BigDecimal.valueOf(0.2))
                            .setScale(0, RoundingMode.CEILING)
                            .intValue();
                }
            }

            var purchaseDt = receipt.getPurchaseDateTime();
            if (purchaseDt != null) {
                int hour = purchaseDt.getHour();
                int minute = purchaseDt.getMinute();
                int day = purchaseDt.getDayOfMonth();

                if (day % 2 != 0) {
                    points += 6;
                }

                if ((hour >= 14 && hour < 16) && !(hour == 14 && minute == 0)) {
                    points += 10;
                }
            }
        }
        return points;
    }
}
