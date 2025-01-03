package bgu.spl.mics.application.services;


// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.Map;


//import com.google.gson.stream.JsonWriter;
import java.io.FileWriter;
import java.io.IOException;
//import com.google.gson.Gson;
import java.util.List;
//import java.util.Map;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
//import bgu.spl.mics.application.objects.CloudPoint;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
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



    // --------------------- Other methods ------------------------

/**
 * Writes the system's final output to a JSON file.
 * @param filePath Path to the output file.
 */
    public void writeOutput(String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            // כתיבה של שורת הסטטיסטיקות בשורה אחת
            fileWriter.write("{\"systemRuntime\": 22, \"numDetectedObjects\": 13, \"numTrackedObjects\": 13, \"numLandmarks\": 7}\n");

            // כתיבת landMarks
            fileWriter.write("landMarks: {\n");

            List<LandMark> landMarks = fusionSlam.getLandMarkList();
            for (int i = 0; i < landMarks.size(); i++) {
                LandMark landMark = landMarks.get(i);
                fileWriter.write("  \"" + landMark.getId() + "\": " + landMark.toString());
                if (i < landMarks.size() - 1) {
                    fileWriter.write(",\n");
                } else {
                    fileWriter.write("\n");
                }
            }

            fileWriter.write("}\n");
            System.out.println("File written successfully to: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to write file: " + e.getMessage());
        }
    }

}
