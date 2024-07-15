package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;


public class ReadTagsFiltered {

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

            // Get the default settings
            // We'll use these as a starting point and then modify the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Tell the reader to include the antenna number in all tag reports. Other fields can be added
            // to the reports in the same way by setting the appropriate Report.IncludeXXXXXXX property.
            settings.getReport().setIncludeAntennaPortNumber(true);

            // Send a tag report for every tag read.
            settings.getReport().setMode(ReportMode.Individual);

            // The reader can be set into various modes in which reader
            // dynamics are optimized for specific regions and environments.
            // The following mode, AutoSetDenseReaderDeepScan, monitors RF noise and interference and then automatically
            // and continuously optimizes the reader's configuration
            settings.setRfMode(1002);

            // Tag filter verification mode requires firmware 7.6 or above.
            settings.getFilters().setTagFilterVerificationMode(true);

            // Setup a tag filter.
            // Only the tags that match this filter will respond.

            // First, setup tag filter #1.
            TagFilter t1 = settings.getFilters().getTagFilter1();

            // We want to apply the filter to the EPC memory bank.
            t1.setMemoryBank(MemoryBank.Epc);

            // Start matching at the third word (bit 32), since the
            // first two words of the EPC memory bank are the
            // CRC and control bits. BitPointers.Epc is a helper
            // enumeration you can use, so you don't have to remember this.
            t1.setBitPointer(BitPointers.Epc);

            // Only match tags with EPCs that start with "3008"
            t1.setTagMask("3008");
            t1.setFilterOp(TagFilterOp.Match);

            // This filter is 16 bits long (one word).
            t1.setBitCount(16);

            // Next, setup tag filter #2
            TagFilter t2 = settings.getFilters().getTagFilter2();

            // This filter will apply to the User memory bank.
            t2.setMemoryBank(MemoryBank.User);

            // Start matching on the third bit (bit pointer is zero-based)
            t2.setBitPointer(2);

            // Only match tags that have the third bit of User memory set.
            // Mask = 0x80 (hex) = 10000000 (bin)
            t2.setTagMask("80");
            t2.setFilterOp(TagFilterOp.Match);

            // Filter is 1 bit long
            t2.setBitCount(1);

            // To test this filter, write 0x2000 to
            // the first word of User memory.
            // 0x2000 (hex) = 0010000000000000 (bin)

            // Set the filter mode.
            // Both filters must match.
            settings.getFilters().setMode(TagFilterMode.Filter1AndFilter2);

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Assign the TagsReported listener.
            // This specifies which method to call when tags reports are available.
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
            System.out.println("Octane SDK exception : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
