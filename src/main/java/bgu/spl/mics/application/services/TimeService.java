package bgu.spl.mics.application.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;
//
/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    //private final int tickTime;
    private final int duration;
    private int currentTick = 0;
    private final StatisticalFolder stats;
    private final ScheduledExecutorService scheduler;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int Duration, StatisticalFolder stats) {
        super("TimeService");
        //this.tickTime = TickTime;
        this.duration = Duration;
        this.currentTick = 0; //might be 1 -> saw in the forum
        this.stats = stats;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
        scheduler.scheduleAtFixedRate(() -> {
            if(currentTick < duration){
                sendBroadcast(new TickBroadcast(currentTick));
                stats.incrementSystemRuntime();
                currentTick++;
            } else{
                sendBroadcast(new TerminatedBroadcast(getName()));
                shutdownSystem();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


    private void shutdownSystem(){
        try {
            scheduler.shutdown();
            if(!scheduler.awaitTermination(1, TimeUnit.SECONDS)){ //NOT SURE IF NED TO WAIT OR NOT
                scheduler.shutdownNow();
            }
        } catch (InterruptedException  e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        //writeOutput();
        terminate();
    }
}

// try {
//     while (currentTick < duration) {
//         //Send TickBroadcast to all microService
//         sendBroadcast(new TickBroadcast(currentTick));

//         //Update StatisticalFolder
//         stats.incrementSystemRuntime();

//         //Wait until the next tick
//         Thread.sleep(tickTime);
//         currentTick++;
//     }
//     //When timeOut --> send TerminatedBroadcast
//     sendBroadcast(new TerminatedBroadcast(getName()));
// } catch (InterruptedException e) {
//     System.err.println("TimeService interrupted: " + e.getMessage());
// }