package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;

import java.util.*;

import bgu.spl.mics.application.messages.*;
//import bgu.spl.mics.application.objects.*;
//import bgu.spl.mics.MicroService;

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

    private final LiDarWorkerTracker liDarWorkerTracker;
    private final LiDarDataBase liDarDataBase;
    private int currentTick;
    private final Map<DetectObjectsEvent, Integer> pendingEvents = new HashMap<>();

    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, LiDarDataBase liDarDataBase, StatisticalFolder statisticalFolder) {
        super("liDar " + LiDarWorkerTracker.getId(), statisticalFolder);
        this.liDarWorkerTracker = LiDarWorkerTracker;
        this.liDarDataBase = liDarDataBase;
        this.currentTick = 0;
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
            processPendingAndReadyEvents(currentTick);
        });

        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {
            int detectionTime = event.getDetectedObjects().getTime();
            int scheduledTime = detectionTime + liDarWorkerTracker.getFrequency();

            if (scheduledTime <= currentTick) {
                // Process immediately if the scheduled time has already passed
                processEvent(event);
            } else {
                // Add to pending events with its scheduled time
                pendingEvents.put(event, scheduledTime);
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {

        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {

        });
    }


    private void processPendingAndReadyEvents(int currentTick) {
        List<TrackedObject> readyTrackedObjects = new ArrayList<>();
        Iterator<Map.Entry<DetectObjectsEvent, Integer>> iterator = pendingEvents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<DetectObjectsEvent, Integer> entry = iterator.next();
            DetectObjectsEvent event = entry.getKey();
            int scheduledTime = entry.getValue();

            if (currentTick >= scheduledTime) {
                readyTrackedObjects.addAll(liDarWorkerTracker.processDetectObjectsEvent(event, liDarDataBase));
                iterator.remove(); // Remove processed event
            }
        }

        // Send all ready objects in one TrackedObjectsEvent
        if (!readyTrackedObjects.isEmpty()) {
            sendEvent(new TrackedObjectsEvent(readyTrackedObjects));
            statisticalFolder.incrementTrackedObjects(readyTrackedObjects.size());
        }
    }


    /**
     * Processes a single DetectObjectsEvent immediately.
     *
     * @param event The DetectObjectsEvent to process.
     */
    private void processEvent(DetectObjectsEvent event) {
        List<TrackedObject> trackedObjects = liDarWorkerTracker.processDetectObjectsEvent(event, liDarDataBase);

        if (!trackedObjects.isEmpty()) {
            sendEvent(new TrackedObjectsEvent(trackedObjects));
            statisticalFolder.incrementTrackedObjects(trackedObjects.size());
        }
    }
}
