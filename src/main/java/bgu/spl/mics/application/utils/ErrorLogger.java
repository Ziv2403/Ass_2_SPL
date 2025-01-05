package bgu.spl.mics.application.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ErrorLogger {
    
        private ErrorLogger() {} 

        public static void writeFormattedErrorReport(String fileName, String errorDescription, String faultySensor, StatisticalFolder stats) {
    Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    try (FileWriter writer = new FileWriter(fileName)) {
        // Start the JSON object
        writer.write("{\"error\":\"" + errorDescription + "\",\n");
        writer.write("  \"faultySensor\":\"" + faultySensor + "\",\n");

        // Add camera frames
        writer.write("  \"lastCamerasFrame\":{\n");
        Map<String, StampedDetectedObjects> lastCamerasFrame = stats.getLastCameraFrames();
        int cameraCount = lastCamerasFrame.size();
        for (Map.Entry<String, StampedDetectedObjects> entry : lastCamerasFrame.entrySet()) {
            writer.write("    \"" + entry.getKey() + "\":" + gson.toJson(entry.getValue()));
            if (--cameraCount > 0) writer.write(",\n"); // Add comma if not the last element
        }
        writer.write("\n  },\n");

        // Add LiDAR frames
        writer.write("  \"lastLiDarWorkerTrackersFrame\":{\n");
        Map<String, TrackedObject> lastLiDarFrame = stats.getLastLiDarFrames();
        int liDarCount = lastLiDarFrame.size();
        for (Map.Entry<String, TrackedObject> entry : lastLiDarFrame.entrySet()) {
            writer.write("    \"" + entry.getKey() + "\": " + gson.toJson(entry.getValue()));
            if (--liDarCount > 0) writer.write(",\n"); // Add comma if not the last element
        }
        writer.write("\n  },\n");

        // Add poses
        writer.write("   \"poses\": " + gson.toJson(stats.getPoseOutput()) + ",\n");

        // Add statistics
        JsonObject statistics = new JsonObject();
        statistics.addProperty("systemRuntime", stats.getSystemRuntime());
        statistics.addProperty("numDetectedObjects", stats.getNumDetectedObjects());
        statistics.addProperty("numTrackedObjects", stats.getNumTrackedObjects());
        statistics.addProperty("numLandmarks", stats.getNumLandmarks());
        writer.write("   \"statistics\": " + gson.toJson(statistics) + "\n");

        // Close the JSON object
        writer.write("}\n");

        System.out.println("Formatted error report written to " + fileName);
    } catch (IOException e) {
        System.err.println("Error writing formatted error report: " + e.getMessage());
    }
    }

}
