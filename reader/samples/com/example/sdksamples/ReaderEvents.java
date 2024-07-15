package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;


public class ReaderEvents {

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

            // this will eventually cause buffer events 
            settings.getReport().setMode(ReportMode.Individual);
            settings.setRfMode(1002);
            settings.setSearchMode(SearchMode.DualTarget);
            settings.setSession(2);

            // Enable all of the antenna ports.
            settings.getAntennas().enableAll();

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Assign handlers for various reader events.

            // turn me on to listen for antenna connect and disconnect
            reader.setAntennaChangeListener(new AntennaChangeListenerImplementation());

            // turn me on to listen for GPI changes
            reader.setGpiChangeListener(new GpiChangeListenerImplementation());

            // turn me on to be notified when the connection to the reader is lost
            reader.setConnectionLostListener(new ConnectionLostListenerImplementation());

            // turn me on to be notified when we receive a keep alive from the reader
            reader.setKeepaliveListener(new KeepAliveListenerImplementation());

            // turn me on to be notified when the internal reader buffer is filling,
            // indicating that there is a backlog on the reader
            reader.setBufferWarningListener(new BufferWarningListenerImplementation());

            // turn me on to be notified when the internal reader buffer has overflowed
            // and reports and events are being discarded
            reader.setBufferOverflowListener(new BufferOverflowListenerImplementation());

            // turn me on to learn when another application tries to connect to this reader
            reader.setConnectionAttemptListener(new ConnectionAttemptListenerImplementation());

            // turn me on to learn when the reader starts and stops inventory
            reader.setReaderStartListener(new ReaderStartListenerImplementation());
            reader.setReaderStopListener(new ReaderStopListenerImplementation());

            // don't turn on this handler unless we want to see all the tag printouts
            // reader.setTagReportListener(new TagReportListenerImplementation());

            // Start the reader (required for antenna events).
            System.out.println("Starting  " + hostname);
            reader.start();

            System.out.println("Press enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            System.out.println("Stopping  " + hostname);
            reader.stop();

            System.out.println("Disconnecting from " + hostname);
            reader.disconnect();

            System.out.println("Done");
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
