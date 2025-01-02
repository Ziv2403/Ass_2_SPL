package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private final LiDarWorkerTracker liDarWorkerTracker;
    private final LiDarDataBase liDarDataBase;
    private int currentTick;
//    private final Map<DetectObjectsEvent, Integer> pendingEvents = new HashMap<>();
    private final Map<DetectObjectsEvent, Integer> pendingEvents = new ConcurrentHashMap<>();


    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, LiDarDataBase liDarDataBase, StatisticalFolder statisticalFolder) {
        super("liDar" + LiDarWorkerTracker.getId(), statisticalFolder);
        // TODO Implement this
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
        // TODO Implement this
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick = tick.getTick();
            System.out.println("lidartick: " + tick.getTick());
            System.out.println(getName() + ": Pending events: " + pendingEvents);

            processPendingAndReadyEvents(currentTick);
        });

        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {
            System.out.println("Received DetectedObjectsEvent from camera" + event.getCameraId());
            int detectionTime = event.getDetectedObjects().getTime();
            int scheduledTime = detectionTime + liDarWorkerTracker.getFrequency();
            System.out.println("detectionTime is: " + detectionTime + " and scheduledTime is: " + scheduledTime);

            if (scheduledTime <= currentTick) {
                processEvent(event); // Process immediately
            } else {
                pendingEvents.put(event, scheduledTime); // Schedule for later
                System.out.println(getName() + ": Added event to pendingEvents. Scheduled for tick: " + scheduledTime);
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            terminate();
        });

    }


//    private void processPendingAndReadyEvents(int currentTick) {
//        List<TrackedObject> readyTrackedObjects = new ArrayList<>();
//        Iterator<Map.Entry<DetectObjectsEvent, Integer>> iterator = pendingEvents.entrySet().iterator();
//
//
//        while (iterator.hasNext()) {
//            Map.Entry<DetectObjectsEvent, Integer> entry = iterator.next();
//            DetectObjectsEvent event = entry.getKey();
//            int scheduledTime = entry.getValue();
//
//            if (currentTick >= scheduledTime) {
//                List<TrackedObject> trackedObjects = liDarWorkerTracker.processDetectObjectsEvent(event, liDarDataBase);
//                if (trackedObjects != null && !trackedObjects.isEmpty()) {
//                    readyTrackedObjects.addAll(trackedObjects);
//                }
//                iterator.remove(); // Remove processed event
//            }
//
//            // Send all ready objects in one TrackedObjectsEvent
//            if (!readyTrackedObjects.isEmpty()) {
//                sendEvent(new TrackedObjectsEvent(readyTrackedObjects));
////                updateStatistics(readyTrackedObjects.size());
//                statisticalFolder.incrementTrackedObjects(readyTrackedObjects.size());
//            }
//        }
//    }

private void processPendingAndReadyEvents(int currentTick) {
    System.out.println(getName() + ": Processing pending events for tick: " + currentTick);
    List<TrackedObject> readyTrackedObjects = new ArrayList<>();

    Iterator<Map.Entry<DetectObjectsEvent, Integer>> iterator = pendingEvents.entrySet().iterator();
    while (iterator.hasNext()) {
        Map.Entry<DetectObjectsEvent, Integer> entry = iterator.next();
        DetectObjectsEvent event = entry.getKey();
        int scheduledTime = entry.getValue();

        System.out.println(getName() + ": Checking event scheduled for tick " + scheduledTime);
        if (currentTick >= scheduledTime) {
            List<TrackedObject> trackedObjects = liDarWorkerTracker.processDetectObjectsEvent(event, liDarDataBase);
            if (trackedObjects == null || trackedObjects.isEmpty()) {
                System.err.println(getName() + ": No tracked objects for event: " + event);
            } else {
                readyTrackedObjects.addAll(trackedObjects);
                System.out.println(getName() + ": Added " + trackedObjects.size() + " tracked objects.");
            }
            iterator.remove(); // Remove processed event
        }
    }

    if (!readyTrackedObjects.isEmpty()) {
        System.out.println(getName() + ": Sending TrackedObjectsEvent for " + readyTrackedObjects.size() + " objects.");
        sendEvent(new TrackedObjectsEvent(readyTrackedObjects));
        statisticalFolder.incrementTrackedObjects(readyTrackedObjects.size());
    } else {
        System.out.println(getName() + ": No tracked objects to send for current tick.");
    }
}

    /**
     * Processes a single DetectObjectsEvent immediately.
     *
     * @param event The DetectObjectsEvent to process.
     */
    private void processEvent(DetectObjectsEvent event) {
        List<TrackedObject> trackedObjects = liDarWorkerTracker.processDetectObjectsEvent(event, liDarDataBase);
        System.out.println("LIST CREATED " + trackedObjects);
        if (trackedObjects!= null && !trackedObjects.isEmpty()) {
            System.out.println("EVENT SENT?? ");
            sendEvent(new TrackedObjectsEvent(trackedObjects));
//            updateStatistics(trackedObjects.size());
            statisticalFolder.incrementTrackedObjects(trackedObjects.size());
        }
    }

    private void updateStatistics(int num) {
        for (int i = 0; i < num; i++) {
            statisticalFolder.incrementTrackedObjects();
        }
    }
}
