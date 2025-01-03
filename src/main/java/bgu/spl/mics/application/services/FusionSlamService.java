package bgu.spl.mics.application.services;


import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;
//
/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private final FusionSlam fusionSlam;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder statisticalFolder) {
        super("FusionSlam", statisticalFolder);
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            int addedLandmarks = fusionSlam.createLandMarks(event.getTrackedObjects());
            statisticalFolder.incrementLandmarks(addedLandmarks);
        });

        subscribeEvent(PoseEvent.class, event -> {
            fusionSlam.addPose(event.getPose());
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            System.out.println("FusionSlamService received TerminatedBroadcast. Writing output...");
            writeOutput("outputTEST.json");
            terminate();
        });

    }



    /**
     * Writes the system's final output to a JSON file.
     * @param filePath Path to the output file.
     */
    public void writeOutput(String filePath) {
        Map<String, Object> outputData = Map.of(
            "systemRuntime", statisticalFolder.getSystemRuntime(),
            "numDetectedObjects", statisticalFolder.getNumDetectedObjects(),
            "numTrackedObjects", statisticalFolder.getNumTrackedObjects(),
            "numLandmarks", fusionSlam.getLandMarkList().size(),
            "landMarks", fusionSlam.getLandMarkList() // Assumes getLandMarkMap returns the necessary map format
        );

        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(outputData, writer);
            System.out.println("Output file written successfully: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }

}
