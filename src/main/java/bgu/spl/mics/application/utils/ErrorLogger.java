package bgu.spl.mics.application.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import bgu.spl.mics.application.objects.StatisticalFolder;

import java.io.FileWriter;
import java.io.IOException;

public class ErrorLogger {
    
        private ErrorLogger() {} 


        public static void writeFormattedErrorReport(String fileName, String errorDescription, String faultySensor, StatisticalFolder stats) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
            try (FileWriter writer = new FileWriter(fileName)) {
                // Create the error report JSON object
                JsonObject errorReport = new JsonObject();
                errorReport.addProperty("error", errorDescription);
                errorReport.addProperty("faultySensor", faultySensor);
        
                // Add camera frames
                // writer.write("{\n");
                writer.write("{\"error\": \"" + errorDescription + "\",\n");
                writer.write("  \"faultySensor\": \"" + faultySensor + "\",\n");
                writer.write("  \"lastCamerasFrame\": {\n");
        
                stats.getLastCameraFrames().forEach((cameraKey, frame) -> {
                    try {
                        writer.write("  \"" + cameraKey + "\": " + gson.toJson(frame) + "\n");
                    } catch (IOException e) {
                        System.err.println("Error writing camera frame: " + e.getMessage());
                    }
                });
        
                // Remove the trailing comma from lastCamerasFrame and close it
                writer.write("},\n");
        
                // Add LiDAR frames
                JsonObject lastLiDarFrame = new JsonObject();
                stats.getLastLiDarFrames().forEach((lidarKey, frame) -> {
                    lastLiDarFrame.add(lidarKey, gson.toJsonTree(frame));
                });
                writer.write("\"lastLiDarWorkerTrackersFrame\": " + gson.toJson(lastLiDarFrame) + ",\n");
        
                // Add poses
                writer.write("\"poses\": " + gson.toJson(stats.getPoseOutput()) + ",\n");
        
                // Add statistics
                JsonObject statistics = new JsonObject();
                statistics.addProperty("systemRuntime", stats.getSystemRuntime());
                statistics.addProperty("numDetectedObjects", stats.getNumDetectedObjects());
                statistics.addProperty("numTrackedObjects", stats.getNumTrackedObjects());
                statistics.addProperty("numLandmarks", stats.getNumLandmarks());
                writer.write("\"statistics\": " + gson.toJson(statistics) + "\n");
        
                writer.write("}\n");
        
                System.out.println("Formatted error report written to " + fileName);
            } catch (IOException e) {
                System.err.println("Error writing formatted error report: " + e.getMessage());
            }
        }
        

        // public static void writeFormattedErrorReport(String fileName, String errorDescription, String faultySensor, StatisticalFolder stats) {
        //     Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
        //     try (FileWriter writer = new FileWriter(fileName)) {
        //         // Create the error report JSON object
        //         JsonObject errorReport = new JsonObject();
        //         errorReport.addProperty("error", errorDescription);
        //         errorReport.addProperty("faultySensor", faultySensor);
        
        //         // Add camera frames
        //         JsonObject lastCamerasFrame = new JsonObject();
        //         stats.getLastCameraFrames().forEach((cameraKey, frame) -> {
        //             lastCamerasFrame.add(cameraKey, gson.toJsonTree(frame));
        //         });
        //         errorReport.add("lastCamerasFrame", lastCamerasFrame);
        
        //         // Add LiDAR frames
        //         JsonObject lastLiDarFrame = new JsonObject();
        //         stats.getLastLiDarFrames().forEach((lidarKey, frame) -> {
        //             lastLiDarFrame.add(lidarKey, gson.toJsonTree(frame));
        //         });
        //         errorReport.add("lastLiDarWorkerTrackersFrame", lastLiDarFrame);
        
        //         // Add poses
        //         errorReport.add("poses", gson.toJsonTree(stats.getPoseOutput()));
        
        //         // Add statistics
        //         JsonObject statistics = new JsonObject();
        //         statistics.addProperty("systemRuntime", stats.getSystemRuntime());
        //         statistics.addProperty("numDetectedObjects", stats.getNumDetectedObjects());
        //         statistics.addProperty("numTrackedObjects", stats.getNumTrackedObjects());
        //         statistics.addProperty("numLandmarks", stats.getNumLandmarks());
        //         errorReport.add("statistics", statistics);
        
        //         // Write each JSON object in a separate line for readability
        //         // writer.write("{\n");
        //         writer.write("{\"error\": \"" + errorDescription + "\",\n");
        //         writer.write("\"faultySensor\": \"" + faultySensor + "\",\n");
        //         writer.write("\"lastCamerasFrame\": " + gson.toJson(lastCamerasFrame) + ",\n");
        //         writer.write("\"lastLiDarWorkerTrackersFrame\": " + gson.toJson(lastLiDarFrame) + ",\n");
        //         writer.write("\"poses\": " + gson.toJson(stats.getPoseOutput()) + ",\n");
        //         writer.write("\"statistics\": " + gson.toJson(statistics) + "\n");
        //         writer.write("}\n");
        
        //         System.out.println("Formatted error report written to " + fileName);
        //     } catch (IOException e) {
        //         System.err.println("Error writing formatted error report: " + e.getMessage());
        //     }
        // }
        

        // public static void writeDetailedErrorReport(String fileName, String errorDescription, String faultySensor, StatisticalFolder stats) {
        //     Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        
        //     try (FileWriter writer = new FileWriter(fileName)) {
        //         JsonObject errorReport = new JsonObject();
        
        //         // Add basic error information
        //         errorReport.addProperty("error", errorDescription);
        //         errorReport.addProperty("faultySensor", faultySensor);
        
        //         // Add camera frames
        //         JsonObject lastCamerasFrame = new JsonObject();
        //         stats.getLastCameraFrames().forEach((cameraKey, frame) -> 
        //             lastCamerasFrame.add(cameraKey, gson.toJsonTree(frame.toString()))
        //         );
        //         errorReport.add("lastCamerasFrame", lastCamerasFrame);
        
        //         // Add LiDAR frames
        //         JsonObject lastLiDarFrame = new JsonObject();
        //         stats.getLastLiDarFrames().forEach((lidarKey, frame) -> 
        //             lastLiDarFrame.add(lidarKey, gson.toJsonTree(frame))
        //         );
        //         errorReport.add("lastLiDarWorkerTrackersFrame", lastLiDarFrame);
        
        //         // Add poses
        //         errorReport.add("poses", gson.toJsonTree(stats.getPoseOutput()));
        
        //         // Add statistics
        //         JsonObject statistics = new JsonObject();
        //         statistics.addProperty("systemRuntime", stats.getSystemRuntime());
        //         statistics.addProperty("numDetectedObjects", stats.getNumDetectedObjects());
        //         statistics.addProperty("numTrackedObjects", stats.getNumTrackedObjects());
        //         statistics.addProperty("numLandmarks", stats.getNumLandmarks());
        //         errorReport.add("statistics", statistics);
        
        //         // Write JSON object to file
        //         writer.write(gson.toJson(errorReport));
        //         System.out.println("Detailed error report written to " + fileName);
        //     } catch (IOException e) {
        //         System.err.println("Error writing detailed error report: " + e.getMessage());
        //     }
        // }
        


    /**
     * Writes an error log to a JSON file.
     *
     * @param fileName The name of the file to write.
     * @param errorSource The source of the error (e.g., camera key).
     * @param errorDescription A description of the error.
     * @param crashTime The time the error occurred.
     * @param lastFrame The last frame of data before the crash.
     */
    public static void writeErrorLog(String fileName, String errorSource, String errorDescription, int crashTime, Object lastFrame) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(fileName)) {
            JsonObject errorLog = new JsonObject();
            errorLog.addProperty("errorSource", errorSource);
            errorLog.addProperty("errorDescription", errorDescription);
            errorLog.addProperty("crashTime", crashTime);
            errorLog.add("lastFrame", gson.toJsonTree(lastFrame));
            gson.toJson(errorLog, writer);
            System.out.println("Error log written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing error log: " + e.getMessage());
        }
    }

    /**
     * Writes a detailed error report to a JSON file.
     *
     * @param fileName The name of the file to write.
     * @param errorDescription The description of the error.
     * @param faultySensor The sensor that caused the error.
     * @param lastCamerasFrame The last frame of camera data.
     * @param lastLiDarFrame The last frame of LiDAR data.
     * @param poses The list of robot poses.
     * @param statistics The runtime statistics.
     */
    public static void writeErrorReport(String fileName, String errorDescription, String faultySensor,JsonObject lastCamerasFrame, JsonObject lastLiDarFrame,
                                        JsonObject poses, JsonObject statistics) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Create error report JSON object
        JsonObject errorReport = new JsonObject();
        errorReport.addProperty("error", errorDescription);
        errorReport.addProperty("faultySensor", faultySensor);
        errorReport.add("lastCamerasFrame", lastCamerasFrame);
        errorReport.add("lastLiDarWorkerTrackersFrame", lastLiDarFrame);
        errorReport.add("poses", poses);
        errorReport.add("statistics", statistics);

        // Write to file
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(errorReport, writer);
            System.out.println("Error report written to " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing error report: " + e.getMessage());
        }
    }


}
