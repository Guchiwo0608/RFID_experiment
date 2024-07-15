package com.example.sdksamples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;

import java.util.Scanner;

public class RfDoppler {

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

            // Get the default settings.
            // We'll use these as a starting point and then modify the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Tell the reader to include the
            // RF doppler frequency in all tag reports.
            settings.getReport().setIncludeDopplerFrequency(true);

            // RfMode must be set to DenseReaderM8 (3).
            settings.setRfMode(3);

            // Use antenna #1
            settings.getAntennas().disableAll();
            settings.getAntennas().enableById(new short[]{1});

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Assign the TagsReported event listener.
            // This specifies which object to notify when tags reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Start reading.
            System.out.println("Starting  " + hostname);
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            System.out.println("Stopping  " + hostname);
            reader.stop();

            // Disconnect from the reader.
            System.out.println("Disconnecting from " + hostname);
            reader.disconnect();

            System.out.println("Done");
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
