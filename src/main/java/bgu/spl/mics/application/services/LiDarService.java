package bgu.spl.mics.application.services;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;

//
/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 * • Responsibilities:
 * o Sends TrackedObjectsEvents (can be multiple events).
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker workerTracker;
    private final LiDarDataBase liDarDataBase;//Extract data from the database to a queue
    private final StatisticalFolder statisticalFolder;
    private int currentTick = 0;
    private final BlockingQueue<StampedDetectedObjects> dataQueue;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */

    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, LiDarDataBase liDarDataBase, StatisticalFolder statisticalFolder) {
        super("liDar " + LiDarWorkerTracker.getId());
        this.workerTracker = LiDarWorkerTracker;
        this.liDarDataBase = liDarDataBase;
        this.statisticalFolder = statisticalFolder;
        this.dataQueue = new LinkedBlockingQueue<>();
    }


    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick = tick.getTick();

            // Process data at the LiDAR's frequency
            if (currentTick % workerTracker.getFrequency() == 0) {
                processQueuedData();
            }
        });

        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {
            try {
                // Add detected objects to the queue for later processing
                dataQueue.offer(event.getDetectedObjects());
            } catch (Exception e) {
                System.err.println(getName() + ": Failed to queue detected objects: " + e.getMessage());
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
            }
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> terminate());
    }

    /**
     * Processes the queued data based on the LiDAR worker's frequency.
     * Prepares and sends objects to Fusion if their detection time plus frequency <= current tick.
     */
    private void processQueuedData() {
        try {
            List<TrackedObject> readyObjects = new ArrayList<>();

            while (!dataQueue.isEmpty()) {
                StampedDetectedObjects detectedObjects = dataQueue.peek();

                // Check if the object is ready to be processed
                if (detectedObjects != null && detectedObjects.getTime() + workerTracker.getFrequency() <= currentTick) {
                    detectedObjects = dataQueue.poll(); // Remove from queue
                    readyObjects.addAll(workerTracker.trackObjects(detectedObjects, currentTick));
                } else {
                    break; // Stop processing if the next object is not ready yet
                }
            }

            if (!readyObjects.isEmpty()) {
                // Send a single TrackedObjectsEvent containing all ready objects
                sendEvent(new TrackedObjectsEvent(readyObjects));

                // Update statistics
                statisticalFolder.incrementTrackedObjects(readyObjects.size());
            }
        } catch (IllegalStateException e) {
            System.err.println(getName() + ": Worker encountered an error during processing: " + e.getMessage());
            sendBroadcast(new CrashedBroadcast(getName()));
            terminate(); // Terminate the service due to error
        }
    }
}
