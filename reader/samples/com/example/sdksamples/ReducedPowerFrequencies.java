package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.ArrayList;
import java.util.Scanner;

public class ReducedPowerFrequencies {

    public static void main(String[] args) {
        try {
            // Pass in a reader hostname or IP address as a command line argument when running the example
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }
            ImpinjReader reader = new ImpinjReader();

            // Connect to the reader.
            System.out.println("Connecting...");
            reader.connect(hostname);

            // Get the reader features to determine if the reader supports a fixed-frequency table.
            FeatureSet features = reader.queryFeatureSet();

            if (features.isHoppingRegion()
                    && (features.getReaderModel() != ReaderModel.SpeedwayR120)
                    && (features.getReaderModel() != ReaderModel.SpeedwayR220)) {

                // Get the default settings.
                // We'll use these as a starting point and then modify the settings we're interested in.
                Settings settings = reader.queryDefaultSettings();

                // Tell the reader to include the antenna number and channel in all tag reports.
                // Other fields can be added to the reports in the same way by setting the
                // appropriate Report.IncludeXXXXXXX property.
                settings.getReport().setIncludeAntennaPortNumber(true);
                settings.getReport().setIncludeChannel(true);

                // Send a tag report for every tag read.
                settings.getReport().setMode(ReportMode.Individual);

                // Specify the transmit frequencies to use.
                // Make sure your reader supports this and that the frequencies are valid for your region.
                // The following example is for FCC readers.
                ArrayList<Double> freqList = new ArrayList<>();
                freqList.add(902.75);
                freqList.add(903.25);
                freqList.add(903.75);
                freqList.add(904.25);
                freqList.add(904.75);
                freqList.add(905.25);
                freqList.add(905.75);
                freqList.add(906.25);
                freqList.add(906.75);
                freqList.add(907.25);
                freqList.add(907.75);
                freqList.add(908.25);
                freqList.add(908.75);
                freqList.add(909.25);
                freqList.add(909.75);
                settings.setReducedPowerFrequenciesInMhz(freqList);

                settings.getAntennas().disableAll();
                settings.getAntennas().getAntenna(1).setEnabled(true);

                // Apply the newly modified settings.
                System.out.println("Applying Settings...");
                reader.applySettings(settings);

                // Fetch the settings from the reader, to confirm reduced power frequencies were applied.
                System.out.println("Fetching Settings...");
                Settings settings2 = reader.querySettings();
                System.out.println("----------------------0000000000000000000000000-------------------------");
                for (Double tx : settings2.getReducedPowerFrequenciesInMhz()) {
                    System.out.println("Found Tx Frequency " + tx);
                }
                System.out.println("----------------------0000000000000000000000000-------------------------");

                // Assign the TagsReported event listener.
                // This specifies which object to notify when tags reports are available.
                reader.setTagReportListener(new TagReportListenerImplementation());

                // Start reading.
                System.out.println("Starting...");
                reader.start();

            } else {
                System.out.println("This reader does not allow reduced power frequencies to be specified.");
            }
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            System.out.println("Stopping...");
            reader.stop();

            // Disconnect from the reader.
            System.out.println("Disconnecting...");
            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
