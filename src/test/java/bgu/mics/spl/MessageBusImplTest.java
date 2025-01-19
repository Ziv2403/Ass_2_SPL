package bgu.mics.spl;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {

    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;
    private MicroService microService3;
    private DetectObjectsEvent testEvent;  // **Reused field for event testing**

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new MicroService("Service1") {
            @Override
            protected void initialize() {
            }
        };
        microService2 = new MicroService("Service2") {
            @Override
            protected void initialize() {
            }
        };
        microService3 = new MicroService("Service3") {
            @Override
            protected void initialize() {
            }
        };

        // Register microservices before testing message handling
        messageBus.register(microService1);
        messageBus.register(microService2);
        messageBus.register(microService3);

        // ✅ Step 1: Create Detected Objects
        List<DetectedObject> detectedObjects = new ArrayList<>();
        detectedObjects.add(new DetectedObject("Wall_1", "Wall"));
        detectedObjects.add(new DetectedObject("Chair_1", "Chair"));

        // ✅ Step 2: Create StampedDetectedObjects with timestamp
        StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(5, detectedObjects);

        // ✅ Step 3: Create a valid DetectObjectsEvent
        testEvent = new DetectObjectsEvent(stampedDetectedObjects, 1);
    }

    @Test
    void getInstance() {
        MessageBusImpl instance1 = MessageBusImpl.getInstance();
        MessageBusImpl instance2 = MessageBusImpl.getInstance();
        assertNotNull(instance1, "Instance should not be null");
        assertSame(instance1, instance2, "MessageBusImpl should be a singleton");
    }

    @Test
    void subscribeEvent() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService1);
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService2);

        // Send the pre-initialized event
        messageBus.sendEvent(testEvent);

        // Check that at least one microservice gets the event
        assertDoesNotThrow(() -> {
            Message receivedMessage = messageBus.awaitMessage(microService1);
            assertEquals(testEvent, receivedMessage, "Received event should match the sent event");
        });
    }

    @Test
    void subscribeBroadcast() {
        messageBus.subscribeBroadcast(TickBroadcast.class, microService1);
        messageBus.subscribeBroadcast(TickBroadcast.class, microService2);

        // Send a broadcast
        TickBroadcast broadcast = new TickBroadcast();
        messageBus.sendBroadcast(broadcast);

        // Check that all subscribed microservices receive the broadcast
        assertDoesNotThrow(() -> {
            Message receivedMessage1 = messageBus.awaitMessage(microService1);
            Message receivedMessage2 = messageBus.awaitMessage(microService2);

            assertEquals(broadcast, receivedMessage1, "Both microservices should receive the broadcast");
            assertEquals(broadcast, receivedMessage2, "Both microservices should receive the broadcast");
        });
    }

    @Test
    void complete() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService1);

        Future<List<StampedDetectedObjects>> future = messageBus.sendEvent(testEvent);

        // Ensure the future is unresolved before completion
        assertFalse(future.isDone(), "Future should not be completed yet");

        // Process event and complete
        assertDoesNotThrow(() -> {
            Message receivedMessage = messageBus.awaitMessage(microService1);
            messageBus.complete((Event<List<StampedDetectedObjects>>) receivedMessage, new ArrayList<>()); // FIX: Use new ArrayList<>()
        });

        // Ensure the future is resolved
        assertTrue(future.isDone(), "Future should be completed");
        assertEquals(new ArrayList<>(), future.get(), "Future should return an empty list as expected");
    }

    @Test
    void sendBroadcast() {
        messageBus.subscribeBroadcast(TickBroadcast.class, microService1);
        messageBus.subscribeBroadcast(TickBroadcast.class, microService2);

        TickBroadcast broadcast = new TickBroadcast();
        messageBus.sendBroadcast(broadcast);

        // Check that both microservices receive the broadcast
        assertDoesNotThrow(() -> {
            Message receivedMessage1 = messageBus.awaitMessage(microService1);
            Message receivedMessage2 = messageBus.awaitMessage(microService2);

            assertEquals(broadcast, receivedMessage1, "First subscriber should receive the broadcast");
            assertEquals(broadcast, receivedMessage2, "Second subscriber should receive the broadcast");
        });
    }

    @Test
    void sendEvent() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService1);
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService2);

        Future<List<StampedDetectedObjects>> future = messageBus.sendEvent(testEvent);

        assertNotNull(future, "Future should not be null");

        // Timeout mechanism to prevent blocking forever
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            Message receivedMessage = messageBus.awaitMessage(microService1);
            assertEquals(testEvent, receivedMessage, "MicroService should receive the event");
        }, "sendEvent() should not block indefinitely");
    }

    @Test
    void register() {
        MicroService newService = new MicroService("NewService") {
            @Override
            protected void initialize() {
            }
        };
        assertDoesNotThrow(() -> messageBus.register(newService), "MicroService should register without exceptions");
    }

    @Test
    void unregister() {
        messageBus.unregister(microService1);

        // Ensure that trying to get messages from unregistered service throws an exception
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(microService1),
                "Unregistered service should not receive messages");
    }

    @Test
    void awaitMessage() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService1);

        messageBus.sendEvent(testEvent);

        // Ensure that awaiting a message does not hang indefinitely
        assertDoesNotThrow(() -> {
            Message receivedMessage = messageBus.awaitMessage(microService1);
            assertEquals(testEvent, receivedMessage, "MicroService should receive the event");
        }, "awaitMessage() should not block indefinitely");
    }

    @Test
    void printSubscribers() {
        messageBus.subscribeEvent(DetectObjectsEvent.class, microService1);
        messageBus.subscribeBroadcast(TickBroadcast.class, microService2);

        // This is a void method; ensure it runs without throwing an exception
        assertDoesNotThrow(() -> messageBus.printSubscribers(), "Printing subscribers should not cause an exception");
    }

    @Test
    void setTimeServiceThread() {
        Thread timeServiceThread = new Thread(() -> {
            try {
                Thread.sleep(1000); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Ensure the method does not throw an exception when setting the TimeService thread
        assertDoesNotThrow(() -> messageBus.setTimeServiceThread(timeServiceThread),
                "Setting the TimeService thread should not cause an exception");

        // Ensure the thread is actually set
        assertEquals(timeServiceThread, messageBus.getTimeServiceThread(),
                "The set TimeService thread should match the one retrieved");
    }

    @AfterEach
    void tearDown() {
        messageBus.unregister(microService1);
        messageBus.unregister(microService2);
        messageBus.unregister(microService3);
    }

}
