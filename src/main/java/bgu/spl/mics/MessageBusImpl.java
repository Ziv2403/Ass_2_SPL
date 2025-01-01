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
	private final Map<Class<? extends Event<?>>, Queue<MicroService>> eventSubscribers = new ConcurrentHashMap<>();// Mapping each Event type to a queue of its subscribers (supports Round-Robin)
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

	/**
 	* Registers a MicroService to receive events of a specific type.
 	* Using Linked to facilitate Round-Robin distribution
	* 
	* Why ConcurrentLinkedQueue:
	* It ensures thread safety without the need for external synchronization, unlike LinkedList.
	* Ideal for systems where multiple threads may concurrently add or remove subscribers from the queue.
	*
	* @param <T>  The type of the result expected by the event.
 	* @param type The type of the event to subscribe to.
 	* @param m    The MicroService to register.
 	*/
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.computeIfAbsent(type, k -> new ConcurrentLinkedQueue<>()).add(m);
		queues.putIfAbsent(m, new LinkedBlockingDeque<>());
	}

	/**
 	* Registers a MicroService to receive broadcasts of a specific type.
 	* Using CopyOnWriteArrayList to handle concurrent reads safely
	* 
	* Why CopyOnWriteArrayList: Ideal for scenarios with frequent reads and infrequent writes
	* Provides thread-safe operations without external synchronization.
	* 
 	* @param type The type of the broadcast to subscribe to.
 	* @param m    The MicroService to register.
 	*/
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(m);
		 queues.putIfAbsent(m, new LinkedBlockingDeque<>());
	}

	/**
 	* Marks an event as completed by resolving its Future object.
 	*
 	* @param <T>    The type of the result expected by the event.
 	* @param e      The event to mark as completed.
 	* @param result The result to resolve the Future associated with the event.
 	*/	
	@SuppressWarnings("unchecked")//???????
	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventFutures.get(e); // Retrieve the Future
		if (future != null) {
			future.resolve(result); // Mark the Future as completed
			eventFutures.remove(e); // // Remove mapping for cleanup
		}
	}

	/**
 	* Sends a broadcast message to all MicroServices subscribed to the broadcast's type.
 	*
 	* @param b The broadcast message to send.
 	*/	
	@Override
	public void sendBroadcast(Broadcast b) {
		List<MicroService> subscribers = broadcastSubscribers.get(b.getClass());// Take the list of the type of broadcast we want to send.
		if (subscribers != null) {
			for (MicroService m : subscribers){
					queues.get(m).offer(b); // Add broadcast to each subscriber's queue
			}
		}
	}

	// @Override
	// public void sendBroadcast(Broadcast b) {
	// 	List<MicroService> subscribers = broadcastSubscribers.get(b.getClass());// Take the list of the type of broadcast we want to send.
	// 	if (subscribers == null) {
	// 		return;
	// 	}

	// 	// Add the Broadcast to the queue of each subscriber
	// 	for (MicroService m : subscribers){
	// 		BlockingQueue<Message> queue = queues.get(m);
	// 		if (queue != null) {
	// 			queue.offer(b);
	// 		}

	// 	}
	// }
	
	/**
 	* Sends an event to one MicroService subscribed to the event's type, using a Round-Robin mechanism.
 	*
 	* @param <T> The type of the result expected by the event.
 	* @param e   The event to send.
 	* @return A Future object to track the result of the event, or null if no MicroService is subscribed.
 	*/														
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Queue<MicroService> subscribers = eventSubscribers.get(e.getClass()); // Take the queue of the type of event we want to send.
		if (subscribers == null || subscribers.isEmpty()) { 
			return null; // No subscribers for this Event
		}

		synchronized (subscribers){
			MicroService m = subscribers.poll();
			if(m != null){
				subscribers.offer(m);
				Future<T> future = new Future<>();
				eventFutures.put(e, future);
				queues.get(m).offer(e);
				return future;
			}
		}
		return null;

		// MicroService m = subscribers.poll(); // Take the next subscriber
		// subscribers.offer(m); // Add back for Round-Robin

		// //Create a new Future object
		// Future<T> future = new Future<>();
		// eventFutures.put(e, future); // 

		// //Add the event to the selected MicroService's queue
		// queues.get(m).offer(e);

		// return future;
	}

	/**
 	* Registers a MicroService in the system by allocating a message queue for it.
 	*
	* Why BlockingQueue: Provides a thread-safe queue with blocking operations
    * Combined with ConcurrentHashMap to eliminate external synchronization
	*
 	* @param m The MicroService to register.
 	*/
	@Override
	public void register(MicroService m) {
		queues.putIfAbsent(m, new LinkedBlockingDeque<>());
	}

	/**
 	* Unregisters a MicroService from the system by removing its message queue and cleaning related references.
 	*
 	* @param m The MicroService to unregister.
 	*/
	@Override
	public void unregister(MicroService m) {
    	queues.remove(m);
    	eventSubscribers.values().forEach(queue -> queue.remove(m));
    	broadcastSubscribers.values().forEach(list -> list.remove(m));
	}

	/**
 	* Retrieves the next message for a registered MicroService. This method blocks until a message is available.
 	*
 	* @param m The MicroService requesting the next message.
 	* @return The next message in the MicroService's queue.
 	* @throws InterruptedException      If interrupted while waiting for a message.
 	* @throws IllegalStateException If the MicroService is not registered.
 	*/
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = queues.get(m);
		if (queue == null) {
			throw new IllegalStateException("MicroService not registered");
		}
		return queue.take(); // Blocking call until message is available
	}

	

}
