import com.impinj.octane.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

// import org.apache.log4j.BasicConfigurator;

public class ReadTagsPeriodicTrigger {

    public static void main(String[] args) {

        // BasicConfigurator.configure();

        try {

            String hostname = Properties.hostname;
            short[] antennaPortNumbers = Properties.antennaPortNumbers;

            FileWriter fw = new FileWriter(Properties.csvFilePath, true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            pw.print("tagId");
            pw.print(",");
            pw.print("frequency");
            pw.print(",");

            for (int ant = 1; ant < Properties.maxAntennas + 1; ant++) {
                pw.print("antenna" + String.valueOf(ant) + ".firstSeenTime");
                pw.print(",");
                pw.print("antenna" + String.valueOf(ant) + ".LastSeenTime");
                pw.print(",");
                pw.print("antenna" + String.valueOf(ant) + ".rssi");
                pw.print(",");
                pw.print("antenna" + String.valueOf(ant) + ".phase");
                pw.print(",");
                pw.print("antenna" + String.valueOf(ant) + ".doppler");
                pw.print(",");
            }
            pw.print("samplingNumber");
            pw.println();

            pw.close();

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + Properties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();
            System.out.println("Connecting");
            reader.connect(hostname);

            Settings settings = reader.queryDefaultSettings();

            ReportConfig report = settings.getReport();
            report.setIncludeAntennaPortNumber(true);
            report.setIncludeLastSeenTime(true);
            report.setIncludeFirstSeenTime(true);
            report.setIncludeSeenCount(true);
            report.setIncludePeakRssi(true);
            report.setIncludePhaseAngle(true);
            report.setIncludeDopplerFrequency(true);
            report.setIncludeCrc(true);
            report.setIncludeFastId(true);
            report.setIncludeChannel(true);
            report.setMode(ReportMode.BatchAfterStop);

            settings.getAutoStart().setMode(AutoStartMode.Immediate);
            settings.getAutoStop().setMode(AutoStopMode.Duration);
            settings.getAutoStop().setDurationInMs(1000);

            // settings.getTxFrequenciesInMhz().add(916.8);
            // settings.getTxFrequenciesInMhz().add(918.0);
            // settings.getTxFrequenciesInMhz().add(919.2);
            settings.getTxFrequenciesInMhz().add(920.4);

            AntennaConfigGroup antennaConfigGroup = settings.getAntennas();

            antennaConfigGroup.disableAll();
            antennaConfigGroup.enableById(antennaPortNumbers);

            antennaConfigGroup.setIsMaxRxSensitivity(true);
            antennaConfigGroup.setIsMaxTxPower(true);

            // settings.setRfMode(1);
            // settings.setSearchMode(SearchMode.SingleTargetReset);
            // settings.setSession(0);

            reader.setTagReportListener(new TagReportListenerImplementation());

            System.out.println("Applying Settings");
            reader.applySettings(settings);

            // Start reading.
            // System.out.println("Starting");
            // reader.start();

            System.out.println("Press Enter to exit.");
            try (Scanner s = new Scanner(System.in)) {
                s.nextLine();
            }
            reader.stop();
            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
