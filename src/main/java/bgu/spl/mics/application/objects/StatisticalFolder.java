package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    // --------------------- fields -------------------------
    /**
     * AtomicInteger is used to ensure thread-safe and efficient updates to counters.
     * It eliminates the need for explicit synchronization or locks,
     * making it ideal for handling frequent updates in a concurrent environment.
     */
    private final AtomicInteger systemRuntime = new AtomicInteger(0);
    private final AtomicInteger numDetectedObjects = new AtomicInteger(0);
    private final AtomicInteger numTrackedObjects = new AtomicInteger(0);
    private final AtomicInteger numLandmarks = new AtomicInteger(0);

    //The use of the ConcurrentHashMap data structure for camera and LIDAR frames ensures efficient and safe writing and reading from multiple threads.
    private final ConcurrentHashMap<String, StampedDetectedObjects> lastCameraFrames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TrackedObject> lastLiDarFrames = new ConcurrentHashMap<>();
    
    private final Map<String, LandMark> landMarks = new HashMap<>();
    private final List<Pose> poseOutput = new ArrayList<>();
    //When there is a main thread that performs writing, and another thread that reads the data occasionally.
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(); 


    // --------------------- SingletonImplemment -------------------------

    /**
     * Default constructor for the StatisticalFolder.
     * Initializes all counters to zero.
     */
    public StatisticalFolder() { } 

    // --------------------- methods ------------------------

    /**
     * @return The total runtime of the system in ticks.
     */
    public int getSystemRuntime() {return systemRuntime.get();}


    /**
     * @return The total number of objects detected by the system.
     */
    public int getNumDetectedObjects() {return numDetectedObjects.get();}


    /**
     * @return The total number of objects tracked by the system.
     */
    public int getNumTrackedObjects() {return numTrackedObjects.get();}


    /**
     * @return The total number of unique landmarks identified by the system.
     */
    public int getNumLandmarks() {return numLandmarks.get();}


    /**
     * Increments the system runtime by one tick.
     * @post {@code getSystemRuntime() == \old(getSystemRuntime()) + 1}
     */

    public void incrementSystemRuntime() {
        systemRuntime.incrementAndGet();
    }

    
    /**
     * Increments the total number of detected objects by one.
     * @post {@code getNumDetectedObjects() == \old(getNumDetectedObjects()) + 1}
     */
    public void incrementDetectedObjects() {
        numDetectedObjects.incrementAndGet();
    }


    /**
     * Increments the total number of detected objects by a specific value.
     * 
     * @param size The number of detected objects to add.
     * @pre {@code size >= 0}
     * @post {@code getNumDetectedObjects() == \old(getNumDetectedObjects()) + size}
     */
    public void incrementDetectedObjects(int size) {
        numDetectedObjects.addAndGet(size);
    }


    /**
     * Increments the total number of tracked objects by one.
     * @post {@code getNumTrackedObjects() == \old(getNumTrackedObjects()) + 1}
     */
    public void incrementTrackedObjects() {
        numTrackedObjects.incrementAndGet();
    }


    /**
     * Increments the total number of tracked objects by a specific value.
     * 
     * @param size The number of tracked objects to add.
     * @pre {@code size >= 0}
     * @post {@code getNumTrackedObjects() == \old(getNumTrackedObjects()) + size}
     */
    public void incrementTrackedObjects(int size) {
        numTrackedObjects.addAndGet(size);
    }


    /**
     * Increments the total number of landmarks by one.
     * @post {@code getNumLandmarks() == \old(getNumLandmarks()) + 1}
     */
    public void incrementLandmarks() {
        numLandmarks.incrementAndGet();
    }


    /**
     * Increments the total number of landmarks by a specific value.
     * 
     * @param size The number of landmarks to add.
     * @pre {@code size >= 0}
     * @post {@code getNumLandmarks() == \old(getNumLandmarks()) + size}
     */
    public void incrementLandmarks(int size) {
        numLandmarks.addAndGet(size);
    }


