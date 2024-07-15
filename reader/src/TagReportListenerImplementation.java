import com.impinj.octane.ImpinjReader;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagReportListenerImplementation implements TagReportListener {

    int samplingNumber = 0;

    @Override
    public void onTagReported(ImpinjReader reader, TagReport report) {
        List<Tag> tags = report.getTags();
        try {
            FileWriter fw = new FileWriter(Properties.csvFilePath, true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();

            final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (Tag t : tags) {
                final String tid = t.getEpc().toString();
                dataMap.putIfAbsent(tid, new HashMap<String, String>());
                Map<String, String> tagMap = dataMap.get(tid);
                for (int ant = 1; ant < Properties.maxAntennas + 1; ant++) {
                    tagMap.putIfAbsent("antenna" + String.valueOf(ant) + ".firstSeenTime", "");
                    tagMap.putIfAbsent("antenna" + String.valueOf(ant) + ".phase", "");
                    tagMap.putIfAbsent("antenna" + String.valueOf(ant) + ".rssi", "");
                    tagMap.putIfAbsent("antenna" + String.valueOf(ant) + ".doppler", "");
                }
                String antennaNumber = String.valueOf(t.getAntennaPortNumber());
                String firstSeenTime = Instant.ofEpochSecond(Long.valueOf(String.valueOf(t.getFirstSeenTime())))
                        .atZone(ZoneId.of("Etc/GMT-9"))
                        .format(dateFormat);
                String lastSeenTime = Instant.ofEpochSecond(Long.valueOf(String.valueOf(t.getLastSeenTime())))
                        .atZone(ZoneId.of("Etc/GMT-9"))
                        .format(dateFormat);

                tagMap.putIfAbsent("frequency", String.valueOf(t.getChannelInMhz()));
                tagMap.putIfAbsent("samplingNumber", String.valueOf(samplingNumber));
                tagMap.put("antenna" + antennaNumber + ".firstSeenTime", firstSeenTime);
                tagMap.put("antenna" + antennaNumber + ".LastSeenTime", lastSeenTime);
                tagMap.put("antenna" + antennaNumber + ".rssi", String.valueOf(t.getPeakRssiInDbm()));
                tagMap.put("antenna" + antennaNumber + ".phase", String.valueOf(t.getPhaseAngleInRadians()));
                tagMap.put("antenna" + antennaNumber + ".doppler", String.valueOf(t.getRfDopplerFrequency()));

                System.out.printf("Tag ID: %s, Antenna Number: %d, Sampling Number: %d\n", t.getEpc().toString(),
                        t.getAntennaPortNumber(), samplingNumber);
            }

            dataMap.forEach((tid, tagMap) -> {

                boolean isDataCorrectingComplete = true;

                for (int ant = 1; ant < Properties.maxAntennas + 1; ant++) {
                    if (tagMap.get("antenna" + String.valueOf(ant) + ".rssi") == "") {
                        isDataCorrectingComplete = false;
                    }
                }

                if (isDataCorrectingComplete) {

                    pw.print(tid);
                    pw.print(",");
                    pw.print(tagMap.get("frequency"));
                    pw.print(",");

                    for (int ant = 1; ant < Properties.maxAntennas + 1; ant++) {
                        pw.print(tagMap.get("antenna" + String.valueOf(ant) + ".firstSeenTime"));
                        pw.print(",");
                        pw.print(tagMap.get("antenna" + String.valueOf(ant) + ".LastSeenTime"));
                        pw.print(",");
                        pw.print(tagMap.get("antenna" + String.valueOf(ant) + ".rssi"));
                        pw.print(",");
                        pw.print(tagMap.get("antenna" + String.valueOf(ant) + ".phase"));
                        pw.print(",");
                        pw.print(tagMap.get("antenna" + String.valueOf(ant) + ".doppler"));
                        pw.print(",");
                    }

                    pw.print(tagMap.get("samplingNumber"));
                    pw.println();
                }

            });

            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        samplingNumber++;
    }
}
