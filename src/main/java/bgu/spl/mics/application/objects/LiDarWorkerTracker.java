package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.ArrayList;
import java.util.List;

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

    // --------------------- constructor --------------------

    /**
     * Default constructor.
     * Initializes the LiDAR worker with default values.
     * 
     * @post {@code id == 0}
     * @post {@code frequency == 0}
     * @post {@code status == STATUS.UP}
     * @post {@code lastTrackedObjects.isEmpty() == true}
     */
    public LiDarWorkerTracker() {
        this.id = 0;
        this.frequency = 0;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new ArrayList<>();
    }


    /**
     * Parameterized constructor.
     * 
     * @param id The unique identifier for the worker.
     * @param frequency The frequency at which the worker operates.
     * @post {@code this.id == id}
     * @post {@code this.frequency == frequency}
     */
    public LiDarWorkerTracker(int id, int frequency){
        this.id = id;
        this.frequency = frequency;
    }

    // --------------------- methods --------------------

    /**
     * @return The unique identifier of the LiDAR worker.
     */
    public int getId() {return id;}


    /**
     * @return The frequency at which the worker operates.
     */
    public int getFrequency() {return frequency;}


    /**
     * @return The current status of the worker.
     */
    public STATUS getStatus() {return status;}


    /**
     * @return The list of objects last tracked by the worker.
     */
    public List<TrackedObject> getLastTrackedObjects() {return lastTrackedObjects;}


    /**
     * Sets the status of the worker.
     * 
     * @param status The new status to set.
     * @pre {@code status != null}
     * @post {@code this.status == status}
     */
    public void setStatus(STATUS status) {
        this.status = status;
    }
    

    /**
     * Adds a tracked object to the list of last tracked objects.
     * 
     * @param trackedObject The tracked object to add.
     * @pre {@code trackedObject != null}
     * @post {@code lastTrackedObjects.contains(trackedObject)}
     */
    public void addTrackedObject(TrackedObject trackedObject) {
        this.lastTrackedObjects.add(trackedObject);
    }



    /**
     * Processes a DetectObjectsEvent and returns a list of the corresponding TrackedObjects.
     * 
     * @param event The event to process.
     * @param liDarDataBase The database containing LiDAR data.
     * @return A list of tracked objects generated from the event.
     * @pre {@code event != null && liDarDataBase != null}
     * @post {@code result.size() >= 0}
     */
    public List<TrackedObject> processDetectObjectsEvent(DetectObjectsEvent event, LiDarDataBase liDarDataBase) {
        int detectionTime = event.getDetectedObjects().getTime();
        StampedDetectedObjects detectedObjects = event.getDetectedObjects();
        // Check if the worker is active and if the event is ready to process
        if (status == STATUS.UP) {
            List<TrackedObject> output = new ArrayList<>();
            for (DetectedObject d : detectedObjects.getDetectedObjectsList()) {
                String id = d.getId();
                String description = d.getDescription();
                CloudPoint[] points = liDarDataBase.getCloudPointsOfObject(id);
                if (points != null) {
                    TrackedObject trackedObject = new TrackedObject(id, detectionTime, description, points); // CHECK TIMING AGAIN
                    addTrackedObject(trackedObject);
                    output.add(trackedObject);
                }
            }
            return output;
        }
        return new ArrayList<>(); // No object was tracked
    }

    
    /**
     * @return A string representation of the LiDarWorkerTracker.
     */
    @Override
    public String toString() {
        return "LiDarWorkerTracker{" +
                "id=" + id +
                ", frequency=" + frequency +
                ", status=" + status +
                ", lastTrackedObjects=" + lastTrackedObjects +
                '}';
    }

}
