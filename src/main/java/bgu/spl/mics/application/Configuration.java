package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.util.List;

public class Configuration {
    private CamerasConfiguration Cameras;
    private LidarWorkersConfiguration LidarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    // Getters and setters
    public CamerasConfiguration getCameras() { return Cameras; }
    public void setCameras(CamerasConfiguration cameras) { Cameras = cameras; }

    public LidarWorkersConfiguration getLidarWorkers() { return LidarWorkers; }
    public void setLidarWorkers(LidarWorkersConfiguration lidarWorkers) { LidarWorkers = lidarWorkers; }

    public String getPoseJsonFile() { return poseJsonFile; }
    public void setPoseJsonFile(String poseJsonFile) { this.poseJsonFile = poseJsonFile; }

    public int getTickTime() { return TickTime; }
    public void setTickTime(int tickTime) { TickTime = tickTime; }

    public int getDuration() { return Duration; }
    public void setDuration(int duration) { Duration = duration; }



    public static class CamerasConfiguration {
        private List<Camera> CamerasConfigurations;
        private String cameras_datas_path;

        public List<Camera> getCamerasConfigurations() { return CamerasConfigurations; }
        public void setCamerasConfigurations(List<Camera> camerasConfigurations) { CamerasConfigurations = camerasConfigurations; }

        public String getCameraDatasPath() { return cameras_datas_path; }
        public void setCameraDatasPath(String cameraDatasPath) { this.cameras_datas_path = cameraDatasPath; }
    }



    public static class LidarWorkersConfiguration {
        private List<LiDarWorkerTracker> LidarConfigurations;
        private String Lidars_data_path;

        public List<LiDarWorkerTracker> getLidarConfigurations() { return LidarConfigurations; }
        public void setLidarConfigurations(List<LiDarWorkerTracker> lidarConfigurations) { LidarConfigurations = lidarConfigurations; }

        public String getLidarsDataPath() { return Lidars_data_path; }
        public void setLidarsDataPath(String lidarsDataPath) { this.Lidars_data_path = lidarsDataPath; }
    }
}
