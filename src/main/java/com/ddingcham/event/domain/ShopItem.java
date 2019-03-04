package com.ddingcham.event.domain;

import com.ddingcham.event.domain.commands.OrderWithTimeout;
import com.ddingcham.event.domain.commands.Pay;
import com.ddingcham.event.domain.events.DomainEvent;
import com.ddingcham.event.domain.events.ItemOrdered;
import com.ddingcham.event.domain.events.ItemPaid;
import com.ddingcham.event.domain.events.ItemPaymentTimeout;
import com.google.common.collect.ImmutableList;
import io.vavr.API;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.ddingcham.event.domain.ShopItemStatus.*;
import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

/*
 * @Wither
 * -> withX() Chaining base 로 API 제공 : eternity 님 블로그에서 유창하게 검색
 * Put on any field to make lombok build a 'wither'
 * -> a withX method which produces a clone of this object (except for 1 field which gets a new value).
 * -> 기존에 생성된 Instance Object를 클로닝 하는 방식으로 지원
 * Complete documentation is found at
 * https://projectlombok.org/features/experimental/Wither.html
 */
@Wither
@RequiredArgsConstructor
@Getter
public class ShopItem {

    // low level details - state
    // state에 대한 동시성 관리(Thread-Safe)를 어떤 방식으로 접근하는 지
    // : 도형님 블로그 글의 Thread-Safe 불변식들을 어떻게 지키는 지
    private final UUID uuid;
    private final ImmutableList<DomainEvent> changes;
    private final ShopItemStatus status;

    // Command Handlers
    public Try<ShopItem> order(OrderWithTimeout command) {
        return Try.of(() -> {
            if (status == ShopItemStatus.INITIALIZED) {
                return appendChange.apply(this, order.apply(this, command));
            } else {
                return noOp.apply(this);
            }
        });
    }

    public Try<ShopItem> pay(Pay command) {
        return Try.of(() -> {
            throwIfStateIs(ShopItemStatus.INITIALIZED, "Payment is not missing yet");
            return null;
        });
    }

    private void throwIfStateIs(ShopItemStatus unexpectedState, String msg) {
        if (status == unexpectedState) {
            throw new IllegalStateException(msg + (" UUID: " + uuid));
        }
    }

    private Instant calculatePaymentTimeoutDate(Instant boughtAt, int hoursToPaymentTimeout) {
        final Instant paymentTimeout = boughtAt.plus(hoursToPaymentTimeout, ChronoUnit.MINUTES);
        if (paymentTimeout.isBefore(boughtAt)) {
            throw new IllegalArgumentException("Payment timeout day is before ordering date!");
        }
        return paymentTimeout;
    }

    /*
        State transitions
            -> MUST accept the facts and CANNOT contain any behaviour! (Handling Event)
        f(state, event) -> state
    */
    private static final Function2<ShopItem, ItemOrdered, ShopItem> ordered =
            (state, event) -> state
                    .withUuid(event.getUuid())
                    .withStatus(ORDERED);

    private static final Function2<ShopItem, ItemPaid, ShopItem> paid =
            (state, event) -> state
                    .withUuid(event.getUuid())
                    .withStatus(PAID);

    private static final Function2<ShopItem, ItemPaymentTimeout, ShopItem> timedOut =
            (state, event) -> state
                    .withUuid(event.getUuid())
                    .withStatus(PAYMENT_MISSING);

    private static final Function2<ShopItem, DomainEvent, ShopItem> appendChange =
            (state, event) -> state
                    .patternMatch(event)
                    .withChanges(ImmutableList.
                            <DomainEvent>builder()
                            .addAll(state.changes)
                            .add(event)
                            .build());

    /*
        Behaviour transitions -> Can fail or return new events
        f(state, command) -> events
     */
    private static final Function2<ShopItem, OrderWithTimeout, DomainEvent> order =
            (state, command) ->
                    new ItemOrdered(
                            command.getUuid(),
                            command.getWhen(),
                            state.calculatePaymentTimeoutDate(command.getWhen(), command.getHoursToPaymentTimeout()),
                            command.getPrice());

    private static final Function1<ShopItem, ShopItem> noOp =
            (state) -> state;

    /*
        Rebuilding aggregate with left fold and pattern match
        $ 형태 API는 왜 만든건지 ... 버전 확인을 했어야 하나 ㅡㅡ 2년전 예제 $ 없는 코드가 더 나은 듯
     */
    private ShopItem patternMatch(DomainEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(ItemPaid.class)), this::paid),
                Case($(instanceOf(ItemOrdered.class)), this::ordered),
                Case($(instanceOf(ItemPaymentTimeout.class)), this::paymentMissed)
        );
    }

    /*
        Event handlers - must accept the fact
     */
    private ShopItem paid(ItemPaid event) {
        return paid.apply(this, event);
    }

    private ShopItem ordered(ItemOrdered event) {
        return ordered.apply(this, event);
    }

    private ShopItem paymentMissed(ItemPaymentTimeout event) {
        return timedOut.apply(this, event);
    }

    // Getting and clearing all the changes
    // (finishing the work with unit of work - aggregate)
    public ImmutableList<DomainEvent> getUncommittedChanges() {
        return changes;
    }

    public ShopItem markChangesAsCommitted() {
        return this.withChanges(ImmutableList.of());
    }
}
