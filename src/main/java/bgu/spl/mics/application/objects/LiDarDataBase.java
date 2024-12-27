package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
// --------------------- fields -------------------------
    private List<StampedCloudPoints> cloudPoints;


    // Inner class that holds the single instance
    private static class LiDarDataBaseHolder {
        private static LiDarDataBase instance = new LiDarDataBase();
    }
// --------------------- constructor --------------------

    // Private constructor to prevent creation of additional instances
    private LiDarDataBase(){
        this.cloudPoints = new ArrayList<StampedCloudPoints>();
    }
// --------------------- methods ------------------------

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public static LiDarDataBase getInstance(String filePath) { // TO COMPLETE
        // TODO: Implement this

        return LiDarDataBaseHolder.instance;
    }

// --------------------- fields -------------------------

}
