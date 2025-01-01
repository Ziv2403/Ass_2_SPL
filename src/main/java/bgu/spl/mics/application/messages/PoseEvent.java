package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

/**
 * PoseEvent
 * •
 * Sent by: PoseService
 * •
 * Handled by: Fusion-SLAM
 * •
 * Details:
 * o
 * Provides the robot's current pose.
 * o
 * Used by Fusion-SLAM for calculations based on received TrackedObjectEvents.
 */
public class PoseEvent implements Event<Pose>{

// --------------------- fields --------------------
    //private int time;
    private Pose pose;

// --------------------- constructor --------------------
    public PoseEvent( Pose pose){
        //this.time = time;
        this.pose = pose;
    }
// --------------------- Methods --------------------
//Getters
   // public int getTime() {return time;}
    public Pose getPose() {return pose;}

}
