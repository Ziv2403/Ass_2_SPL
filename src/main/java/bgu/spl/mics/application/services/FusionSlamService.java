package bgu.spl.mics.application.services;


// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.JsonElement;
// import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.CloudPoint;
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
            writeOutputFile("output_TEST.json");
            terminate();
        });
    }



    // --------------------- Other methods ------------------------

    // /**
    // * Creates and writes the simulation's output to a JSON file.
    // *
    // * @param fileName The name of the output file to write.
    // */
    // private void writeOutputFile(String fileName) {
    //     Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    //     try (FileWriter writer = new FileWriter(fileName)) {

    //             // Add statistics
    //         JsonObject statistics = new JsonObject();
    //         statistics.addProperty("systemRuntime", statisticalFolder.getSystemRuntime());
    //         statistics.addProperty("numDetectedObjects", statisticalFolder.getNumDetectedObjects());
    //         statistics.addProperty("numTrackedObjects", statisticalFolder.getNumTrackedObjects());
    //         statistics.addProperty("numLandmarks", statisticalFolder.getNumLandmarks());

    //         // Add LandMarks to statistics
    //         JsonObject landMarks = new JsonObject();
    //         statisticalFolder.getLandMarks().forEach((id, landMark) -> {
    //             landMarks.add(id, gson.toJsonTree(landMark));
    //         });
    //         statistics.add("landMarks", landMarks);

    //         writer.write("   \"statistics\": " + gson.toJson(statistics) );

    //         writer.write("}\n");
    //         System.out.println("Formatted error report written to " + fileName);

    //     } catch (IOException e) {
    //         System.err.println("Error writing formatted error report: " + e.getMessage());
    //     }
        // // Write statistics in the first line
        // writer.write("{\"systemRuntime\": " + statisticalFolder.getSystemRuntime() + ", ");
        // writer.write("\"numDetectedObjects\": " + statisticalFolder.getNumDetectedObjects() + ", ");
        // writer.write("\"numTrackedObjects\": " + statisticalFolder.getNumTrackedObjects() + ", ");
        // writer.write("\"numLandmarks\": " + statisticalFolder.getNumLandmarks() + "},\n");

        // // Write the landmarks
        // writer.write("\"landMarks\": {\n");

        // List<LandMark> landmarks = fusionSlam.getLandMarkList();
        // for (int i = 0; i < landmarks.size(); i++) {
        //     LandMark landmark = landmarks.get(i);

        //     // Create a single-line JSON string for the landmark
        //     StringBuilder landmarkLine = new StringBuilder();
        //     landmarkLine.append("  \"").append(landmark.getId()).append("\": {");
        //     landmarkLine.append("\"id\": \"").append(landmark.getId()).append("\", ");
        //     landmarkLine.append("\"description\": \"").append(landmark.getDescription()).append("\", ");
        //     landmarkLine.append("\"coordinates\": [");

    //         List<CloudPoint> coordinates = landmark.getCloudPoints();
    //         for (int j = 0; j < coordinates.size(); j++) {
    //             CloudPoint point = coordinates.get(j);
    //             landmarkLine.append("{\"x\": ").append(point.getX()).append(", \"y\": ").append(point.getY()).append("}");
    //             if (j < coordinates.size() - 1) {
    //                 landmarkLine.append(", ");
    //             }
    //         }
    //         landmarkLine.append("]}");

    //         // Write the landmark to the file
    //         writer.write(landmarkLine.toString());

    //         // Add a comma for all but the last landmark
    //         if (i < landmarks.size() - 1) {
    //             writer.write(",\n");
    //         } else {
    //             writer.write("\n");
    //         }
    //     }

    //     // Close the landmarks object
    //     writer.write("}\n");
    //     System.out.println("Output written to " + fileName);
    // } catch (IOException e) {
    //     System.err.println("Error writing output file: " + e.getMessage());
    // }
// }

    /**
     * Creates and writes the simulation's output to a JSON file.
     *
     * @param fileName The name of the output file to write.
     */
    private void writeOutputFile(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("{\"systemRuntime\":" + statisticalFolder.getSystemRuntime() + ", ");
            writer.write("\"numDetectedObjects\":" + statisticalFolder.getNumDetectedObjects() + ", ");
            writer.write("\"numTrackedObjects\":" + statisticalFolder.getNumTrackedObjects() + ", ");
            writer.write("\"numLandmarks\":" + statisticalFolder.getNumLandmarks() + ",\n");
            writer.write("\"landMarks\":{\n");

            List<LandMark> landmarks = fusionSlam.getLandMarkList();
            for (int i = 0; i < landmarks.size(); i++) {
                String landmarkLine = "    " + landmarks.get(i).toString();
                writer.write(landmarkLine);
                if (i < landmarks.size() - 1) {
                    writer.write(",\n"); 
                }
            }
            writer.write("\n    }\n}");
            System.out.println("Output written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }
}


