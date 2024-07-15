import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

public class ReportListener implements TagReportListener {

    int samplingNumber = 0;

    @Override
    public void onTagReported(ImpinjReader reader, TagReport report) {
        List<Tag> tags = report.getTags();

        try {

            FileWriter fw = new FileWriter(Properties.csvFilePath, true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));

            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
            final TimeZone timeZone = TimeZone.getTimeZone("Asia/Tokyo");
            dateFormat.setTimeZone(timeZone);

            for (Tag t : tags) {
                final String tid = t.getEpc().toString();
                String rawTimestamp = t.getFirstSeenTime().toString();
                Date timestamp = new Date(Long.valueOf(rawTimestamp.substring(0, 13)));
                final Map<String, String> tagReport = new HashMap<String, String>() {
                    {
                        put("frequency", String.valueOf(t.getChannelInMhz()));
                        put("antennaNumber", String.valueOf(t.getAntennaPortNumber()));
                        put("timestamp", t.getFirstSeenTime().toString());
                        put("seenCount", String.valueOf(t.getTagSeenCount()));
                        put("rssi", String.valueOf(t.getPeakRssiInDbm()));
                        put("phase", String.valueOf(t.getPhaseAngleInRadians()));
                        put("doppler", String.valueOf(t.getRfDopplerFrequency()));
                        put("samplingNumber", String.valueOf(samplingNumber));
                    }
                };
                System.out.print("tid: " + tid + ", ");
                System.out.print("antennaNumber: " + tagReport.get("antennaNumber") + ", ");
                System.out.print("phase: " + tagReport.get("phase") + ", ");
                System.out.println("samplingNumber: " + samplingNumber + ", ");
                pw.print(tid);
                pw.print(",");
                pw.print(tagReport.get("frequency"));
                pw.print(",");
                pw.print(tagReport.get("antennaNumber"));
                pw.print(",");
                pw.print(tagReport.get("timestamp"));
                pw.print(",");
                pw.print(tagReport.get("seenCount"));
                pw.print(",");
                pw.print(tagReport.get("rssi"));
                pw.print(",");
                pw.print(tagReport.get("phase"));
                pw.print(",");
                pw.print(tagReport.get("doppler"));
                pw.print(",");
                pw.print(tagReport.get("samplingNumber"));
                pw.println();
                pw.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        samplingNumber++;
    }
}
