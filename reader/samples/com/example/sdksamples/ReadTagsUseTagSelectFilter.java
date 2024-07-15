package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;

/**
 * This example shows how to use the tag select filter interface, which allows
 * for more control of the select commands performed by the reader to filter
 * tags.
 *
 * Using this interface allows for up to 5 select commands to be sent with
 * latest versions of octane firmware.
 */
public class ReadTagsUseTagSelectFilter {

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

            // Get the default settings
            // We'll use these as a starting point and then modify the settings we're
            // interested in.
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

            // Set the filter mode.
            // Apply the filters that are specified in the tag select filter list.
            settings.getFilters().setMode(TagFilterMode.UseTagSelectFilters);

            // Setup filters

            // This filter will select the tag on match and deselect if the tag doesn't match.
            // It filters on the first hex character in the EPC memory.
            // The filter is looking to match '3'.
            TagSelectFilter tagSelectFilter1 = new TagSelectFilter();
            tagSelectFilter1.setMatchingAction(TagFilterStateUnawareAction.Select);
            tagSelectFilter1.setNonMatchingAction(TagFilterStateUnawareAction.Unselect);
            tagSelectFilter1.setTagMask("3");
            tagSelectFilter1.setBitPointer(BitPointers.Epc + 0);
            tagSelectFilter1.setMemoryBank(MemoryBank.Epc);

            // This filter will select the tag on match and deselect if the tag doesn't match.
            // It filters on the second hex character in the EPC memory.
            // The filter is looking to match 0.
            TagSelectFilter tagSelectFilter2 = new TagSelectFilter();
            tagSelectFilter2.setMatchingAction(TagFilterStateUnawareAction.Select);
            tagSelectFilter2.setNonMatchingAction(TagFilterStateUnawareAction.Unselect);
            tagSelectFilter2.setTagMask("0");
            tagSelectFilter2.setBitPointer(BitPointers.Epc + 4);
            tagSelectFilter2.setMemoryBank(MemoryBank.Epc);

            // This filter will select the tag on match and deselect if the tag doesn't match.
            // It filters on the third hex character in the EPC memory.
            // The filter is looking to match 0.
            TagSelectFilter tagSelectFilter3 = new TagSelectFilter();
            tagSelectFilter3.setMatchingAction(TagFilterStateUnawareAction.Select);
            tagSelectFilter3.setNonMatchingAction(TagFilterStateUnawareAction.Unselect);
            tagSelectFilter3.setTagMask("0");
            tagSelectFilter3.setBitPointer(BitPointers.Epc + 8);
            tagSelectFilter3.setMemoryBank(MemoryBank.Epc);

            // This filter will select the tag on match and deselect if the tag doesn't match.
            // It filters on the fourth hex character in the EPC memory.
            // The filter is looking to match 8.
            TagSelectFilter tagSelectFilter4 = new TagSelectFilter();
            tagSelectFilter4.setMatchingAction(TagFilterStateUnawareAction.Select);
            tagSelectFilter4.setNonMatchingAction(TagFilterStateUnawareAction.Unselect);
            tagSelectFilter4.setTagMask("8");
            tagSelectFilter4.setBitPointer(BitPointers.Epc + 12);
            tagSelectFilter4.setMemoryBank(MemoryBank.Epc);


            // Add newly created filters to extended tag filter list.
            // Altogether, it will only select tags that pass every filter.
            settings.getFilters().getTagSelectFilterList().add(tagSelectFilter1);
            settings.getFilters().getTagSelectFilterList().add(tagSelectFilter2);
            settings.getFilters().getTagSelectFilterList().add(tagSelectFilter3);
            settings.getFilters().getTagSelectFilterList().add(tagSelectFilter4);

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Assign the TagsReported event listener.
            // This specifies which object to notify when tags reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Start reading.
            System.out.println("Starting  " + hostname);
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press enter to exit.");
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
