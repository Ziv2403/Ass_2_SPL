package bgu.spl.mics.application.objects;


import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
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

    /**
     * Constructor for creating an instance of LiDarDataBase with specified cloud points.
     *
     * @param cloudPoints A list of StampedCloudPoints to initialize the database.
     * @post {@code this.cloudPoints.equals(cloudPoints)}
     */
    public LiDarDataBase(List<StampedCloudPoints> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }

    /**
     * Private constructor to enforce singleton pattern and initialize with an empty list.
     * @post {@code this.cloudPoints.isEmpty() == true}
     */
    private LiDarDataBase(){
        this.cloudPoints = new ArrayList<StampedCloudPoints>();
    }
// --------------------- methods ------------------------

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     * @pre {@code filePath != null&& !filePath.isEmpty()}
     * @post {@code this.cloudPoints.size() >= 0}
     */
    public static LiDarDataBase getInstance(String filePath) {
        LiDarDataBase instance = LiDarDataBaseHolder.instance;  // Access singleton instance
    
        try (FileReader reader = new FileReader(filePath)) {
            // Parse JSON to List<StampedCloudPoints>
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            Gson gson = new Gson();
            List<StampedCloudPoints> stampedCloudPointList = gson.fromJson(reader, listType);
    
            // Add the parsed data to the cloudPoints field
            instance.cloudPoints.addAll(stampedCloudPointList);
        } catch (IOException e) {
            // Handle file access or parsing issues
            System.err.println("Failed to load data from file: " + filePath);
            e.printStackTrace();
        }
    
        return instance;
    }
    
    
    /**
     * Retrieves all cloud points stored in the database.
     *
     * @return A list of StampedCloudPoints representing all tracked data.
     * @post {@code result.size() == this.cloudPoints.size()}
     */
    public List<StampedCloudPoints> getCloudPoints() {return cloudPoints;}


    /**
     * Retrieves cloud points for a specific object by its ID.
     *
     * @param id The unique identifier of the object.
     * @return An array of CloudPoint objects if the object exists, otherwise {@code null}.
     * @pre {@code id != null && !id.isEmpty()}
     * @post {@code result == null || result.length >= 0}
     */
    public CloudPoint[] getCloudPointsOfObject(String id) {
        for (StampedCloudPoints p : cloudPoints) {
            if (p.getId().equals(id)) {
                return p.toCloudPointObj();
            }
        }
        return null;
    }



    /**
     * Generates a string representation of the LiDarDataBase object.
     *
     * @return A string containing all cloud points in the database.
     */
    @Override
    public String toString() {
        return "LiDarDataBase{" +
                "cloudPoints=" + cloudPoints +
                '}';
    }
}
