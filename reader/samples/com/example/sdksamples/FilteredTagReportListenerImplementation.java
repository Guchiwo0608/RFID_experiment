package com.example.sdksamples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilteredTagReportListenerImplementation implements TagReportListener {

    // Create a Map to store the tags we've read.
    Map<String, Tag> map;

    public FilteredTagReportListenerImplementation() {
        map = new HashMap<>();
    }

    @Override
    public void onTagReported(ImpinjReader reader, TagReport report) {
        List<Tag> tags = report.getTags();

        // This event handler is called asynchronously when tag reports are available.
        // Loop through each tag in the report and print the data.
        for (Tag t : tags) {
            String key;

            if (t.isFastIdPresent()) {
                // If the TID is available through FastID, use it as the key
                key = t.getTid().toHexString();
            } else {
                // Otherwise use the EPC
                key = t.getEpc().toHexString();
            }

            // Only print out the EPC and TID, etc., if this tag hasn't been read before.
            if (map.containsKey(key)) {
                // Move on to the next tag reported.
                continue;
            } else {
                // Add this tag to the list of tags we've read.
                map.put(key, t);
            }

            System.out.print(" EPC: " + t.getEpc().toString());

            if (t.isFastIdPresent()) {
                System.out.print(", TID: " + t.getTid().toString());
            }

            if (t.isAntennaPortNumberPresent()) {
                System.out.print(" antenna: " + t.getAntennaPortNumber());
            }

            if (t.isFirstSeenTimePresent()) {
                System.out.print(" first: " + t.getFirstSeenTime().ToString());
            }

            if (t.isLastSeenTimePresent()) {
                System.out.print(" last: " + t.getLastSeenTime().ToString());
            }

            if (t.isSeenCountPresent()) {
                System.out.print(" count: " + t.getTagSeenCount());
            }

            if (t.isRfDopplerFrequencyPresent()) {
                System.out.print(" doppler: " + t.getRfDopplerFrequency());
            }

            System.out.println("");
        }
    }
}
