microservices-pattern-rabbitmq


Saga Pattern Choregraphy



Skip to content
Chat history




You said:
Saga orchestration pattern + rabbitmq + spring cloud stream + 3 micro services examples
ChatGPT said:
Here’s a concrete Saga Orchestration example using RabbitMQ + Spring Cloud Stream with 3 microservices.

Goal scenario
Order Service creates an order, then:

Payment Service charges money

Inventory Service reserves stock
If any step fails, orchestrator triggers compensations:

If inventory fails after payment success → refund payment

If payment fails → cancel order (no other undo needed)

Architecture
Orchestrator: order-service (owns saga state + workflow)

Participants: payment-service, inventory-service

Transport: RabbitMQ via Spring Cloud Stream

Messaging style: commands + events (async)

Channels (Rabbit bindings)
Commands (to services)

payment.commands

inventory.commands

Events (back to orchestrator)

payment.events

inventory.events

Event/Command types
Payment
ChargePaymentCommand(orderId, amount)

RefundPaymentCommand(orderId, amount)

PaymentChargedEvent(orderId)

PaymentFailedEvent(orderId, reason)

PaymentRefundedEvent(orderId)

Inventory
ReserveInventoryCommand(orderId, items)

ReleaseInventoryCommand(orderId, items)

InventoryReservedEvent(orderId)

InventoryFailedEvent(orderId, reason)

InventoryReleasedEvent(orderId)

Order (internal states)
NEW → PAYMENT_PENDING → PAYMENT_OK → INVENTORY_PENDING → COMPLETED

failure paths:

payment fail → CANCELLED

inventory fail after payment ok → REFUND_PENDING → CANCELLED

Step-by-step flow (orchestration)
Client calls POST /orders

order-service saves saga state = PAYMENT_PENDING

order-service publishes ChargePaymentCommand → payment.commands

payment-service consumes command:

success → emits PaymentChargedEvent to payment.events

fail → emits PaymentFailedEvent

order-service consumes payment event:

if charged → state INVENTORY_PENDING, publish ReserveInventoryCommand → inventory.commands

if failed → state CANCELLED

inventory-service consumes reserve command:

success → emits InventoryReservedEvent

fail → emits InventoryFailedEvent

order-service consumes inventory event:

if reserved → state COMPLETED

if failed → state REFUND_PENDING, publish RefundPaymentCommand → payment.commands

payment-service refunds and emits PaymentRefundedEvent

order-service marks saga CANCELLED

Spring Cloud Stream setup (RabbitMQ)
1) Common dependency (all services)
spring-boot-starter

spring-cloud-starter-stream-rabbit

2) order-service config (application.yml)
spring:
  cloud:
    stream:
      bindings:
        chargePayment-out-0:
          destination: payment.commands
        reserveInventory-out-0:
          destination: inventory.commands

        paymentEvents-in-0:
          destination: payment.events
          group: order-saga
        inventoryEvents-in-0:
          destination: inventory.events
          group: order-saga
order-service code (orchestrator)
@Configuration
public class SagaMessaging {

  @Bean
  public Supplier<Message<ChargePaymentCommand>> chargePaymentOutbox(OutboxService outbox) {
    return () -> outbox.nextChargePaymentMessage(); // returns null if nothing
  }

  @Bean
  public Supplier<Message<ReserveInventoryCommand>> reserveInventoryOutbox(OutboxService outbox) {
    return () -> outbox.nextReserveInventoryMessage();
  }

  @Bean
  public Consumer<PaymentEvent> paymentEvents(SagaCoordinator coordinator) {
    return coordinator::onPaymentEvent;
  }

  @Bean
  public Consumer<InventoryEvent> inventoryEvents(SagaCoordinator coordinator) {
    return coordinator::onInventoryEvent;
  }
}
SagaCoordinator logic (simplified)

On PaymentChargedEvent → send reserve inventory

On PaymentFailedEvent → cancel order

On InventoryFailedEvent → send refund payment

On InventoryReservedEvent → complete order

On PaymentRefundedEvent → cancel order

Real projects use an Outbox table (recommended) instead of directly publishing inside the DB transaction.

payment-service config
spring:
  cloud:
    stream:
      bindings:
        paymentCommands-in-0:
          destination: payment.commands
          group: payment
        paymentEvents-out-0:
          destination: payment.events
payment-service code
@Configuration
public class PaymentMessaging {

  @Bean
  public Consumer<PaymentCommand> paymentCommands(PaymentHandler handler) {
    return handler::handle;
  }

  @Bean
  public Function<PaymentResult, Message<PaymentEvent>> paymentEvents() {
    return result -> MessageBuilder.withPayload(result.toEvent()).build();
  }
}
(You can also publish directly using StreamBridge if you prefer.)

inventory-service config
spring:
  cloud:
    stream:
      bindings:
        inventoryCommands-in-0:
          destination: inventory.commands
          group: inventory
        inventoryEvents-out-0:
          destination: inventory.events
inventory-service code
@Configuration
public class InventoryMessaging {

