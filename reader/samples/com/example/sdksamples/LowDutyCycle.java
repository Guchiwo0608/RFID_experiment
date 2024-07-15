package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;


public class LowDutyCycle {

    public static void main(String[] args) {
        try {
            // Pass in a reader hostname or IP address as a
            // command line argument when running the example
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

            // Tell the reader to include the antenna number in all tag reports. Other fields can be added
            // to the reports in the same way by calling the appropriate ReportConfig.setIncludeXXXXXXX() method.
            settings.getReport().setIncludeAntennaPortNumber(true);

            // Send a tag report for every tag read.
            settings.getReport().setMode(ReportMode.Individual);

            // Configure the reader for low-duty cycle mode.
            LowDutyCycleSettings ldc = settings.getLowDutyCycle();
            ldc.setEmptyFieldTimeoutInMs(500);
            ldc.setFieldPingIntervalInMs(200);
            ldc.setIsEnabled(true);

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Connect a tag report listener. This listener will detect when tag reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Start reading.
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
            // Handle Octane SDK errors.
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            // Handle other errors.
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
