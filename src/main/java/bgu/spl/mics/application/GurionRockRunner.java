package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;


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

            MessageBusImpl messageBus = MessageBusImpl.getInstance();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        // TODO: Initialize system components and services.

        // TODO: Start the simulation.
    }

    // Initializing Camera Objects
    public void initCameras(Configuration config) {

    }

    // Initializing LiDar Objects
    public void initLiDar(Configuration config) {

    }

    private static <T> T loadJsonData(String filePath, Type type) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type); // Parse JSON content into the specified type
        } catch (IOException e) {
            throw new RuntimeException("Failed to load file: " + filePath, e); // Handle file reading errors
        }
    }




}
