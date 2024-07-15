package com.example.sdksamples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;

import java.util.Scanner;


public class ReadFastId {

    public static void main(String[] args) {
        try {
            // Pass in a reader hostname or IP address as a command line argument when running the example.
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }
            ImpinjReader reader = new ImpinjReader();

            // Connect to the reader.
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);

            // Get the default settings. We'll use these as a starting point and then modify
            // the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Tell the reader to include the antenna number and TID (using FastID) in all tag reports.
            // FastID is available on Impinj Monza 4 and later chips.
            settings.getReport().setIncludeAntennaPortNumber(true);
            settings.getReport().setIncludeFastId(true);

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Assign a listener for TagsReported events.
            // This specifies which method to call when tags reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Start the reader.
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            System.out.println("Stopping " + hostname);
            reader.stop();

            // Disconnect from the reader.
            System.out.println("Disconnecting from " + hostname);
            reader.disconnect();

            System.out.println("Done");
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
