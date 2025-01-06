package bgu.spl.mics.application.services;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;


/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    // --------------------- fields -------------------------
    private final FusionSlam fusionSlam;

    // --------------------- constructors -------------------

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder statisticalFolder) {
        super("FusionSlam", statisticalFolder);
        this.fusionSlam = fusionSlam;
    }

    // --------------------- initialize -------------------

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        subscribeEvent(TrackedObjectsEvent.class, event -> {
            int addedLandmarks = fusionSlam.createLandMarks(event.getTrackedObjects());

            for (LandMark landMark : fusionSlam.getLandMarkList()) {
                statisticalFolder.addOrUpdateLandMark(landMark.getId(), landMark);
            }

            statisticalFolder.incrementLandmarks(addedLandmarks);
        });

        subscribeEvent(PoseEvent.class, event -> {
            fusionSlam.addPose(event.getPose());
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
            System.out.println(getName() + " received CrashedBroadcast and is terminating.");

        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            System.out.println("FusionSlamService received TerminatedBroadcast. Writing output...");
            writeSimulationOutput("output_TEST.json", statisticalFolder);
            terminate();
        });
    }


    public static void writeSimulationOutput(String fileName, StatisticalFolder stats) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("{");

            writer.write("\"systemRuntime\":" + stats.getSystemRuntime() + ",");
            writer.write("\"numDetectedObjects\":" + stats.getNumDetectedObjects() + ",");
            writer.write("\"numTrackedObjects\":" + stats.getNumTrackedObjects() + ",");
            writer.write("\"numLandmarks\":" + stats.getNumLandmarks() + ",\n");

            writer.write("\"landMarks\":{");
            writer.write("\n");

            Map<String, LandMark> landMarks = stats.getLandMarks();
            int landMarkCount = landMarks.size();

            for (Map.Entry<String, LandMark> entry : landMarks.entrySet()) {
                String id = entry.getKey();
                LandMark landMark = entry.getValue();

                writer.write("    \"" + id + "\":{");
                writer.write("\"id\":\"" + landMark.getId() + "\",");
                writer.write("\"description\":\"" + landMark.getDescription() + "\",");
                writer.write("\"coordinates\":[");

                List<CloudPoint> cloudPoints = landMark.getCloudPoints();
                for (int i = 0; i < cloudPoints.size(); i++) {
                    CloudPoint point = cloudPoints.get(i);
                    writer.write("{\"x\":" + point.getX() + ",\"y\":" + point.getY() + "}");
                    if (i < cloudPoints.size() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("]}");

                if (--landMarkCount > 0) {
                    writer.write(", \n");
                }

            }
            writer.write("\n");

            writer.write("    }\n");
            writer.write("}");
        } catch (IOException e) {
            System.err.println("Error writing compact simulation output: " + e.getMessage());
        }
    }
}


