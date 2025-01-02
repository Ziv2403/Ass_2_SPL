package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;
import java.util.Map;


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
        String configFilePath = args[0];
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(configFilePath)) {
            Configuration config = gson.fromJson(reader, Configuration.class);

            MessageBusImpl messageBus = MessageBusImpl.getInstance();

            String poseFilePath = resolveRelativePath(configFilePath, config.getPoseJsonFile());
            String cameraFilePath = resolveRelativePath(configFilePath, config.getCameras().getCameraDatasPath());
            String lidarFilePath = resolveRelativePath(configFilePath, config.getLidarWorkers().getLidarsDataPath());

            List<Pose> poses = loadJsonData(poseFilePath, new TypeToken<List<Pose>>() {}.getType());
            Map<String, List<StampedDetectedObjects>> cameraData = loadJsonData(cameraFilePath, new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType());
            List<StampedCloudPoints> lidarData = loadJsonData(lidarFilePath, new TypeToken<List<StampedCloudPoints>>() {}.getType());

            GPSIMU gpsimu = new GPSIMU(0, STATUS.UP, poses);
            LiDarDataBase liDarDataBase = new LiDarDataBase(lidarData);
            StatisticalFolder statisticalFolder = new StatisticalFolder();

            // ------------ Create, register and start services ------------
            List<MicroService> microServices = new ArrayList<>();

            // Time Service
            TimeService timeService = new TimeService(config.getTickTime(), config.getDuration(), statisticalFolder);
            messageBus.register(timeService);
            microServices.add(timeService);

            // Camera Services
            for (Camera camera : config.getCameras().getCamerasConfigurations()) {
                CameraService cameraService = new CameraService(camera, cameraData.get(camera.getCameraKey()),statisticalFolder);
                messageBus.register(cameraService);
                microServices.add(cameraService);
            }

            // LiDar Services
            for (LiDarWorkerTracker liDar : config.getLidarWorkers().getLidarConfigurations()) {
                LiDarService liDarService = new LiDarService(liDar, liDarDataBase, statisticalFolder);
                messageBus.register(liDarService);
                microServices.add(liDarService);
            }


            // Pose Services
            PoseService poseService = new PoseService(gpsimu, statisticalFolder);
            microServices.add(poseService);

            // FusionSlam Service

            // Start services
            for (MicroService m : microServices) {
                Thread thread = new Thread(m);
                thread.start();
            }
            // -----------------------------------------------------------

            System.out.println("test");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        // TODO: Initialize system components and services.

        // TODO: Start the simulation.
    }

    // Initializing Camera Objects
    public void initCameraData(String filepath) {
        
    }

    // Initializing LiDar Objects
    public void initLiDarData(String filepath) {

    }

    private static <T> T loadJsonData(String filePath, Type type) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type); // Parse JSON content into the specified type
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file: " + filePath, e); // Handle file reading errors
        }
    }

    private static String resolveRelativePath(String configFilePath, String relativePath) {
        return new java.io.File(new java.io.File(configFilePath).getParent(), relativePath).getAbsolutePath();
    }



}
