package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SpatialReaderDirection {

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
            reader.connect(hostname);

            // Set the DirectionReport listener.
            // This specifies which object to notify when direction reports are available.
            reader.setDirectionReportListener(new DirectionReportListenerImplementation());

            // Get the default settings
            // We'll use these as a starting point and then modify the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Put the spatial reader into direction mode.
            settings.getSpatialConfig().setMode(SpatialMode.Direction);

            // Retrieve the DirectionConfig object stored on the reader so that we can
            // modify the settings we are interested in.
            DirectionConfig directionConfig = settings.getSpatialConfig().getDirection();

            // Tells the spatial reader to perform tag reads with high sensitivity.
            // Use HighPerformance instead, to perform tag reads more quickly at the expense of sensitivity.
            directionConfig.setMode(DirectionMode.HighSensitivity);

            // Enable the sectors you want to track tags in here. Note that you may only enable
            // non-adjacent sectors (e.g. 2 and 4, but not 2 and 3). Further note that sectors 2
            // and 9 are also considered adjacent.
            List<Integer> enabledSectorIds = Arrays.asList(2, 4, 6);

            // xSpans can only enable sectors 2 and 3
            if (reader.isXSpan()) {
                enabledSectorIds = Arrays.asList(2, 3);
            }

            for (Integer sector : enabledSectorIds) {
                directionConfig.enableSector(sector.shortValue());
            }

            // Enable any reports you are interested in here. Entry reports are generated when
            // a tag is first read.  Updates are sent every "update interval" seconds indicating
            // that a tag is still visible to the reader. Exit reports are sent when a tag that
            // was seen previously, has not been read for "tag age interval" seconds. Both
            // "update interval" and "tag age interval" are set below to two and four seconds
            // respectively.
            directionConfig.setEntryReportEnabled(true);
            directionConfig.setUpdateReportEnabled(true);
            directionConfig.setExitReportEnabled(true);

            // Zero indicates to use max power.  Set a custom value by providing a non-zero value.
            directionConfig.setTxPowerinDbm(25.25);

            // Tells the spatial reader we want to track tags in a narrow area.
            // Alternatively, WIDE tells it we want to track tags in as wide of an area as possible.
            directionConfig.setFieldOfView(DirectionFieldOfView.NARROW);

            // Sets our application to only receive tag updates (or heartbeats) every two seconds.
            directionConfig.setUpdateIntervalSeconds((short) 2);

            // Sets our application to only receive a tag's exit report after it has not been read
            // in any sector for four seconds.
            directionConfig.setTagAgeIntervalSeconds((short) 4);

            // Sets a user limit on the tag population
            directionConfig.setUserTagPopulationLimit((short) 20);

            // The spatial reader's direction feature works best with a smaller tag population,
            // so we can set up a filter on the reader to only track tags we care about.
            //
            // For example, if you run this sample with "-DtargetTag=9999" specified on
            // the command line, only tags whose EPC starts with "9999" will be tracked
            // as part of the tag direction population.
            //
            // How one sets up this filter is shown below (only activated if -DtargetTag argument is provided):
            String targetEpc = System.getProperty(SampleProperties.targetTag);
            if (targetEpc != null) {
                // Setup a tag filter.
                // Only the tags that match this filter will respond.
                TagFilter t1 = settings.getFilters().getTagFilter1();

                // We want to apply the filter to the EPC memory bank.
                t1.setMemoryBank(MemoryBank.Epc);

                // Start matching at the third word (bit 32), since the
                // first two words of the EPC memory bank are the
                // CRC and control bits. BitPointers.Epc is a helper
                // enumeration you can use, so you don't have to remember this.
                t1.setBitPointer(BitPointers.Epc);

                // Only match tags with EPCs that start with -DtargetTag value.
                t1.setTagMask(targetEpc);
                t1.setFilterOp(TagFilterOp.Match);

                // This filter is 16 bits long (one word).
                t1.setBitCount(16);

                // Set the filter mode. Use only the first filter
                settings.getFilters().setMode(TagFilterMode.OnlyFilter1);

                System.out.println("Matching 1st 16 bits of epc " + targetEpc);
            }

            // Pushes our specified configuration to the reader. If the set of enabled sectors violates
            // the rules specified above, an OctaneSDKException will be thrown here.
            reader.applySettings(settings);

            // Initiates our application and we should start to receive direction reports.
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();
            s.close();

            // Stop reading.
            reader.stop();

            // Reset to default settings.
            reader.applyDefaultSettings();

            // Disconnect the reader.
            reader.disconnect();

        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }

    // This static nested class provides the DirectionReportListener implementation
    // to actually receive direction reports.
    public static class DirectionReportListenerImplementation implements
            DirectionReportListener {

        // This function is invoked when a DirectionReport is dispatched.
        public void onDirectionReported(ImpinjReader reader, DirectionReport report) {
            // Here we are dumping selected items... alternatively, you could dump the entire report as a string.
            // e.g. System.out.println(report.toString());
            System.out.println("Direction report");
            System.out.println("   Type = " + report.getReportType());
            System.out.println("   EPC = " + report.getEpc());
            System.out.println("   Last Read Sector = " + report.getLastReadSector());
            System.out.println("   Last Read Timestamp = " + report.getLastReadTime());
            System.out.println("   First Seen Sector = " + report.getFirstSeenSector());
            System.out.println("   First Seen Timestamp = " + report.getFirstSeenTime());
            System.out.println("   Tag Population Status = " + report.getTagPopulationStatus());
        }
    }
}
