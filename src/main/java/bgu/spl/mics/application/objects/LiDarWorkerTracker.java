package bgu.spl.mics.application.objects;

//import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private  STATUS status;
    private  List<TrackedObject> lastTrackedObjects;
    private final BlockingQueue<StampedDetectedObjects> processingQueue; //Saves all data sent by the camera and not yet processed (processing at different frequencies)

    // --------------------- constructor --------------------
    public LiDarWorkerTracker(int id, int frequency, STATUS status ){
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new ArrayList<>();
        this.processingQueue = new LinkedBlockingQueue<>();
    }

    // --------------------- methods --------------------

    //Getters
    public int getId() {return id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}
    public List<TrackedObject> getLastTrackedObjects() {return lastTrackedObjects;}

    //Setter
    public void setStatus(STATUS status) {
        this.status = status;
    }
    
    //other Methods
    public void addTrackedObject(TrackedObject trackedObject) {
        this.lastTrackedObjects.add(trackedObject);
    }

    public void addDetectedObjects(StampedDetectedObjects objects) {
        processingQueue.offer(objects); 
    }
    
    // Process DetectObjectsEvent and return a TrackedObject
    public List<TrackedObject> processEvent( LiDarDataBase database, int currentTick) {
        if (status != STATUS.UP) {
            throw new IllegalStateException("Worker is DOWN or ERROR detected.");
        }

        List<TrackedObject> trackedObjects  = new ArrayList<>();
        StampedDetectedObjects detectedObjects;

        while ((detectedObjects = processingQueue.poll()) != null) {
            for (DetectedObject object : detectedObjects.getDetectedObjectsList()) {
                List<CloudPoint> cloudPoints = database.getCloudPoints(object.getId());
                if (!cloudPoints.isEmpty()) {
                    trackedObjects.add(new TrackedObject(object.getId(), cloudPoints, currentTick));
                }
            }
        }
        return trackedObjects;
    }

    public Collection<? extends TrackedObject> trackObjects(StampedDetectedObjects detectedObjects, int currentTick) {
        List<TrackedObject> trackedObjects = new ArrayList<>();
    
        for (DetectedObject object : detectedObjects.getDetectedObjectsList()) {
            String objectId = object.getId();
            List<CloudPoint> cloudPoints = LiDarDataBase.getInstance().getCloudPoints(objectId);
    
            // If cloud points are available, create a TrackedObject
            if (!cloudPoints.isEmpty()) {
                int detectionTime = detectedObjects.getTime();
                int scheduledTime = detectionTime + getFrequency();
    
                // Only add to tracked objects if it is ready to be processed
                if (scheduledTime <= currentTick) {
                    trackedObjects.add(new TrackedObject(objectId, detectionTime, object.getDescription(), cloudPoints));
                }
            }
        }
    
        // Update internal state with newly tracked objects
        lastTrackedObjects.clear();
        lastTrackedObjects.addAll(trackedObjects);
    
        return trackedObjects;
    }
    

    // public TrackedObject processDetectObjectsEvent(DetectObjectsEvent event, LiDarDataBase liDarDataBase) {
    //     int detectionTime = event.getDetectedObjects().getTime();
    //     int scheduledTime = detectionTime + frequency;
    //     // Check if the worker is active and if the event is ready to process
    //     if (status == STATUS.UP) {
    //         List<CloudPoint> cloudPoints = liDarDataBase.getCloudPoints(event.getObjectId());
    //         if (!cloudPoints.isEmpty()) {
    //             TrackedObject trackedObject = new TrackedObject(event.getObjectId(), cloudPoints, scheduledTime);
    //             addTrackedObject(trackedObject);
    //             return trackedObject;
    //         }
    //     }
    //     return null; // No object was tracked
    // }

}
