package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;

public class QueryStatus {

    public static void main(String[] args) {

        try {
            // Connect to the reader.
            // Pass in a reader hostname or IP address as a
            // command line argument when running the example
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }
            ImpinjReader reader = new ImpinjReader();
            reader.connect(hostname);

            // Get the reader status.
            System.out.println("Querying reader status...");
            Status status = reader.queryStatus();

            // Get the reader temperature
            System.out.println("Reader temperature : " + status.getTemperatureCelsius() + "\u00B0 C");

            // Is the reader running?
            System.out.println("Is singulating : " + status.getIsSingulating());

            // Is a client application connected to the reader?
            System.out.println("Is connected : " + status.getIsConnected());

            // Antenna status
            if (status.getAntennaStatusGroup().getAntennaList().size() != 0) {
                System.out.println("Antenna Status :");
                for (AntennaStatus as : status.getAntennaStatusGroup().getAntennaList()) {
                    System.out.println("  Antenna : " + as.getPortNumber() + " IsConnected : " + as.isConnected());
                }
            }

            // GPI status
            if (status.getGpiStatusGroup().getGpiList().size() != 0) {
                System.out.println("GPI Status :");
                for (GpiStatus gs : status.getGpiStatusGroup().getGpiList()) {
                    System.out.println("  GPI : " + gs.getPortNumber() + " State : " + gs.isState());
                }
            }

            // Antenna Hub status
            if (status.getAntennaHubStatusGroup().getAntennaHubList().size() != 0) {
                System.out.println("Antenna Hub Status :");
                for (AntennaHubStatus ahs : status.getAntennaHubStatusGroup().getAntennaHubList()) {
                    System.out.println("  Antenna Hub : " + ahs.getHubId() + " Connected : " + ahs.getConnected()
                            + " Fault : " + ahs.getFault());
                }
            }

            // Tilt values for readers equipped with a tilt sensor.
            if (status.getTiltSensorValue() != null) {
                System.out.println("Tilt :  x-" + status.getTiltSensorValue().getxAxis()
                        + " y-" + status.getTiltSensorValue().getyAxis());
            } else {
                System.out.println("Tilt :  no data");
            }

            // Wait for the user to press enter.
            System.out.println("Press enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();
            s.close();

            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
