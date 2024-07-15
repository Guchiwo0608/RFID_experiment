package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;

public class SetReaderModes {

    public static void main(String[] args) {

        try {
            // Pass in a reader hostname or IP address as a command line argument when running the example.
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }
            ImpinjReader reader = new ImpinjReader();

            // Connect to the reader.
            reader.connect(hostname);

            // Get the default settings.
            // We'll use these as a starting point and then modify the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Get a reference to the report configuration.
            ReportConfig r = settings.getReport();

            // Tell the reader to include the antenna number in all tag reports. Other fields can be added
            // to the reports in the same way by setting the appropriate Report.IncludeXXXXXXX property.
            r.setIncludeAntennaPortNumber(true);
            r.setIncludeFirstSeenTime(false);

            // Send a tag report for every tag read.
            r.setMode(ReportMode.Individual);

            // Set the RF Mode, Search Mode, Session and Tag Population Estimate.
            settings.setRfMode(1002);
            settings.setSearchMode(SearchMode.DualTarget);
            settings.setSession(2);
            settings.setTagPopulationEstimate(32);

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Assign the TagsReported listener.
            // This specifies which object to notify when tags reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Start reading.
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            reader.stop();

            // Disconnect from the reader.
            reader.disconnect();

        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
