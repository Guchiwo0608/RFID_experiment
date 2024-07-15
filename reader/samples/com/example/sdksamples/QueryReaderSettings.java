package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryReaderSettings {

    // Create an instance of the ImpinjReader class.
    static ImpinjReader reader = new ImpinjReader();

    static void DisplayCurrentSettings() throws OctaneSdkException {
        // Query the current reader settings and print some of the results.
        System.out.println("Reader Settings");
        System.out.println("---------------");

        Settings settings = reader.querySettings();
        System.out.println("RF mode : " + settings.getRfMode());
        System.out.println("Search mode : " + settings.getSearchMode());
        System.out.println("Session : " + settings.getSession());

        ArrayList<AntennaConfig> ac = settings.getAntennas().getAntennaConfigs();

        if (ac.get(0).getIsMaxRxSensitivity()) {
            System.out.println("Rx sensitivity (Antenna 1) : Max");
        } else {
            System.out.println("Rx sensitivity (Antenna 1) : "
                    + ac.get(0).getRxSensitivityinDbm() + " dBm");
        }

        if (ac.get(0).getIsMaxTxPower()) {
            System.out.println("Tx power (Antenna 1) : Max");
        } else {
            System.out.println("Tx power (Antenna 1) : "
                    + ac.get(0).getTxPowerinDbm() + " dBm");
        }

        System.out.println("");
    }

    public static void main(String[] args) {

        try {
            String hostname = System.getProperty(SampleProperties.hostname);
            String folderPath = System.getProperty(SampleProperties.folderPath);
            if (folderPath == null) {
                folderPath = "";
            }

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            reader.connect(hostname);

            // Query the reader features and print the results.
            System.out.println("Reader Features");
            System.out.println("---------------");
            FeatureSet features = reader.queryFeatureSet();
            System.out.println("Model name : " + features.getModelName());
            System.out.println("Model number : " + features.getModelNumber());
            System.out.println("Reader model : " + features.getReaderModel().toString());
            System.out.println("Firmware version : " + features.getFirmwareVersion());
            System.out.println("Antenna count : " + features.getAntennaCount());
            List<TxPowerTableEntry> txPowers = features.getTxPowers();
            System.out.println("Max tx power : " + ((txPowers != null && !txPowers.isEmpty()) ?
                    txPowers.get(txPowers.size() - 1).Dbm : 0) + " dBm\n");

            // Write the reader features to file.
            features.save(folderPath + "features.xml");

            // Query the current reader status.
            System.out.println("Reader Status");
            System.out.println("---------------");
            Status status = reader.queryStatus();
            System.out.println("Is connected : " + status.getIsConnected());
            System.out.println("Is singulating : " + status.getIsSingulating());
            System.out.println("Temperature : " + status.getTemperatureCelsius() + "\u00B0 C");
            System.out.println("");

            // Configure the reader with the default settings.
            reader.applyDefaultSettings();

            // Display the current reader settings.
            DisplayCurrentSettings();

            // Save the settings to file in XML format.
            System.out.println("Saving settings to file.");
            Settings settings = reader.querySettings();
            settings.save(folderPath + "settings.xml");

            // Wait here, so we can edit the
            // settings.xml file in a text editor.
            System.out.println("Edit settings.xml and press enter.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Load the modified settings from file.
            System.out.println("Loading settings from file.");
            settings = Settings.load(folderPath + "settings.xml");

            // Apply the settings we just loaded from file.
            System.out.println("Applying settings from file.\n");
            reader.applySettings(settings);

            // Display the settings again to show the changes.
            DisplayCurrentSettings();

            // Wait for the user to press enter.
            System.out.println("Press enter to exit.");
            s.nextLine();
            s.close();

            // Disconnect from the reader.
            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (java.io.IOException ex) {
            System.out.println("Exception : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
