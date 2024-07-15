package com.example.sdksamples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;

import java.util.Scanner;

public class SetGPO {

    public static void main(String[] args) {

        try {
            String hostname = System.getProperty(SampleProperties.hostname);

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();

            reader.connect(hostname);

            Settings settings = reader.queryDefaultSettings();
            reader.applySettings(settings);

            System.out.println("Setting general purpose outputs");

            for (int i = 1; i <= 4; i++) {
                reader.setGpo(i, true);
                Thread.sleep(1500);
                reader.setGpo(i, false);
            }

            // ImpinReader.ApplySettings will reset to factory settings before applying
            // the provided settings, which, among other things, will drive all GPOs to low.
            // Alternatively, the user can specify to NOT reset to factory settings.
            settings = reader.queryDefaultSettings();
            // Set the GPO high (true).
            reader.setGpo(1, true);
            // Apply settings, with a factory reset.
            reader.applySettings(settings);
            // If we had a way to retrieve the current state of GPO 1, it would now be low (false).
            // Set the GPO high (true) again.
            reader.setGpo(1, true);
            // Apply settings, without a factory reset.
            reader.applySettingsWithoutFactoryReset(settings);
            // If we had a way to retrieve the current state of GPO 1, it should still be high (true).

            System.out.println("Press enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
