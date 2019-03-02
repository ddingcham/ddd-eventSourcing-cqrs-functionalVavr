package com.ddingcham.event.boundary;

import com.ddingcham.event.domain.ShopItem;
import com.ddingcham.event.domain.ShopItemRepository;
import com.ddingcham.event.domain.commands.Order;
import com.ddingcham.event.domain.commands.OrderWithTimeout;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@Slf4j
public class ShopItems {

    private final ShopItemRepository itemRepository;
    private final int hoursToPaymentTimeout;

    @Autowired
    public ShopItems(ShopItemRepository itemRepository,
                     @Value("${minutes.to.payment.timeout:1}") int hoursToPaymentTimeout) {
        this.itemRepository = itemRepository;
        this.hoursToPaymentTimeout = hoursToPaymentTimeout;
    }

    public void order(Order command) {
        OrderWithTimeout orderWithTimeout =
                new OrderWithTimeout(
                        command.getUuid(),
                        command.getPrice(),
                        command.getWhen(),
                        hoursToPaymentTimeout
                );

        withItem(command.getUuid(),
                item -> item.order(orderWithTimeout));
        log.info("{}, item ordered at {}", command.getUuid(), command.getWhen());
    }

    public ShopItem getByUUID(UUID uuid) {
        return itemRepository.findByUUID(uuid);
    }

    private ShopItem withItem(UUID uuid, Function1<ShopItem, Try<ShopItem>> action) {
        final ShopItem item = getByUUID(uuid);
        final ShopItem modified = action
                .apply(item)
                .getOrElseThrow(throwable -> new IllegalArgumentException(throwable));
        return itemRepository.save(modified);

    }

}
