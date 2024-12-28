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

// --------------------- Singleton Holder -------------------------
    private static class StatisticalFolderHolder {
        private static final StatisticalFolder INSTANCE = new StatisticalFolder();
    }

    private StatisticalFolder() { } // Private constructor for Singleton

    public static StatisticalFolder getInstance() {
        return StatisticalFolderHolder.INSTANCE;
    }

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
    public String toString() {
        return "StatisticalFolder{ systemRuntime=" + systemRuntime +
                ", numDetectedObjects=" + numDetectedObjects +
                ", numTrackedObjects=" + numTrackedObjects +
                ", numLandmarks=" + numLandmarks + '}';
    }

}
