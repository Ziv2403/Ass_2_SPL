package bgu.spl.mics.application.services;

import bgu.spl.mics.application.utils.ErrorLogger;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


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
    // --------------------- fields -------------------------
    private final LiDarWorkerTracker liDarWorkerTracker;
    private final LiDarDataBase liDarDataBase;
    private int currentTick;
    private final Map<DetectObjectsEvent, Integer> pendingEvents = new ConcurrentHashMap<>();

    // --------------------- constructors -------------------


    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     * @param liDarDataBase The LiDAR database for retrieving cloud point data.
     * @param statisticalFolder The StatisticalFolder for tracking system statistics.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, LiDarDataBase liDarDataBase, StatisticalFolder statisticalFolder) {
        super("liDar" + LiDarWorkerTracker.getId(), statisticalFolder);
        this.liDarWorkerTracker = LiDarWorkerTracker;
        this.liDarDataBase = liDarDataBase;
        this.currentTick = 0;
    }

    // --------------------- initialize ------------------------

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        int errorTime = validateLiDarDatabaseForErrors();

        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick = tick.getTick();

            if (errorTime != -1 && errorTime == currentTick) {
                handleLiDarError(currentTick); 
                sendBroadcast(new CrashedBroadcast(String.valueOf(liDarWorkerTracker.getId()), "Detected error in LiDAR"));
                terminate();
            }
            
            if(!pendingEvents.isEmpty()){
                processPendingAndReadyEvents(currentTick);
            }


        });

        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {
            int detectionTime = event.getDetectedObjects().getTime();
            int scheduledTime = detectionTime + liDarWorkerTracker.getFrequency();

            if (scheduledTime <= currentTick) {
                processEvent(event); // Process immediately
            } else {
                pendingEvents.put(event, scheduledTime); // Schedule for later
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            liDarWorkerTracker.setStatus(STATUS.DOWN);
            terminate();
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            liDarWorkerTracker.setStatus(STATUS.DOWN);
            terminate();
        });

    }

    // --------------------- methods ------------------------
    /**
     * Processes all pending events that are ready to be handled at the current tick.
     *
     * @param currentTick The current tick of the simulation.
     */
    private void processPendingAndReadyEvents(int currentTick) {
        List<TrackedObject> readyTrackedObjects = new ArrayList<>();
        Iterator<Map.Entry<DetectObjectsEvent, Integer>> iterator = pendingEvents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<DetectObjectsEvent, Integer> entry = iterator.next();
            DetectObjectsEvent event = entry.getKey();
            int scheduledTime = entry.getValue();

            if (currentTick >= scheduledTime) {
                List<TrackedObject> trackedObjects = liDarWorkerTracker.processDetectObjectsEvent(event, liDarDataBase);

                if (trackedObjects != null && !trackedObjects.isEmpty()) {
                    readyTrackedObjects.addAll(trackedObjects);
                }

                iterator.remove(); // Remove processed event
            }
        }

        if (!readyTrackedObjects.isEmpty()) {
            TrackedObjectsEvent newEvent = new TrackedObjectsEvent(readyTrackedObjects);
            sendEvent(newEvent);
            statisticalFolder.incrementTrackedObjects(readyTrackedObjects.size());
            statisticalFolder.addLiDarFrame(liDarWorkerTracker.getLiDarKey(), newEvent.getTrackedObjects().get(newEvent.getTrackedObjects().size() - 1));//NOT SURE ABOUT THE PARAMETERS CORRECTNESS

        } 
    }   

    /**
     * Processes a single DetectObjectsEvent immediately.
     *
     * @param event The DetectObjectsEvent to process.
     */
    private void processEvent(DetectObjectsEvent event) {
        List<TrackedObject> trackedObjects = liDarWorkerTracker.processDetectObjectsEvent(event, liDarDataBase);
        if (trackedObjects!= null && !trackedObjects.isEmpty()) {
            TrackedObjectsEvent newEvent = new TrackedObjectsEvent(trackedObjects);
            sendEvent(newEvent);
            statisticalFolder.incrementTrackedObjects(trackedObjects.size());
            statisticalFolder.addLiDarFrame(liDarWorkerTracker.getLiDarKey(), newEvent.getTrackedObjects().get(newEvent.getTrackedObjects().size() - 1));//NOT SURE ABOUT THE PARAMETERS CORRECNESS
        }
    }

    /**
    * Handles an error detected in a LiDAR event.
    *
    * @param currentTick The current tick of the simulation.
    */
    private void handleLiDarError(int currentTick) {
        liDarWorkerTracker.setStatus(STATUS.ERROR);
        ErrorLogger.writeErrorReport( "error_output.json","LiDAR Disconnected", liDarWorkerTracker.getLiDarKey(),statisticalFolder);
    }

    /**
    * Validates the LiDAR database for any errors (e.g., ID with "ERROR").
    * If an error is found, returns the timestamp of the erroneous data.
    * Otherwise, returns -1.
    *
    * @return The timestamp of the erroneous data, or -1 if no errors are found.
    */
    private int validateLiDarDatabaseForErrors() {
        for (StampedCloudPoints cloudPoints : liDarDataBase.getCloudPoints()) {
            if (cloudPoints.isContainError()) { 
                return cloudPoints.getTime(); 
            }
        }
        return -1;
    }

}
