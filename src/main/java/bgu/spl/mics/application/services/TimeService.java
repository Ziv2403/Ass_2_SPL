package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;
//
/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 * • Purpose: Global system timer handling clock ticks.
 * • Responsibilities:
 * o Counts clock ticks since initialization.
 * o Sends TickBroadcast messages at each tick.
 * o Receives speed (tick duration in milliseconds) and duration (number of ticks before
 * termination) as constructor arguments.
 * o Stops sending TickBroadcast messages after duration ticks.
 * o Signals termination of the process; do not wait for all events to finish.
 * o Note: Ensure the event loop is not blocked.
 * o Note: If this service reaches `duration` ticks the system should terminate.
 */
public class TimeService extends MicroService {
// --------------------- fields -------------------------
    private final int tickTime;
    private final int duration;
    private int currentTick;

// --------------------- constructors -------------------

    /**
     * Constructor for TimeService.
     *
     * @param TickTime The duration of each tick in seconds.
     * @param Duration The total number of ticks before the service terminates.
     * @param statisticalFolder The StatisticalFolder for tracking system statistics.
     */
    public TimeService(int TickTime, int Duration, StatisticalFolder statisticalFolder) {
        super("TimeService", statisticalFolder);
        this.tickTime = TickTime;
        this.duration = Duration;
        this.currentTick = 0; 
    }

    // --------------------- initialize ------------------------

    @Override
    protected void initialize() {

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });

        try {
            // Main loop: Broadcast ticks while not terminated and within duration
            while (currentTick < duration && !isTerminated()) {

                // Broadcast the current tick
                sendBroadcast(new TickBroadcast(currentTick));

                // Sleep for the tick duration
                try {
                    Thread.sleep(tickTime * 1000L);
                } catch (InterruptedException e) {
                    drainPendingMessages();
                    break; // Exit the loop on interruption
                }

                currentTick++; 
                statisticalFolder.incrementSystemRuntime();


            }

            // If duration is completed or terminated, notify termination
            sendBroadcast(new TerminatedBroadcast(getName()));

        } catch (Exception e) {
            // Log unexpected errors
            System.err.println("Error in TimeService: " + e.getMessage());
        } finally {
            // Ensure proper termination
            terminate();
        }
    }

    // Helper method to drain remaining messages
    private void drainPendingMessages() {
        try {
            while (!isTerminated()) {
                Message message = MessageBusImpl.getInstance().awaitMessage(this);
                Callback<Message> callback = (Callback<Message>) callbacks.get(message.getClass());
                if (callback != null) {
                    callback.call(message);
                }
            }
        } catch (InterruptedException e) {
            System.out.println(getName() + " interrupted while processing remaining messages.");
        }
    }
}
