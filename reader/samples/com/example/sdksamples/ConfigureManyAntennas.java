package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConfigureManyAntennas {

    // This examples shows the different ways to configure antennas.
    public static void main(String[] args) {
        try {
            String hostname = System.getProperty(SampleProperties.hostname);

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();

            // Connect
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);

            FeatureSet features = reader.queryFeatureSet();


            // Get the default settings
            Settings settings = reader.queryDefaultSettings();

            // send a tag report for every tag read
            settings.getReport().setMode(ReportMode.Individual);
            settings.getReport().setIncludeAntennaPortNumber(true);

            // disable all antennas, then enable our special set
            AntennaConfigGroup ac = settings.getAntennas();

            ac.disableAll();

            // For xarray, you can enable by sector or ring. For xspan, you can enable by sector.
            // System.out.println("enabling ring 4 and 7");
            // ac.enableById(AntennaUtilities.GetAntennaIdsByRing(Arrays.asList(4, 7), ReaderModel.XArray));
            // System.out.println("enabling sector 3,4 and 5");
            // ac.enableById(AntennaUtilities.GetAntennaIdsBySector(Arrays.asList(3, 4, 5), ReaderModel.XArray));

            // For spatial readers (xarray), you can specify an optimized antenna list
            System.out.println("enabling optimized antenna list");
            ArrayList<AntennaConfig> listAntennaConfig = ac.getAntennaConfigs();
            listAntennaConfig.clear();

            for (Integer antenna : AntennaUtilities.GetOptimizedAntennaList(ReaderModel.XArray)) {
                 listAntennaConfig.add(new AntennaConfig(antenna));
            }

            // Or set each antenna individually
            // ac.getAntenna((short) 1).setEnabled(true);
            // ac.getAntenna((short) 3).setEnabled(true);
            // ...

            // set all to max power and sensitivity
            ac.setIsMaxTxPower(true);
            ac.setIsMaxRxSensitivity(true);

            // or set all antennas to a specific value in dBm
            // String power = System.getProperty(SampleProperties.powerDbm);
            // if (power != null) {
            //    double pwr = Double.parseDouble(power);
            //    ac.setIsMaxTxPower(false);
            //    ac.setTxPowerinDbm(pwr);
            // }
            // String rxSens = System.getProperty(SampleProperties.sensitivityDbm);
            // if (rxSens != null) {
            //    double rx = Double.parseDouble(rxSens);
            //    ac.setIsMaxRxSensitivity(false);
            //    ac.setRxSensitivityinDbm(rx);
            // }

            // Apply the new settings
            reader.applySettings(settings);

            // connect a listener
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Start the reader
            reader.start();

            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

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
