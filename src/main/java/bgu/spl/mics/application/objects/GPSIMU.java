package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
// --------------------- fields --------------------
    private int currentTick;
    private STATUS status;
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
    public void setCurrentTick(int tick) {this.currentTick = tick;}

    public STATUS getStatus() {return status;}
    public void setStatus(STATUS status) {this.status = status;}

    public List<Pose> getPoseList() {return poseList;}

    // REVIEW AGAIN
    public Pose getPoseAtTick() {
        for (Pose p : poseList) {
            if (p.getTime() == currentTick) {
                return p;
            }
        }
        return null;
    }

}
