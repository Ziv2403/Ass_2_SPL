package bgu.mics.spl;

import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.LiDarDataBase;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.TrackedObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LiDarWorkerTrackerTest {

    private LiDarWorkerTracker lidarWorker;
    private LiDarDataBase liDarDataBase;
    private DetectObjectsEvent event;

    @BeforeEach
    void setUp() {
        // Initialize a LiDarWorkerTracker with an ID and frequency
        lidarWorker = new LiDarWorkerTracker(1, 2);

        // Mock LiDarDataBase
        liDarDataBase = new LiDarDataBase();

        // Mock detected objects
        StampedDetectedObjects detectedObjects = new StampedDetectedObjects(3, Arrays.asList(
                new DetectedObject("Object1", "Wall"),
                new DetectedObject("Object2", "Chair")
        ));

        // Mock event with detected objects
        event = new DetectObjectsEvent(detectedObjects, 1);

        // Mock LiDar database responses
        liDarDataBase.addCloudPoints("Object1", Arrays.asList(
                new CloudPoint(0.5, 3.9),
                new CloudPoint(0.2, 3.7)
        ));

        liDarDataBase.addCloudPoints("Object2", Arrays.asList(
                new CloudPoint(1.1, 2.5),
                new CloudPoint(1.3, 2.6)
        ));
    }

    @Test
    void testProcessDetectObjectsEvent_WithValidEvent() {
        // Ensure the LiDAR worker is active
        lidarWorker.setStatus(STATUS.UP);

        // Process the event
        List<TrackedObject> trackedObjects = lidarWorker.processDetectObjectsEvent(event, liDarDataBase);

        // Validate results
        assertNotNull(trackedObjects, "TrackedObjects list should not be null");
        assertEquals(2, trackedObjects.size(), "Should track two objects");

        // Validate first tracked object
        TrackedObject first = trackedObjects.get(0);
        assertEquals("Object1", first.getId(), "First tracked object ID should match");
        assertEquals("Wall", first.getDescription(), "First tracked object description should match");
        assertEquals(3, first.getTime(), "Tracked object time should match detection time");

        // Validate second tracked object
        TrackedObject second = trackedObjects.get(1);
        assertEquals("Object2", second.getId(), "Second tracked object ID should match");
        assertEquals("Chair", second.getDescription(), "Second tracked object description should match");

        // Validate lastTrackedObjects list in LiDarWorkerTracker
        assertEquals(2, lidarWorker.getLastTrackedObjects().size(), "lastTrackedObjects should store two entries");
    }

    @Test
    void testProcessDetectObjectsEvent_WhenLiDarWorkerIsDown() {
        // Set LiDAR worker to DOWN status
        lidarWorker.setStatus(STATUS.DOWN);

        // Process the event
        List<TrackedObject> trackedObjects = lidarWorker.processDetectObjectsEvent(event, liDarDataBase);

        // Validate that no objects are tracked when LiDAR is down
        assertNotNull(trackedObjects, "TrackedObjects list should not be null");
        assertEquals(0, trackedObjects.size(), "No objects should be tracked when LiDAR is down");
    }

    @Test
    void testProcessDetectObjectsEvent_WithNoCloudPointsInDatabase() {
        // Remove all cloud points from the database
        liDarDataBase.clear();

        // Process the event
        List<TrackedObject> trackedObjects = lidarWorker.processDetectObjectsEvent(event, liDarDataBase);

        // Validate that no objects are tracked if no cloud points exist
        assertNotNull(trackedObjects, "TrackedObjects list should not be null");
        assertEquals(0, trackedObjects.size(), "No objects should be tracked if LiDar database has no cloud points");
    }

    @Test
    void testProcessDetectObjectsEvent_WithNullEvent() {
        assertThrows(NullPointerException.class, () -> {
            lidarWorker.processDetectObjectsEvent(null, liDarDataBase);
        }, "Processing a null event should throw NullPointerException");
    }

    @Test
    void testProcessDetectObjectsEvent_WithNullDatabase() {
        assertThrows(NullPointerException.class, () -> {
            lidarWorker.processDetectObjectsEvent(event, null);
        }, "Processing with a null LiDar database should throw NullPointerException");
    }
}
