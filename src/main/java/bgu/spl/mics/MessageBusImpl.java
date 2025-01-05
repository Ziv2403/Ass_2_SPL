package bgu.spl.mics;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	// --------------------- fields -------------------------

	private static MessageBusImpl instance = null;
	
	private final Map<MicroService, BlockingQueue<Message>> queues= new ConcurrentHashMap<>(); // Mapping each MicroService to its private message queue
	private final Map<Class<? extends Event>, Queue<MicroService>> eventSubscribers = new ConcurrentHashMap<>();// Mapping each Event type to a queue of its subscribers (supports Round-Robin)
	private final Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscribers = new ConcurrentHashMap<>(); // Mapping each Broadcast type to a list of its subscribers
	private final Map<Event<?>, Future<?>> eventFutures = new ConcurrentHashMap<>();

    // --------------------- SingletonImplemment -------------------------

    /**
     * Private constructor to enforce the singleton pattern.
     */
	private MessageBusImpl() {}


    /**
     * Retrieves the singleton instance of the MessageBusImpl.
     * This method is thread-safe.
     *
     * @return the singleton instance of the MessageBusImpl.
     */
	public static synchronized MessageBusImpl getInstance() {
		
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }


    // --------------------- methods ------------------------

    /**
     * Subscribes a MicroService to receive events of a specific type.
	 * Why ConcurrentLinkedQueue:
	 * It ensures thread safety without the need for external synchronization, unlike LinkedList.
	 * Ideal for systems where multiple threads may concurrently add or remove subscribers from the queue.
     *
     * @param <T>  The type of the result expected by the completed event.
     * @param type The class representing the type of event to subscribe to.
     * @param m    The subscribing MicroService.
     * @pre m must not be null and must be registered.
     * @post The MicroService is added to the subscriber list for the specified event type.
     */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
		queues.putIfAbsent(m, new LinkedBlockingDeque<>());

		
	}


    /**
     * Subscribes a MicroService to receive broadcasts of a specific type.
	 * 
	 * Why CopyOnWriteArrayList: Ideal for scenarios with frequent reads and infrequent writes
     * Provides thread-safe operations without external synchronization
     *
	 * @param type The class representing the type of broadcast to subscribe to.
     * @param m    The subscribing MicroService.
     * @pre m must not be null and must be registered.
     * @post The MicroService is added to the subscriber list for the specified broadcast type.
     */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(m);
		queues.putIfAbsent(m, new LinkedBlockingDeque<>());
	}


    /**
     * Marks an event as completed and resolves its Future with the given result.
     *
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The result of the completed event.
     * @pre e must have been previously sent and must not be null.
     * @post The Future associated with the event is resolved with the given result.
     */
	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventFutures.get(e); // Retrieve the Future
		if (future != null) {
			future.resolve(result); // Mark the Future as completed
			eventFutures.remove(e); // Optional: remove the Event-Future mapping
		}
	}


    /**
     * Sends a broadcast message to all subscribed MicroServices.
     *
     * @param b The broadcast message to send.
     * @pre b must not be null.
     * @post All subscribed MicroServices have the broadcast added to their queues.
     */
	@Override
	public void sendBroadcast(Broadcast b) {
		List<MicroService> subscribers = broadcastSubscribers.get(b.getClass());// Take the list of the type of broadcast we want to send.
		if (subscribers == null) {
			return;
		}

		// Add the Broadcast to the queue of each subscriber
		for (MicroService m : subscribers){
			BlockingQueue<Message> queue = queues.get(m);
			if (queue != null) {
				queue.offer(b);
			}

		}
	}

	

    /**
     * Sends an event to one of the subscribed MicroServices in a round-robin manner.
     *
     * @param <T> The type of the result expected by the event.
     * @param e   The event to send.
     * @return a Future object to be resolved once the processing is complete, or null if no MicroService is subscribed.
     * @pre e must not be null.
     * @post The event is added to the queue of a subscribed MicroService.
     */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Queue<MicroService> subscribers = eventSubscribers.get(e.getClass()); // Take the queue of the type of event we want to send.
		if (subscribers == null || subscribers.isEmpty()) { 
			return null; // No subscribers for this Event
		}

		//Select the next MicroService to handle the event
		MicroService m = subscribers.poll();
		if (m == null) { return null; }
		subscribers.offer(m);

		//Create a new Future object
		Future<T> future = new Future<>();
		eventFutures.put(e, future); // 

		//Add the event to the selected MicroService's queue
		queues.get(m).offer(e);

		return future;
	}


    /**
     * Registers a MicroService by allocating a message queue for it.
     *
	 * Why BlockingQueue: Provides a thread-safe queue with blocking operations   
	 * Combined with ConcurrentHashMap to eliminate external synchronization
	 * 
     * @param m the MicroService to register.
     * @pre m must not be null.
     * @post The MicroService has a queue allocated for it in the MessageBus.
     */
	@Override
	public void register(MicroService m) {
		// Add new entries only if they don't exist
		queues.putIfAbsent(m, new LinkedBlockingDeque<>());
	}


    /**
     * Unregisters a MicroService by removing its message queue and clearing all references.
     *
     * @param m the MicroService to unregister.
     * @pre m must not be null and must be registered.
     * @post The MicroService is removed from all subscriber lists and its queue is deleted.
     */
	@Override
	public void unregister(MicroService m) {
		// Remove the MicroService's queue
    	queues.remove(m);

    	// Remove the MicroService from all Event subscriber lists
    	eventSubscribers.values().forEach(queue -> queue.remove(m));

    	// Remove the MicroService from all Broadcast subscriber lists
    	broadcastSubscribers.values().forEach(list -> list.remove(m));
	}


    /**
     * Allows a registered MicroService to take the next message from its queue.
     *
     * @param m The MicroService requesting to take a message from its queue.
     * @return The next message in the MicroService's queue.
     * @throws InterruptedException    if interrupted while waiting for a message to become available.
     * @throws IllegalStateException if the MicroService is not registered.
     * @pre m must be registered and not null.
     * @post The message is removed from the MicroService's queue.
     */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = queues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService not registered");
		}
		return queue.take(); // Waits until a message is available
	}


    /**
     * Prints the lists of event and broadcast subscribers for debugging purposes.
     */
	public void printSubscribers() {
		System.out.println("Event Subscribers: " + eventSubscribers);
		System.out.println("Broadcast Subscribers: " + broadcastSubscribers);
	}

}
