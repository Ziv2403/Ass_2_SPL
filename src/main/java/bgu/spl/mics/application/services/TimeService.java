package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
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
    private final int tickTime;
    private final int duration;
    private int currentTick;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration, StatisticalFolder statisticalFolder) {
        super("TimeService", statisticalFolder);
        this.tickTime = TickTime;
        this.duration = Duration;
        this.currentTick = 0; //might be 1 -> saw in the forum
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        try {
            while (currentTick < duration) {
                //Send TickBroadcast to all microService
                sendBroadcast(new TickBroadcast(currentTick));

                //Update StatisticalFolder
                StatisticalFolder.getInstance().incrementSystemRuntime();

                //Wait until the next tick
                Thread.sleep(tickTime);
                currentTick++;
            }
            //When timeOut --> send TerminatedBroadcast
            sendBroadcast(new TerminatedBroadcast(getName()));
        } catch (InterruptedException e) {
            System.err.println("TimeService interrupted: " + e.getMessage());
        }
    }
}
