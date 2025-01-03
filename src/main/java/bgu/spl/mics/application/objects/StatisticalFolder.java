package bgu.spl.mics.application.objects;

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
     * @return A string representation of the StatisticalFolder object.
     */
    @Override
    public synchronized String toString() {
        return "StatisticalFolder{ systemRuntime=" + systemRuntime +
                ", numDetectedObjects=" + numDetectedObjects +
                ", numTrackedObjects=" + numTrackedObjects +
                ", numLandmarks=" + numLandmarks + '}';
    }







}