  @Bean
  public Consumer<InventoryCommand> inventoryCommands(InventoryHandler handler) {
    return handler::handle;
  }
}
Key reliability rules (don’t skip these)
Outbox pattern in orchestrator (and ideally participants) to avoid “DB committed but message not sent”.

Idempotency: every consumer must handle duplicates safely (Rabbit retries can cause repeats).

Correlation IDs: every message carries orderId + sagaId.

DLQ + retry: configure retries and dead-letter queues for poison messages.

Time-out handling: if payment event never arrives, saga should time out and cancel.

When to use orchestration vs choreography
Use orchestration when:

you need strict control, clear workflow, compensation rules, audit trail

Use choreography when:

workflow is simple, services can react independently, fewer central rules

If you want, I can also give you:

a complete runnable repo structure (3 Spring Boot apps)

exact Spring Cloud Stream function names mapped to bindings

Outbox table schema + polling publisher

a sequence diagram (PlantUML) of this saga flow

You said:
I prefer streamBridge
ChatGPT said:
Cool. Here’s the same Saga Orchestration (3 services) but using StreamBridge everywhere (RabbitMQ + Spring Cloud Stream).

Channels
Commands

payment.commands

inventory.commands

Events

payment.events

inventory.events

1) Order Service (Orchestrator)
application.yml
spring:
  cloud:
    stream:
      bindings:
        paymentEvents-in-0:
          destination: payment.events
          group: order-saga
        inventoryEvents-in-0:
          destination: inventory.events
          group: order-saga

        paymentCommands-out-0:
          destination: payment.commands
        inventoryCommands-out-0:
          destination: inventory.commands
Message contracts (simple)
public record ChargePaymentCommand(String sagaId, String orderId, double amount) {}
public record RefundPaymentCommand(String sagaId, String orderId, double amount) {}

public sealed interface PaymentEvent permits PaymentChargedEvent, PaymentFailedEvent, PaymentRefundedEvent {
  String sagaId(); String orderId();
}
public record PaymentChargedEvent(String sagaId, String orderId) implements PaymentEvent {}
public record PaymentFailedEvent(String sagaId, String orderId, String reason) implements PaymentEvent {}
public record PaymentRefundedEvent(String sagaId, String orderId) implements PaymentEvent {}

public record ReserveInventoryCommand(String sagaId, String orderId, Map<String,Integer> items) {}
public record ReleaseInventoryCommand(String sagaId, String orderId, Map<String,Integer> items) {}

public sealed interface InventoryEvent permits InventoryReservedEvent, InventoryFailedEvent, InventoryReleasedEvent {
  String sagaId(); String orderId();
}
public record InventoryReservedEvent(String sagaId, String orderId) implements InventoryEvent {}
public record InventoryFailedEvent(String sagaId, String orderId, String reason) implements InventoryEvent {}
public record InventoryReleasedEvent(String sagaId, String orderId) implements InventoryEvent {}
Orchestrator publisher (StreamBridge)
@Service
@RequiredArgsConstructor
public class SagaPublisher {
  private final StreamBridge streamBridge;

  public void chargePayment(ChargePaymentCommand cmd) {
    streamBridge.send("paymentCommands-out-0", cmd);
  }

  public void refundPayment(RefundPaymentCommand cmd) {
    streamBridge.send("paymentCommands-out-0", cmd);
  }

  public void reserveInventory(ReserveInventoryCommand cmd) {
    streamBridge.send("inventoryCommands-out-0", cmd);
  }

  public void releaseInventory(ReleaseInventoryCommand cmd) {
    streamBridge.send("inventoryCommands-out-0", cmd);
  }
}
Orchestrator consumers (Stream)
@Configuration
@RequiredArgsConstructor
public class SagaConsumers {

  private final SagaCoordinator coordinator;

  @Bean
  public Consumer<PaymentEvent> paymentEvents() {
    return coordinator::onPaymentEvent;
  }

  @Bean
  public Consumer<InventoryEvent> inventoryEvents() {
    return coordinator::onInventoryEvent;
  }
}
Coordinator logic (core orchestration)
@Service
@RequiredArgsConstructor
public class SagaCoordinator {

  private final SagaRepository sagaRepo;      // store saga state
  private final SagaPublisher publisher;

  public void startSaga(String sagaId, String orderId, double amount, Map<String,Integer> items) {
    sagaRepo.save(new SagaState(sagaId, orderId, amount, items, "PAYMENT_PENDING"));
    publisher.chargePayment(new ChargePaymentCommand(sagaId, orderId, amount));
  }

  public void onPaymentEvent(PaymentEvent evt) {
    var saga = sagaRepo.findBySagaId(evt.sagaId());

    if (evt instanceof PaymentChargedEvent) {
      saga.setStatus("INVENTORY_PENDING");
      sagaRepo.save(saga);
      publisher.reserveInventory(new ReserveInventoryCommand(saga.sagaId(), saga.orderId(), saga.items()));
      return;
    }

    if (evt instanceof PaymentFailedEvent failed) {
      saga.setStatus("CANCELLED");
      saga.setError(failed.reason());
      sagaRepo.save(saga);
      return;
    }

    if (evt instanceof PaymentRefundedEvent) {
      saga.setStatus("CANCELLED");
      sagaRepo.save(saga);
    }
  }

