package bgu.spl.mics.application.services;

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

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
//    @Override
//    protected void initialize() {
//        try {
//            // Subscribe to CrashedBroadcast
//            subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
//                terminate();
//                System.out.println(getName() + " received CrashedBroadcast and is terminating.");
//            });
//
//            while (currentTick < duration && !isTerminated()) {
//                System.out.println("Current tick: " + currentTick);
//                //Send TickBroadcast to all microService
//                sendBroadcast(new TickBroadcast(currentTick));
//
//                //Update StatisticalFolder
//                statisticalFolder.incrementSystemRuntime();
//
//                //Wait until the next tick
//                long tickTimeInSeconds = (long)tickTime*1000;
//                Thread.sleep(tickTimeInSeconds);
//
//                currentTick++;
//            }
//            //When timeOut --> send TerminatedBroadcast
//            sendBroadcast(new TerminatedBroadcast(getName()));
//
//            Thread.sleep(1000);
//            terminate();
//
//        } catch (InterruptedException e) {
//            System.err.println("TimeService interrupted: " + e.getMessage());
//        }
//    }
//    @Override
//    protected void initialize() {
//        try {
//            // Subscribe to CrashedBroadcast
//            subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
//                System.out.println("TimeService received CrashedBroadcast.");
//                terminate();
//            });
//
//            while (currentTick < duration && !isTerminated()) {
//                // Broadcast current tick
//                sendBroadcast(new TickBroadcast(currentTick));
//                System.out.println("Tick: " + currentTick);
//
//                // Simulate runtime
//                statisticalFolder.incrementSystemRuntime();
//
//                // Sleep for tick duration
//                try {
//                    Thread.sleep(tickTime * 1000L);
//                } catch (InterruptedException e) {
//                    System.err.println("TimeService sleep interrupted: " + e.getMessage());
//                    break; // Exit loop if interrupted
//                }
//
//                currentTick++;
//            }
//
//            // Notify termination
//            sendBroadcast(new TerminatedBroadcast(getName()));
//            System.out.println("TimeService terminated normally.");
//
//        } catch (Exception e) {
//            System.err.println("Error in TimeService: " + e.getMessage());
//        } finally {
//            terminate();
//        }
//    }
    @Override
    protected void initialize() {
        System.out.println("TimeService is initializing...");

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            System.out.println(getName() + " received CrashedBroadcast.");
            terminate();
        });

        try {
            // Main loop: Broadcast ticks while not terminated and within duration
            while (currentTick < duration && !isTerminated()) {
                System.out.println("TimeService broadcasting Tick: " + currentTick);

                // Broadcast the current tick
                sendBroadcast(new TickBroadcast(currentTick));

                // Simulate runtime
                statisticalFolder.incrementSystemRuntime();

                // Sleep for the tick duration
                try {
                    Thread.sleep(tickTime * 1000L);
                } catch (InterruptedException e) {
                    System.err.println("TimeService sleep interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    break; // Exit the loop on interruption
                }

                currentTick++;
            }

            // If duration is completed or terminated, notify termination
            System.out.println("TimeService broadcasting TerminatedBroadcast.");
            sendBroadcast(new TerminatedBroadcast(getName()));

        } catch (Exception e) {
            // Log unexpected errors
            System.err.println("Error in TimeService: " + e.getMessage());
        } finally {
            // Ensure proper termination
            System.out.println("TimeService is terminating.");
            terminate();
        }
    }

}
