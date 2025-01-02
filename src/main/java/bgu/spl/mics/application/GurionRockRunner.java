package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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

//        String configFilePath = args[0] + " " + args[1];
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

            GPSIMU gpsimu = new GPSIMU(1, STATUS.UP, poses);
            LiDarDataBase liDarDataBase = new LiDarDataBase(lidarData);
            StatisticalFolder statisticalFolder = new StatisticalFolder();
            FusionSlam fusionSlam = FusionSlam.getInstance();

            // ------------ Create, register and start services ------------
            List<MicroService> microServices = new ArrayList<>();
            List<Thread> threads = new ArrayList<>();

            // Time Service
            TimeService timeService = new TimeService(config.getTickTime(), config.getDuration(), statisticalFolder);
            messageBus.register(timeService);

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
            messageBus.register(poseService);
            microServices.add(poseService);

            // FusionSlam Service
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam, statisticalFolder);
            messageBus.register(fusionSlamService);
            microServices.add(fusionSlamService);

            // Start services except timeService
            for (MicroService m : microServices) {
                Thread thread = new Thread(m, m.getName());
                threads.add(thread);
                thread.start();
                System.out.println("Starting service: " + thread.getName());
            }

            // Start timeService (Clock starts ticking)
            microServices.add(timeService);
            Thread timeServiceThread = new Thread(timeService, timeService.getName());
            threads.add(timeServiceThread);
            System.out.println("Starting service: " + timeServiceThread.getName());
            timeServiceThread.start();

//            messageBus.printSubscribers();
//            Thread.sleep(4000);
//            messageBus.printSubscribers();

            // -----------------------------------------------------------

            // Wait for all threads to finish
            for (Thread thread : threads) {
                try {
                    thread.join(); // Wait for the thread to terminate
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + thread.getName());
                }
            }

            writeStatsToFile(statisticalFolder, "outputTEST.json");

        } catch (IOException | IllegalArgumentException e ) {
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

    private static void writeStatsToFile(StatisticalFolder stats, String fileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(stats, writer);
            System.out.println("Stats have been written to " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to write stats: " + e.getMessage());
        }
    }




}