  public void onInventoryEvent(InventoryEvent evt) {
    var saga = sagaRepo.findBySagaId(evt.sagaId());

    if (evt instanceof InventoryReservedEvent) {
      saga.setStatus("COMPLETED");
      sagaRepo.save(saga);
      return;
    }

    if (evt instanceof InventoryFailedEvent failed) {
      saga.setStatus("REFUND_PENDING");
      saga.setError(failed.reason());
      sagaRepo.save(saga);
      publisher.refundPayment(new RefundPaymentCommand(saga.sagaId(), saga.orderId(), saga.amount()));
      return;
    }

    if (evt instanceof InventoryReleasedEvent) {
      // optional: if you had reserved first and payment failed later
    }
  }
}
2) Payment Service (Participant)
application.yml
spring:
  cloud:
    stream:
      bindings:
        paymentCommands-in-0:
          destination: payment.commands
          group: payment
        paymentEvents-out-0:
          destination: payment.events
Consumer + event publishing with StreamBridge
@Service
@RequiredArgsConstructor
public class PaymentCommandHandler {
  private final StreamBridge streamBridge;

  @Bean
  public Consumer<Object> paymentCommands() {
    return cmd -> {
      try {
        if (cmd instanceof ChargePaymentCommand c) {
          // charge logic...
          streamBridge.send("paymentEvents-out-0", new PaymentChargedEvent(c.sagaId(), c.orderId()));
        } else if (cmd instanceof RefundPaymentCommand r) {
          // refund logic...
          streamBridge.send("paymentEvents-out-0", new PaymentRefundedEvent(r.sagaId(), r.orderId()));
        }
      } catch (Exception e) {
        String sagaId = (cmd instanceof ChargePaymentCommand c) ? c.sagaId()
                      : (cmd instanceof RefundPaymentCommand r) ? r.sagaId()
                      : "unknown";
        String orderId = (cmd instanceof ChargePaymentCommand c) ? c.orderId()
                      : (cmd instanceof RefundPaymentCommand r) ? r.orderId()
                      : "unknown";

        streamBridge.send("paymentEvents-out-0", new PaymentFailedEvent(sagaId, orderId, e.getMessage()));
      }
    };
  }
}
Tip: In real code, don’t use Consumer<Object>; use a wrapper like PaymentCommand sealed interface so deserialization is clean.

3) Inventory Service (Participant)
application.yml
spring:
  cloud:
    stream:
      bindings:
        inventoryCommands-in-0:
          destination: inventory.commands
          group: inventory
        inventoryEvents-out-0:
          destination: inventory.events
Consumer + StreamBridge publishing
@Service
@RequiredArgsConstructor
public class InventoryCommandHandler {
  private final StreamBridge streamBridge;

  @Bean
  public Consumer<Object> inventoryCommands() {
    return cmd -> {
      try {
        if (cmd instanceof ReserveInventoryCommand r) {
          // reserve logic...
          streamBridge.send("inventoryEvents-out-0", new InventoryReservedEvent(r.sagaId(), r.orderId()));
        } else if (cmd instanceof ReleaseInventoryCommand rel) {
          // release logic...
          streamBridge.send("inventoryEvents-out-0", new InventoryReleasedEvent(rel.sagaId(), rel.orderId()));
        }
      } catch (Exception e) {
        String sagaId = (cmd instanceof ReserveInventoryCommand r) ? r.sagaId()
                      : (cmd instanceof ReleaseInventoryCommand r) ? r.sagaId()
                      : "unknown";
        String orderId = (cmd instanceof ReserveInventoryCommand r) ? r.orderId()
                      : (cmd instanceof ReleaseInventoryCommand r) ? r.orderId()
                      : "unknown";

        streamBridge.send("inventoryEvents-out-0", new InventoryFailedEvent(sagaId, orderId, e.getMessage()));
      }
    };
  }
}
Important configs you’ll want (Rabbit reliability)
Add these (at least in prod):

Consumer retries + DLQ

Idempotency (store processed message IDs per service)

Correlation headers (sagaId, orderId)

Outbox (for exactly-once-ish behavior)

Example: send headers with StreamBridge

streamBridge.send("paymentCommands-out-0",
  MessageBuilder.withPayload(cmd)
    .setHeader("sagaId", cmd.sagaId())
    .setHeader("orderId", cmd.orderId())
    .build()
);
Quick mental model
Orchestrator owns the “state machine”

Participants just execute commands and publish result events

Compensation is just another command (refund / release)

If you want, I can also give you a PlantUML sequence diagram and a minimal working project layout (3 apps + shared “contracts” module) using StreamBridge and JSON serialization.






No file chosenNo file chosen
ChatGPT can make mistakes. Check important info.
