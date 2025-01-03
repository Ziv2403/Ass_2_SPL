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

    /**
     * Constructs a GPSIMU instance with a specified current tick and status.
     *
     * @param currentTick The current time tick.
     * @param status The operational status of the GPSIMU.
     * @post {@code this.currentTick == currentTick}
     * @post {@code this.status == status}
     * @post {@code poseList.isEmpty() == true}
     */
    public GPSIMU(int currentTick, STATUS status){
        this.currentTick = currentTick;
        this.status = status;
        this.poseList = new ArrayList<>();
    }


    /**
     * Constructs a GPSIMU instance with a specified current tick, status, and pose list.
     *
     * @param currentTick The current time tick.
     * @param status The operational status of the GPSIMU.
     * @param poseList The list of poses associated with the GPSIMU.
     * @post {@code this.currentTick == currentTick}
     * @post {@code this.status == status}
     * @post {@code this.poseList.equals(poseList)}
     */
    public GPSIMU(int currentTick, STATUS status, List<Pose> poseList){
        this.currentTick = currentTick;
        this.status = status;
        this.poseList = poseList;
    }

// --------------------- methods --------------------

    /**
     * @return The current time tick.
     */
    public int getCurrentTick() {return currentTick;}

    /**
     * Sets the current time tick.
     *
     * @param tick The new current tick value.
     * @post {@code this.currentTick == tick}
     */
    public void setCurrentTick(int tick) {this.currentTick = tick;}

    /**
     * @return The current operational status of the GPSIMU.
     */
    public STATUS getStatus() {return status;}
    
    /**
     * Sets the operational status of the GPSIMU.
     *
     * @param status The new status to set.
     * @pre {@code status != null}
     * @post {@code this.status == status}
     */
    public void setStatus(STATUS status) {this.status = status;}

    /**
     * @return The list of poses associated with the GPSIMU.
     */
    public List<Pose> getPoseList() {return poseList;}

    /**
     * Retrieves the pose at the current tick.
     *
     * @return The pose at the current tick, or null if not found.
     */
    public Pose getPoseAtTick() {
        for (Pose p : poseList) {
            if (p.getTime() == currentTick) {
                return p;
            }
        }
        return null;
    }


    /**
     * @return A string representation of the GPSIMU.
     */
    @Override
    public String toString() {
        return "GPSIMU{" +
                "currentTick=" + currentTick +
                ", status=" + status +
                ", poseList=" + poseList +
                '}';
    }
}
