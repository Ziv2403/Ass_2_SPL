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

	private static MessageBusImpl instance = null;
	
	private final Map<MicroService, BlockingQueue<Message>> queues= new ConcurrentHashMap<>(); // Mapping each MicroService to its private message queue
	private final Map<Class<? extends Event>, Queue<MicroService>> eventSubscribers = new ConcurrentHashMap<>();// Mapping each Event type to a queue of its subscribers (supports Round-Robin)
	private final Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscribers = new ConcurrentHashMap<>(); // Mapping each Broadcast type to a list of its subscribers
	private final Map<Event<?>, Future<?>> eventFutures = new ConcurrentHashMap<>();

	private MessageBusImpl(){

	}

	//Added method: thread-safe singleton
	public static synchronized MessageBusImpl getInstance() {
		
        if (instance == null) {
            instance = new MessageBusImpl();
        }
        return instance;
    }

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// Register a MicroService as a subscriber to an Event type
        // Using LinkedList to facilitate Round-Robin distribution
		eventSubscribers.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);

		// Ensure the MicroService has a queue in the `queues` map
		queues.putIfAbsent(m, new LinkedBlockingDeque<>());

		// Why ConcurrentLinkedQueue:
		// It ensures thread safety without the need for external synchronization, unlike LinkedList.
		// Ideal for systems where multiple threads may concurrently add or remove subscribers from the queue.
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        // Register a MicroService as a subscriber to a Broadcast type
        // Using CopyOnWriteArrayList to handle concurrent reads safely
		broadcastSubscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(m);

		// Why CopyOnWriteArrayList: Ideal for scenarios with frequent reads and infrequent writes
        // Provides thread-safe operations without external synchronization

		 // Ensure the MicroService has a queue in the `queues` map
		 queues.putIfAbsent(m, new LinkedBlockingDeque<>());
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventFutures.get(e); // Retrieve the Future
		if (future != null) {
			future.resolve(result); // Mark the Future as completed
			eventFutures.remove(e); // Optional: remove the Event-Future mapping
		}
	}

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

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Queue<MicroService> subscribers = eventSubscribers.get(e.getClass()); // Take the queue of the type of event we want to send.
		if (subscribers == null || subscribers.isEmpty()) { 
			return null; // No subscribers for this Event
		}

		//Select the next MicroService to handle the event
		MicroService m = subscribers.poll();
		subscribers.offer(m);

		//Create a new Future object
		Future<T> future = new Future<>();
		eventFutures.put(e, future); // 

		//Add the event to the selected MicroService's queue
		queues.get(m).offer(e);

		return future;
	}

	@Override
	public void register(MicroService m) {
		// Add new entries only if they don't exist
		queues.putIfAbsent(m, new LinkedBlockingDeque<>());

		// Why BlockingQueue: Provides a thread-safe queue with blocking operations
        // Combined with ConcurrentHashMap to eliminate external synchronization

	}

	@Override
	public void unregister(MicroService m) {
		// Remove the MicroService's queue
    	queues.remove(m);

    	// Remove the MicroService from all Event subscriber lists
    	eventSubscribers.values().forEach(queue -> queue.remove(m));

    	// Remove the MicroService from all Broadcast subscriber lists
    	broadcastSubscribers.values().forEach(list -> list.remove(m));
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = queues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService not registered");
		}
		return queue.take(); // Waits until a message is available
	}

	

}
