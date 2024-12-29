package bgu.spl.mics.application;
import bgu.spl.mics.application.services.*;
import bgu.spl.mics.application.objects.*;


import java.util.ArrayList;
import java.util.List;


public class Initializer {
    public static List<CameraService> initializeCameras(Configuration config) {
        List<CameraService> cameraServices = new ArrayList<>();
        for (Camera cameraConfig : config.getCameras()) {
            cameraServices.add(new CameraService(cameraConfig));
        }
        return cameraServices;
    }

    public static List<LiDarWorkerService> initializeLidars(Configuration config) {
        List<LiDarWorkerService> lidarServices = new ArrayList<>();
        for (LidarConfiguration lidarConfig : config.getLidars().getLidarConfigurations()) {
            lidarServices.add(new LiDarWorkerService(lidarConfig));
        }
        return lidarServices;
    }

    public static PoseService initializePoseService(Configuration config) {
        return new PoseService(config.getPoseJsonFile());
    }

    public static TimeService initializeTimeService(Configuration config) {
        return new TimeService(config.getTickTime(), config.getDuration());
    }
}
