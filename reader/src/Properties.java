import java.util.HashMap;
import java.util.Map;

/**
 * This contains the list of possible properties
 */
public class Properties {

    public static String hostname = "169.254.227.23";
    // public static String targetTag = "E200421602D0641002BCE121";
    // public static String targetTag = "E20042160400641002BCE134";
    // public static String targetTag = "E200421604F0641002BCE143";
    public static String targetTag = "E2004215FDF0641002BCE0D3";
    // public static String[] targetTags = {
    // // "E200421605F0641002BCE153",
    // "E20042160170641002BCE10B",
    // "E200421608E0641002BCE182",
    // "E20042160380641002BCE12C",
    // "E20042160370641002BCE12B",
    // "E20042160200641002BCE114",
    // };
    public static short[] antennaPortNumbers = { 1 };
    public static String csvFilePath = "analysis/data/directional/data.csv";
    public static int maxAntennas = 3;
    public static int tagNumber = 4;
    public static Map<String, Double> actualCoordinate = new HashMap<String, Double>() {
        {
            put("x", -1.16);
            put("y", 1.0);
        }
    };
}
