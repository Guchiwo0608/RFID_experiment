package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class WaitForQuery {

    public static void main(String[] args) {

        try {
            // Pass in a reader hostname or IP address as a command line argument when running the example.
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }
            ImpinjReader reader = new ImpinjReader();

            // Connect to the reader.
            System.out.println("Connecting");
            reader.connect(hostname);

            // Get the default settings.
            // We'll use these as a starting point and then modify the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Tell the reader to include the antenna number in all tag reports. Other fields can be added
            // to the reports in the same way by setting the appropriate Report.IncludeXXXXXXX property.
            ReportConfig report = settings.getReport();
            report.setIncludeAntennaPortNumber(true);

            // Tell the reader not to send tag reports.
            // We will ask for them.
            report.setMode(ReportMode.WaitForQuery);

            // Apply the newly modified settings.
            System.out.println("Applying Settings");
            reader.applySettings(settings);

            // Assign the TagsReported event listener.
            // This specifies which object to inform when tags reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Assign a listener that will be informed when the tag report buffer is almost full.
            reader.setBufferWarningListener(new BufferWarningListenerImplementation());

            // Assign a listener that will be informed when the tag report buffer has overflowed.
            reader.setBufferOverflowListener(new BufferOverflowListenerImplementation());

            // Start reading.
            System.out.println("Starting");
            reader.start();

            System.out.println("Waiting while the reader reads tags.");
            TimeUnit.MILLISECONDS.sleep(5000);

            // Ask for the tag reports.
            reader.queryTags();

            // Wait for the user to press enter.
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            reader.stop();

            // Disconnect from the reader.
            reader.disconnect();

        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
