package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
    private Map<String, LandMark> landMarks; // Map of LandMarks by their unique ID

// --------------------- SingletonImplemment -------------------------
    // private static class StatisticalFolderHolder {
    //     private static final StatisticalFolder INSTANCE = new StatisticalFolder();
    // }

    public StatisticalFolder() { } // Private constructor for Singleton

    // public static StatisticalFolder getInstance() {
    //     return StatisticalFolderHolder.INSTANCE;
    //}

    // --------------------- methods ------------------------
//Getters - get() => Gets the current value.
    public int getSystemRuntime() {return systemRuntime.get();}

    public int getNumDetectedObjects() {return numDetectedObjects.get();}

    public int getNumTrackedObjects() {return numTrackedObjects.get();}

    public int getNumLandmarks() {return numLandmarks.get();}

    //Adders - incrementAndGet() => Atomically increments by one the current value.
    public void incrementSystemRuntime() {
        systemRuntime.incrementAndGet();
    }

    public void incrementDetectedObjects() {
        numDetectedObjects.incrementAndGet();
    }

    public void incrementTrackedObjects() {
        numTrackedObjects.incrementAndGet();
    }

    public void incrementLandmarks() {
        numLandmarks.incrementAndGet();
    }
    //toString:
    @Override
    public synchronized String toString() {
        return "StatisticalFolder{ systemRuntime=" + systemRuntime +
                ", numDetectedObjects=" + numDetectedObjects +
                ", numTrackedObjects=" + numTrackedObjects +
                ", numLandmarks=" + numLandmarks + '}';
    }

    public void incrementDetectedObjects(int size) {
        numDetectedObjects.addAndGet(size);
    }

    public void incrementTrackedObjects(int size) {
        numTrackedObjects.addAndGet(size);
    }

    public void incrementLandmarks(int size) {
        numLandmarks.addAndGet(size);
    }

    /**
    * Adds a new LandMark to the system or updates an existing one.
    * Ensures no duplicate CloudPoints are added to the LandMark.
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
                // No existing landmark with this ID, add new one
                return newLandMark;
            } else {
                // Update existing landmark by merging cloud points without duplicates
                Set<CloudPoint> existingPointsSet = new HashSet<>(existingLandMark.getCloudPoints());
                for (CloudPoint newPoint : newLandMark.getCloudPoints()) {
                    if (existingPointsSet.add(newPoint)) {
                        existingPointsSet.add(newPoint);
                    }
                }

                existingLandMark.setCloudPoints(existingPointsSet);
                return existingLandMark;
            }
        });
    }


}
