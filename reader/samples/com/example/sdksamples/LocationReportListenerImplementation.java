package com.example.sdksamples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.LocationReport;
import com.impinj.octane.LocationReportListener;

public class LocationReportListenerImplementation implements
        LocationReportListener {

    @Override
    public void onLocationReported(ImpinjReader reader, LocationReport report) {
        System.out.println("Location report");
        System.out.println("   Type = " + report.getReportType());
        System.out.println("   EPC = " + report.getEpc());
        System.out.println("   X = " + report.getLocationXCm() + " cm");
        System.out.println("   Y = " + report.getLocationYCm() + " cm");
        System.out.println("   Timestamp = " + report.getTimestamp()
                + " (" + report.getTimestamp().getLocalDateTime() + ")");
        System.out.println("   Read count = " + report.getConfidenceFactors().getReadCount());
    }
}
