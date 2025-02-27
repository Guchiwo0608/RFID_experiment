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
            report.setMode(ReportMode.Individual);

            // settings.getAutoStart().setMode(AutoStartMode.Periodic);
            // settings.getAutoStart().setPeriodInMs(1000);
            // settings.getAutoStop().setMode(AutoStopMode.Duration);
            // settings.getAutoStop().setDurationInMs(500);

            // settings.getTxFrequenciesInMhz().add(916.8);
            // settings.getTxFrequenciesInMhz().add(918.0);
            // settings.getTxFrequenciesInMhz().add(919.2);
            settings.getTxFrequenciesInMhz().add(920.4);

            AntennaConfigGroup antennaConfigGroup = settings.getAntennas();
            short[] antennaPortNumbers = Properties.antennaPortNumbers;

            antennaConfigGroup.disableAll();
            antennaConfigGroup.enableById(antennaPortNumbers);

            antennaConfigGroup.setIsMaxRxSensitivity(true);
            antennaConfigGroup.setTxPowerinDbm(18);
            // antennaConfigGroup.setIsMaxTxPower(true);

            settings.setRfMode(0);
            settings.setSearchMode(SearchMode.DualTarget);
            settings.setSession(1);

            String targetTag = Properties.targetTag;
            settings.getFilters().setMode(TagFilterMode.UseTagSelectFilters);

            TagSelectFilter tagSelectFilter = new TagSelectFilter();
            tagSelectFilter.setMatchingAction(TagFilterStateUnawareAction.Select);
            tagSelectFilter.setNonMatchingAction(TagFilterStateUnawareAction.Unselect);
            tagSelectFilter.setTagMask(targetTag);
            tagSelectFilter.setBitPointer(BitPointers.Epc);
            tagSelectFilter.setMemoryBank(MemoryBank.Epc);
            settings.getFilters().getTagSelectFilterList().add(tagSelectFilter);

            reader.setTagReportListener(new ReportListener());

            System.out.println("Applying Settings");
            reader.applySettings(settings);

            // Start reading.
            System.out.println("Starting");
            reader.start();

            System.out.println("Press Enter to exit.");
            try (Scanner s = new Scanner(System.in)) {
                s.nextLine();
            }
            reader.stop();
            reader.disconnect();
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