/**
     * Adds a frame for a specific camera.
     *
     * @param cameraKey The key of the camera.
     * @param frame The detected objects frame to add.
     * @pre {@code cameraKey != null && frame != null}
     * @post The frame is added to the lastCameraFrames map.
     */
    public void addCameraFrame(String cameraKey, StampedDetectedObjects frame) {
        lastCameraFrames.put(cameraKey, frame);
    }


    /**
     * Retrieves the last frames of all cameras.
     *
     * @return A map containing the last frames of all cameras.
     */
    public Map<String, StampedDetectedObjects> getLastCameraFrames() {
        return lastCameraFrames;
    }

    /**
     * Retrieves the last frames of all LiDARs.
     *
     * @return A map containing the last frames of all LiDARs.
     */
    public Map<String, TrackedObject> getLastLiDarFrames() {
        return lastLiDarFrames;
    }

    
    /**
     * Adds a frame for a specific LiDAR.
     *
     * @param liDarKey The key of the LiDAR.
     * @param frame The cloud points frame to add.
     * @pre {@code liDarKey != null && frame != null}
     * @post The frame is added to the lastLiDarFrames map.
     */
    public void addLiDarFrame(String liDarKey, TrackedObject frame) {
        lastLiDarFrames.put(liDarKey, frame);
    }


    /**
     * Adds a Pose to the output string.
     *
     * @param pose The Pose to add.
     */
    public void addPose(Pose pose) {
        lock.writeLock().lock(); //  Write Block
        try {
            poseOutput.add(pose);
        } finally {
            lock.writeLock().unlock(); // Write Release
        }
    }

    /**
     * Retrieves the Pose output as a single string.
     *
     * @return A string containing all the Poses.
     */
    public List<Pose> getPoseOutput() {
        lock.readLock().lock(); // Read Block
        try {
            return poseOutput;
        } finally {
            lock.readLock().unlock(); // Read Release
        }
    }

    /**
    * Retrieves all the LandMarks stored in the system.
    * 
    * @return A map of LandMarks where the key is the ID of the LandMark, 
    *         and the value is the LandMark object.
    * @post {@code result != null}
    */
    public Map<String, LandMark> getLandMarks() {
        return landMarks;
    }


    /**
    * Adds a new LandMark to the system or updates an existing one.
    *
    * @param id The unique identifier of the LandMark.
    * @param newLandMark The LandMark object to be added or updated.
    * @pre {@code id != null && !id.isEmpty()}
    * @pre {@code newLandMark != null}
    * @post {@code landMarks.containsKey(id)}
    */
    public void addOrUpdateLandMark(String id, LandMark newLandMark) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("LandMark ID cannot be null or empty.");
        }
        if (newLandMark == null) {
            throw new IllegalArgumentException("LandMark cannot be null.");
        }

        landMarks.compute(id, (key, existingLandMark) -> {
            if (existingLandMark == null) {
                // No existing landmark with this ID, add the new one
                return newLandMark;
            } else {
                // Update existing landmark by merging unique cloud points
                List<CloudPoint> existingPoints = existingLandMark.getCloudPoints();
                List<CloudPoint> newPoints = newLandMark.getCloudPoints();

                // Add only new points that don't already exist in the list
                List<CloudPoint> uniqueNewPoints = newPoints.stream()
                        .filter(point -> !existingPoints.contains(point))
                        .collect(Collectors.toList());

                existingPoints.addAll(uniqueNewPoints);
                existingLandMark.setCloudPoints(existingPoints);
                return existingLandMark;
            }
         });

    }


     /**
     * @return A string representation of the StatisticalFolder object in JSON output format
     */
    @Override
    public synchronized String toString() {
        return "{" + '"'+"systemRuntime"+'"'+":" + systemRuntime +
                ","  + " numDetectedObjects:" + numDetectedObjects +
                "," + '"'+ " numTrackedObjects"+'"'+":" + numTrackedObjects +
                "," + '"'+ "numLandmarks"+'"'+":" + numLandmarks + ',';
    }
}
