package com.example.sdksamples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;

import java.util.Scanner;


public class Keepalives {

    public static void main(String[] args) {
        try {
            // Connect to the reader.
            // Pass in a reader hostname or IP address as a
            // command line argument when running the example
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }
            ImpinjReader reader = new ImpinjReader();
            System.out.println("Connecting");
            reader.connect(hostname);

            // Get the default settings
            // We'll use these as a starting point
            // and then modify the settings we're
            // interested in.
            Settings settings = reader.queryDefaultSettings();

            // Enable keepalives.
            settings.getKeepalives().setEnabled(true);
            settings.getKeepalives().setPeriodInMs(3000);

            // Enable link monitor mode.
            // If our application fails to reply to
            // five consecutive keepalive messages,
            // the reader will close the network connection.
            settings.getKeepalives().setEnableLinkMonitorMode(true);
            settings.getKeepalives().setLinkDownThreshold(5);

            // set up a listener for keepalives
            reader.setKeepaliveListener(new KeepAliveListenerImplementation());

            // set up a listener for connection Lost
            reader.setConnectionLostListener(new ConnectionLostListenerImplementation());

            // apply the settings to enable keepalives
            reader.applySettings(settings);

            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
