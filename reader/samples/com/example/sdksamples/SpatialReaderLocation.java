package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SpatialReaderLocation {

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

            // Set the LocationReport listener.
            // This specifies which object to notify when location reports are available.
            reader.setLocationReportListener(new LocationReportListenerImplementation());

            // Get the default settings.
            // We'll use these as a starting point and then modify the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Put the spatial reader into location mode.
            settings.getSpatialConfig().setMode(SpatialMode.Location);

            // Set spatial reader placement parameters.
            PlacementConfig pc = settings.getSpatialConfig().getPlacement();

            // The mounting height of the spatial reader, in centimeters
            pc.setHeightCm((short) 457);

            // These settings aren't required in a single spatial reader environment
            // They can be set to zero (which is the default)
            pc.setFacilityXLocationCm(0);
            pc.setFacilityYLocationCm(0);
            pc.setOrientationDegrees((short) 0);

            // Set spatial reader location parameters.
            LocationConfig lc = settings.getSpatialConfig().getLocation();

            // Set up filtering and aging on the tags.
            lc.setComputeWindowSeconds((short) 10);
            lc.setTagAgeIntervalSeconds((short) 20);

            // Specify how often we want to receive location reports
            lc.setUpdateIntervalSeconds((short) 5);

            // Disable antennas targeting areas from which we may not want location reports,
            // in this case we're disabling antennas 10 and 15
            List<Short> disabledAntennas = new ArrayList<>();
            disabledAntennas.add((short) 10);
            disabledAntennas.add((short) 15);
            lc.setDisabledAntennaList(disabledAntennas);

            // Enable all three report types.
            lc.setEntryReportEnabled(true);
            lc.setExitReportEnabled(true);
            lc.setUpdateReportEnabled(true);

            // Set power used for each antenna by first indicating that the max power
            // should not be used, then by setting an explicit power in dbm.
            lc.setIsMaxTxPower(false);
            lc.setTxPowerinDbm(25.25);

            // set up some general reader settings
            settings.setSession(2);
            settings.setRfMode(1002);

            // You can choose to filter tags...
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

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Start the reader
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();
            s.close();

            // Stop reading.
            reader.stop();

            // Apply the default settings before exiting.
            reader.applyDefaultSettings();

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
