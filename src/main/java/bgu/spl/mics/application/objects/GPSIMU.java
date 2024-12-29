package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
// --------------------- fields --------------------
    private final int currentTick;
    private final STATUS status;
    private final List<Pose> poseList;

// --------------------- constructors --------------------
    public GPSIMU(int currentTick, STATUS status){
        this.currentTick = currentTick;
        this.status = status;
        this.poseList = new ArrayList<>();
    }

    public GPSIMU(int currentTick, STATUS status, List<Pose> poseList){
        this.currentTick = currentTick;
        this.status = status;
        this.poseList = poseList;
    }

// --------------------- methods --------------------
    public int getCurrentTick() {return currentTick;}
    public STATUS getStatus() {return status;}
    public List<Pose> getPoseList() {return poseList;}

}
