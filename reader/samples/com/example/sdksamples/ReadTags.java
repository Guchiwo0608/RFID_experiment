package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;

public class ReadTags {

    public static void main(String[] args) {

        try {
            // Pass in a reader hostname or IP address as a command line argument when
            // running the example.
            String hostname = SampleProperties.hostname;
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "'property");
            }
            ImpinjReader reader = new ImpinjReader();

            // Connect to the reader.
            System.out.println("Connecting");
            reader.connect(hostname);

            // Get the default settings.
            // We'll use these as a starting point and then modify the settings we're
            // interested in.
            Settings settings = reader.queryDefaultSettings();

            ReportConfig report = settings.getReport();

            // Tell the reader to include the antenna number in all tag reports. Other
            // fields can be added
            // to the reports in the same way by setting the appropriate
            // Report.IncludeXXXXXXX property.
            report.setIncludeAntennaPortNumber(true);
            report.setMode(ReportMode.Individual);

            // The reader can be set into various modes in which reader dynamics are
            // optimized for
            // specific regions and environments.
            // The following mode, AutoSetDenseReaderDeepScan, monitors RF noise and
            // interference
            // and then automatically and continuously optimizes the reader's configuration.
            settings.setRfMode(1002);
            settings.setSearchMode(SearchMode.DualTarget);
            settings.setSession(2);

            // Enable antenna #1. Disable all others.
            AntennaConfigGroup antennas = settings.getAntennas();
            antennas.disableAll();
            antennas.enableById(new short[] { 2 });

            // Set the Transmit Power and
            // Receive Sensitivity to the maximum.

            // You can also set them to specific values like this...
            // antennas.getAntenna((short) 1).setTxPowerinDbm(20.0);
            // antennas.getAntenna((short) 1).setRxSensitivityinDbm(-70);

            // Apply the newly modified settings.
            System.out.println("Applying Settings");
            reader.applySettings(settings);

            // Assign the TagsReported event listener.
            // This specifies which object to inform when tags reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Start reading.
            System.out.println("Starting");
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            s.close();

            // Stop reading.
            System.out.println("Stopping");
            reader.stop();

            // Disconnect from the reader.
            System.out.println("Disconnecting");
            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
