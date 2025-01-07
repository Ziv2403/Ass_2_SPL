package bgu.spl.mics;

import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FusionSlamTest {

    private FusionSlam fusionSlam;
    private StatisticalFolder statisticalFolder;
    private Pose pose;
    private CloudPoint localPoint;
    private TrackedObject trackedObject;

    @BeforeEach
    public void setUp() {
        fusionSlam = FusionSlam.getInstance();
        statisticalFolder = new StatisticalFolder(); 
        fusionSlam.getPoseList().clear(); // Reset poses for clean testing
        fusionSlam.getLandMarkList().clear(); // Reset landmarks for clean testing

        // Initialize a pose
        pose = new Pose(10, 15, 45, 1); // x=10, y=15, yaw=45 degrees, time=1

        // Initialize a local point
        localPoint = new CloudPoint(5, 5); // Local coordinates x=5, y=5

        // Initialize a tracked object
        CloudPoint[] coordinates = {
            new CloudPoint(1, 1),
            new CloudPoint(2, 2)
        };
        trackedObject = new TrackedObject("Object1", 1, "Test Object", coordinates);
    }

    @Test
    public void testAddPose() {
        fusionSlam.addPose(pose);
        assertEquals(1, fusionSlam.getPoseList().size(), "Pose list size should be 1.");
        assertEquals(pose, fusionSlam.getPoseList().get(0), "Pose should be added correctly.");
    }

    @Test //The method that transforms tracked objects to landmarks
    public void testCreateLandMarksNewObject() {
        fusionSlam.addPose(pose); // Add pose
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(trackedObject);

        int newLandmarks = fusionSlam.createLandMarks(trackedObjects);

        assertEquals(1, newLandmarks, "Should create 1 new landmark.");
        assertEquals(1, fusionSlam.getLandMarkList().size(), "Landmark list size should be 1.");

        LandMark createdLandmark = fusionSlam.getLandMarkList().get(0);
        assertEquals("Object1", createdLandmark.getId(), "Landmark ID should match tracked object ID.");
        assertEquals("Test Object", createdLandmark.getDescription(), "Landmark description should match tracked object description.");
        assertEquals(2, createdLandmark.getCloudPoints().size(), "Landmark should have 2 cloud points.");
    }

    @Test
    public void testUpdateExistingLandmark() {
        fusionSlam.addPose(pose); // Add pose

        // Create and add a tracked object
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(trackedObject);
        fusionSlam.createLandMarks(trackedObjects);

        // Add another object with the same ID
        CloudPoint[] updatedCoordinates = {
            new CloudPoint(3, 3),
            new CloudPoint(4, 4)
        };
        TrackedObject updatedTrackedObject = new TrackedObject("Object1", 1, "Updated Test Object", updatedCoordinates);
        trackedObjects.clear();
        trackedObjects.add(updatedTrackedObject);

        int newLandmarks = fusionSlam.createLandMarks(trackedObjects);

        assertEquals(0, newLandmarks, "Should not create a new landmark.");
        assertEquals(1, fusionSlam.getLandMarkList().size(), "Landmark list size should still be 1.");

        LandMark updatedLandmark = fusionSlam.getLandMarkList().get(0);
        assertEquals(4, updatedLandmark.getCloudPoints().size(), "Landmark should now have 4 cloud points.");
    }

    @Test
    public void testNoMatchingPose() {
        // Add tracked object without adding matching pose
        List<TrackedObject> trackedObjects = new ArrayList<>();
        trackedObjects.add(trackedObject);

        int newLandmarks = fusionSlam.createLandMarks(trackedObjects);

        assertEquals(0, newLandmarks, "No landmarks should be created without a matching pose.");
        assertEquals(0, fusionSlam.getLandMarkList().size(), "Landmark list should remain empty.");
    }

    @Test
    public void testConvertPointToGlobalCoordinates() {
        Pose pose = new Pose(10, 15, 45, 1); // Initialize pose
        CloudPoint globalPoint = fusionSlam.convertPoint(localPoint, pose);
    
        // Expected global coordinates based on local point and pose transformation
        double expectedX = 10 + (5 * Math.cos(Math.toRadians(45)) - 5 * Math.sin(Math.toRadians(45)));
        double expectedY = 15 + (5 * Math.sin(Math.toRadians(45)) + 5 * Math.cos(Math.toRadians(45)));

        assertEquals(expectedX, globalPoint.getX(), 0.001, "X coordinate should be correctly transformed to global.");
        assertEquals(expectedY, globalPoint.getY(), 0.001, "Y coordinate should be correctly transformed to global.");
    }

    @Test
    public void testAddOrUpdateLandMark() {
        // Create new LandMark and add it
        LandMark landMark = new LandMark("L1", "Test Landmark", new ArrayList<>());
        CloudPoint point1 = new CloudPoint(1.0, 1.0);
        List<CloudPoint> initialPoints = new ArrayList<>();
        initialPoints.add(point1);
        landMark.setCloudPoints(initialPoints);
    
        statisticalFolder.addOrUpdateLandMark(landMark.getId(), landMark);
    
        // Verify addition
        assertTrue(statisticalFolder.getLandMarks().containsKey("L1"), "LandMark should be added.");
        assertEquals(1, statisticalFolder.getLandMarks().get("L1").getCloudPoints().size(), "LandMark should have 1 cloud point.");
        assertEquals("Test Landmark", statisticalFolder.getLandMarks().get("L1").getDescription(), "LandMark description should remain unchanged.");
    
        // Update the existing LandMark
        CloudPoint point2 = new CloudPoint(2.0, 2.0);
        List<CloudPoint> updatedPoints = new ArrayList<>();
        updatedPoints.add(point2);
        LandMark updatedLandMark = new LandMark("L1", "Updated Test Landmark", updatedPoints);
    
        statisticalFolder.addOrUpdateLandMark(updatedLandMark.getId(), updatedLandMark);
    
        // Verify update
        assertEquals(2, statisticalFolder.getLandMarks().get("L1").getCloudPoints().size(), "LandMark should now have 2 cloud points.");
        assertEquals("Test Landmark", statisticalFolder.getLandMarks().get("L1").getDescription(), "LandMark description should remain unchanged.");
    }
    
}
