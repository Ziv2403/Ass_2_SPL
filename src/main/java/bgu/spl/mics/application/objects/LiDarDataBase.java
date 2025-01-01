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
// --------------------- constructors --------------------
    public LiDarDataBase(List<StampedCloudPoints> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }

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
    public static LiDarDataBase getInstance() {
        return LiDarDataBaseHolder.instance;
    }

    public void loadData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
            Gson gson = new Gson();
            this.cloudPoints = gson.fromJson(reader, listType);
        } catch (IOException e) {
            System.err.println("Failed to load data from file: " + filePath);//cause to error?
            e.printStackTrace();
        }
    }
    // public static LiDarDataBase getInstance(String filePath) {
    //     LiDarDataBase instance = LiDarDataBaseHolder.instance;  // Access singleton instance
    
    //     try (FileReader reader = new FileReader(filePath)) {
    //         // Parse JSON to List<StampedCloudPoints>
    //         Type listType = new TypeToken<List<StampedCloudPoints>>() {}.getType();
    //         Gson gson = new Gson();
    //         List<StampedCloudPoints> stampedCloudPointList = gson.fromJson(reader, listType);
    
    //         // Add the parsed data to the cloudPoints field
    //         instance.cloudPoints.addAll(stampedCloudPointList);
    //     } catch (IOException e) {
    //         // Handle file access or parsing issues
    //         System.err.println("Failed to load data from file: " + filePath);
    //         e.printStackTrace();
    //     }
    
    //     return instance;
    // }
    
    public List<StampedCloudPoints> getCloudPoints() {return cloudPoints;}

    public List<CloudPoint> getCloudPoints(String objectId) {
        List<CloudPoint> result = new ArrayList<>();
        for (StampedCloudPoints stamped : cloudPoints) {
            if (stamped.getId().equals(objectId)) {
                result.addAll(stamped.getCloudPoints());
            }
        }
        return result;
    }
    

}
