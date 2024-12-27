package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

// --------------------- fields -------------------------
    private final List<LandMark> landmarks;
    private final List<Pose> poses;

// --------------------- constructor --------------------
    public FusionSlam(){
        this.landmarks = new ArrayList<>();
        this.poses = new ArrayList<>();
    }

// --------------------- methods --------------------
    public static FusionSlam getInstance(){
        return FusionSlamHolder.INSTANCE;
    }

    public void addLandmark (LandMark landmark){
        this.landmarks.add(landmark);
    }

    public void addPose( Pose pose){
        this.poses.add(pose);
    }

    public List<LandMark> getLandMarkList() { return landmarks;}
    public List<Pose> getPoseList() {return poses;}
}
