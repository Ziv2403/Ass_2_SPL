package bgu.spl.mics;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.TrackedObject;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


public class FusionSlamTest {
    private FusionSlam fusionSlam;

    @BeforeEach
    public void setUp() {
        fusionSlam = FusionSlam.getInstance(); 
        fusionSlam.clear(); 
    }


    @Test
public void testCreateLandMarks_NewLandmarks() {
    // Arrange
    Pose pose = new Pose(1.0, 2.0, 0.5, 1);
    fusionSlam.addPose(pose); 

    TrackedObject trackedObject = new TrackedObject("obj1", 1, "Test Object", List.of(new CloudPoint(3.0, 4.0)));

    // Act
    int newLandmarks = fusionSlam.createLandMarks(List.of(trackedObject));

    // Assert
    assertEquals(1, newLandmarks, "Should add one new landmark");
    LandMark landmark = fusionSlam.getLandMarkById("obj1");
    assertNotNull(landmark, "Landmark should be created");
    assertEquals("Test Object", landmark.getDescription(), "Description should match");
    assertEquals(1, landmark.getCloudPoints().size(), "Should have one cloud point");
    assertEquals(4.0, landmark.getCloudPoints().get(0).getY(), 0.01, "Y coordinate should match");
}

}
