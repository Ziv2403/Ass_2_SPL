package bgu.spl.mics.application;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;



/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // TODO: Parse configuration file.
        String configFilePath = args[0] + " " + args[1];
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(configFilePath)) {
            Configuration config = gson.fromJson(reader, Configuration.class);

            // Use the configuration
            System.out.println("Pose JSON File: " + config.getPoseJsonFile());
            System.out.println("Camera JSON File: " + config.getCameras().getCameraDatasPath());
            System.out.println("Lidar JSON File: " + config.getLidarWorkers().getLidarsDataPath());
            System.out.println("Tick Time: " + config.getTickTime());
            System.out.println("Duration: " + config.getDuration());

            // Access cameras
            config.getCameras().getCamerasConfigurations().forEach(camera -> {
                System.out.println("Camera ID: " + camera.getId());
                System.out.println("Camera Frequency: " + camera.getFrequency());
                System.out.println("Camera Key: " + camera.getCameraKey());
            });

            // Access LiDAR workers
            config.getLidarWorkers().getLidarConfigurations().forEach(lidar -> {
                System.out.println("LiDAR ID: " + lidar.getId());
                System.out.println("LiDAR Frequency: " + lidar.getFrequency());
            });
        } catch (IOException e) {
            System.err.println("Error reading configuration file: " + e.getMessage());
        }
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }


}
