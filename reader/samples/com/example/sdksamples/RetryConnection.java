package com.example.sdksamples;

import com.impinj.octane.*;

import java.nio.channels.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

// In this example, the application is simulating a situation where the reader may be
// accessed by multiple workers over time, each worker connecting, running the rospec,
// querying tags, and then disconnecting.
// Whenever communicating across a network, there is a possibility of failure.  Here we
// will try to detect these failures and retry requests.

public class RetryConnection {
    private static ImpinjReader reader;
    private static int totalConnections = 0;
    private static int totalTimeoutsDetected = 0;
    private static int totalSessionFailures = 0;
    private static boolean setTimer = false;
    private static int durationInMinutes = 0;
    private static Timer timer = null;

    public static void main(String[] args) {
        try {
            String hostname = System.getProperty(SampleProperties.hostname);
            String duration = System.getProperty(SampleProperties.duration);

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            if (duration != null) {
                try {
                    durationInMinutes = Integer.parseInt(duration);
                    if (durationInMinutes > 0) {
                        setTimer = true;
                        System.out.println("App will run for " + duration + " minutes.");
                    }
                } catch (NumberFormatException nfe) {
                    throw new Exception("Invalid value for " + SampleProperties.duration + ": " + duration);
                }
            }


            // If user specified a duration, start a timer.
            if (setTimer) {
                TimerTask task = new TimerTask() {
                    public void run() {
                        System.out.println("Timer expired... stopping application...");
                        System.out.println("Total connections = " + totalConnections);
                        System.out.println("Total timeouts detected = " + totalTimeoutsDetected);
                        System.out.println("Total session failures detected = " + totalSessionFailures);
                        // Stop rospec, disconnect the reader, and exit.
                        try {
                            reader.stop();
                        } catch (OctaneSdkException ose) {
                            System.out.println("TimerTask: OctaneSdkException " + ose.getMessage());
                        }
                        System.out.println("TimerTask: disconnect reader...");
                        reader.disconnect();
                        System.exit(0);
                    }
                };
                timer = new Timer("Timer");

                timer.schedule(task, (long) durationInMinutes * 60 * 1000);
            }

            // Let the user hit enter to stop early, or as a graceful stop if no duration specified.
            new Thread(new Runnable() {
                public void run() {
                    // Wait for the user to press enter.
                    System.out.println("Press Enter to exit" + (setTimer ? ", or wait for specified duration.\n" : ".\n"));
                    Scanner s = new Scanner(System.in);
                    s.nextLine();
                    timer.cancel();
                    System.out.println("Total connections = " + totalConnections);
                    System.out.println("Total timeouts detected = " + totalTimeoutsDetected);
                    System.out.println("Total session failures detected = " + totalSessionFailures);
                    // Stop rospec, disconnect the reader, and exit.
                    System.out.println("watcher: stop rospec...");
                    try {
                        reader.stop();
                    } catch (OctaneSdkException ose) {
                        System.out.println("watcher: OctaneSdkException " + ose.getMessage());
                    }
                    System.out.println("watcher: disconnect reader...");
                    reader.disconnect();
                    System.exit(0);
                }
            }).start();

            reader = new ImpinjReader();

            // Listen for tag reports.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Make the initial connection, configure the reader, and disconnect.
            if (initializeReader(hostname)) {
                // Just looping forever, until user hits 'Enter', or timer expires.
                while (true) {
                    // Connect to reader, start the ROSpec, and then 'query tags' N times, with
                    // a 1-second pause between queries.  Then disconnect the reader.
                    simulateWorker(hostname);
                }
            }
            else
            {
                System.out.println("App terminated... failed to initialize the reader.");
                System.exit(-3);
            }

        } catch (OctaneSdkException ex) {
            System.out.println("App terminated... " + ex.getMessage());
            System.exit(-1);
        } catch (Exception ex) {
            System.out.println("App terminated... " + ex.getMessage());
            ex.printStackTrace(System.out);
            System.exit(-2);
        }
    }



    public static boolean initializeReader(String hostname) {
        // The default value for the ConnectTimeout is DEFAULT_TIMEOUT_MS = 5000 ms.  So using a value of 500 ms
        // is a deliberately short value, for the purpose of this exercise.
        reader.setConnectTimeout(500);

        // The default value for the MessageTimeout is DEFAULT_TIMEOUT_MS = 5000 ms.  So using a value of 500 ms
        // is a deliberately short value, for the purpose of this exercise.
        reader.setMessageTimeout(500);

        // Note: the reader's own response time may vary, depending on model, etc.  These timeouts have to be
        // large enough to allow for the reader to respond, plus normal transmission of the request and response.

        // Connect to the reader.  If it fails, keep retrying every 5 seconds.  User will have to
        // press 'enter' to get out of the loop if connection continues to fail (or wait for timer to expire).
        System.out.println("initializeReader: connect to the reader (" + hostname + ")...");
        while (true) {
            try {
                reader.connect(hostname);
                totalConnections++;
                break;
            } catch (UnresolvedAddressException e) {
                System.out.println("initializeReader: connection failure, UnresolvedAddressException");
                return false;
            } catch (OctaneSdkException e) {
                System.out.println("initializeReader: connection failure, OctaneSdkException " + e.getMessage());
                delayBeforeRetrying();
                System.out.println("initializeReader: retrying connection...");
            }
        }

        // Query the reader's status, get its feature set, and get the default settings.
        // Then configure the settings as we like, apply and save.
        // In theory, any of these can fail due to a network issue, or the reader doesn't respond
        // in time, etc.  Let's put them all in the same try-catch, and retry a few times if there
        // are exceptions that are worth retrying -- since repeating these requests won't cause
        // any problems.
        Settings settings = null;
        Status status = null;
        FeatureSet featureSet = null;
        boolean success = false;
        int attempts = 0;
        while (!success) {
            attempts++;
            try {
                System.out.println("initializeReader: query status...");
                status = reader.queryStatus();
                System.out.println("initializeReader: query feature set...");
                featureSet = reader.queryFeatureSet();
                // Get the default settings as a starting point.
                System.out.println("initializeReader: query default settings...");
                settings = reader.queryDefaultSettings();

                System.out.println("=> isConnected: " + status.getIsConnected() + ", temperature: " + status.getTemperatureCelsius() +
                    ", firmwareVersion: " + featureSet.getFirmwareVersion() + ", modelNumber: " + featureSet.getModelNumber() +
                    ", modelName: " + featureSet.getModelName() );

                // Configure the antennas ... since we want to load this example
                // with tag reports, let's configure all of the antennas.
                AntennaConfigGroup antennas = settings.getAntennas();
                antennas.disableAll();
                for (AntennaConfig antennaConfig : antennas) {
                    antennaConfig.setEnabled(true);
                    antennaConfig.setIsMaxRxSensitivity(false);
                    antennaConfig.setRxSensitivityinDbm(-80); // -30 (low) .. -60 .. -80, -90 (high)
                    antennaConfig.setIsMaxTxPower(false);
                    antennaConfig.setTxPowerinDbm(30); // 10 (low) .. 20 .. 30 (high)
                    System.out.println("  port: " + antennaConfig.getPortNumber() +
                            ", isEnabled: " + antennaConfig.isEnabled() +
                            ", Rx: " + antennaConfig.getIsMaxRxSensitivity() + " / " + antennaConfig.getRxSensitivityinDbm() +
                            ", Tx: " + antennaConfig.getIsMaxTxPower() + " / " + antennaConfig.getTxPowerinDbm());
                }

                // Configure report
                ReportConfig report = settings.getReport();
                report.setIncludeAntennaPortNumber(true);
                report.setIncludeChannel(false);
                report.setIncludeCrc(false);
                report.setIncludeDopplerFrequency(false);
                report.setIncludeFastId(false);
                report.setIncludeFirstSeenTime(false);
                report.setIncludeGpsCoordinates(false);
                report.setIncludeLastSeenTime(false);
                report.setIncludePcBits(false);
                report.setIncludePeakRssi(true);
                report.setIncludePhaseAngle(true);
                report.setIncludeSeenCount(true);
                report.setMode(ReportMode.WaitForQuery); // ReportMode: BatchAfterStop, Individual, WaitForQuery

                //  Manage autoStart and auto Stop
                AutoStartConfig autoStartConfig = settings.getAutoStart();
                autoStartConfig.setMode(AutoStartMode.None); // Immediate/None, Periodic, [GpiTrigger]
                AutoStopConfig autoStopConfig = settings.getAutoStop();
                autoStopConfig.setMode(AutoStopMode.None); // Duration, [GpiTrigger,] None

                // Configure keepalives and link monitor mode
                KeepaliveConfig keepAliveConfig = settings.getKeepalives();
                keepAliveConfig.setEnabled(true);
                keepAliveConfig.setPeriodInMs(3000);
                keepAliveConfig.setEnableLinkMonitorMode(true);
                keepAliveConfig.setLinkDownThreshold(5);

                // Apply settings
                System.out.println("initializeReader: apply settings...");
                reader.applySettings(settings);

                // Save settings
                System.out.println("initializeReader: save settings...");
                reader.saveSettings();
                success = true;
            } catch (OctaneSdkException e) {
                System.out.println("initializeReader: OctaneSdkException " + e.getMessage());

                // Is this a failure that is worth retrying?
                if (isWorthRetrying(e.getMessage())) {
                    // If we fail four times, that's it, we're done!
                    if (attempts == 4) {
                        return false;
                    } else {
                        delayBeforeRetrying();
                        System.out.println("initializeReader: Retry attempt #" + attempts + "...");
                    }
                } else {
                    // Not worth retrying this failure.
                    // Disconnect the reader, and exit.
                    System.out.println("initializeReader: disconnect reader...");
                    reader.disconnect();
                    return false;
                }
            }
        }

        // Once configuration done, disconnect the reader so it is available for workers to use.
        System.out.println("initializeReader: disconnect()");
        reader.disconnect();

        return true;
    }

    // A timeout or failure to get a session can sometimes be resolved by trying again.
    private static boolean isWorthRetrying(String exceptionMessage) {
        boolean retry = false;
        if (exceptionMessage.contains("timeout")) {
            retry = true;
            totalTimeoutsDetected++;
        } else if (exceptionMessage.contains("Failed to get the session")) {
            retry = true;
            totalSessionFailures++;
        }
        return retry;
    }

    private static void delayBeforeRetrying() {
        try
        {
            Thread.sleep(5*1000);
        }
        catch (InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }

    // This is the 'work' that a client will do with a reader.  It will connect to the reader, start the rospec,
    // query tags a few times, stop the rospec, and disconnect.
    private static void simulateWorker(String hostname) throws OctaneSdkException
    {
        // There are five distinct phases to the work... 1) connect to reader, 2) start rospec
        // 3) query tags, 4) stop rospec, 5) disconnect from reader
        // Starting an rospec twice might cause an error, so we don't want to casually redo that
        // step if, say, a 'query tags' request times out.  Therefore we need to break these up into
        // separate retry loops.

        // Phase 1: Connect to the reader.
        phase1ConnectToReader(hostname);

        // Phase 2: Start the rospec.
        phase2StartTheRoSpec();

        // Phase 3: Query tags.
        phase3QueryTags();

        // Phase 4: Stop the rospec.
        phase4StopTheRoSpec();

        // Phase 5: Disconnect the reader.
        System.out.println("simulateWorker: disconnect reader...");
        reader.disconnect();
    }

    private static void phase1ConnectToReader(String hostname) throws OctaneSdkException {
        // Phase 1: Connect to the reader.
        boolean success = false;
        int attempts = 0;
        System.out.println("phase1ConnectToReader: connect to the reader (" + hostname + ")...");
        while (!success) {
            attempts++;
            try {
                reader.connect(hostname);
                totalConnections++;
                success = true;
            } catch (OctaneSdkException ose) {
                System.out.println("phase1ConnectToReader: OctaneSdkException " + ose.getMessage());

                // Is this a failure that is worth retrying?
                if (isWorthRetrying(ose.getMessage())) {
                    if (attempts < 4) {
                        delayBeforeRetrying();
                        System.out.println("phase1ConnectToReader: Retry connection, attempt #" + attempts + "...");
                    } else {
                        // If we fail four times, that's it, we're done!
                        throw ose;
                    }
                } else {
                    // Not worth retrying this failure.
                    throw ose;
                }
            }
        }
    }

    private static void phase2StartTheRoSpec() throws OctaneSdkException {
        // Phase 2: Start the rospec.
        boolean success = false;
        int attempts = 0;
        System.out.println("phase2StartTheRoSpec: starting rospec...");
        while (!success) {
            attempts++;
            try {
                // Enable and start the rospec.
                reader.start();
                success = true;
            } catch (OctaneSdkException ose) {
                System.out.println("phase2StartTheRoSpec: OctaneSdkException " + ose.getMessage());

                // Is this a failure that is worth retrying?
                if (isWorthRetrying(ose.getMessage())) {
                    if (attempts < 4) {
                        delayBeforeRetrying();
                        System.out.println("phase2StartTheRoSpec: Retry start, attempt #" + attempts + "...");
                        // Note: Just in case our start request actually got through to the reader, we
                        // might want to do a reader.stop() here, and swallow any OctaneSdkException
                        // that might be thrown.
                    } else {
                        // If we fail four times, that's it, we're done!
                        System.out.println("phase2StartTheRoSpec: disconnect reader...");
                        reader.disconnect();
                        throw ose;
                    }
                } else {
                    // Not worth retrying this failure.
                    System.out.println("phase2StartTheRoSpec: disconnect reader...");
                    reader.disconnect();
                    throw ose;
                }
            }
        }
    }

    private static void phase3QueryTags() throws OctaneSdkException {
        // Five queries, one second apart.
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            // Phase 3: query tags
            boolean success = false;
            int attempts = 0;
            System.out.println("phase3QueryTags: query tags...");
            while (!success) {
                attempts++;
                try {
                    reader.queryTags();
                    success = true;
                } catch (OctaneSdkException ose) {
                    System.out.println("phase3QueryTags: OctaneSdkException " + ose.getMessage());

                    // Is this a failure that is worth retrying?
                    if (isWorthRetrying(ose.getMessage())) {
                        if (attempts < 4) {
                            delayBeforeRetrying();
                            System.out.println("phase3QueryTags: Retry query tags, attempt #" + attempts + "...");
                        } else {
                            // If we fail four times, that's it, we're done!
                            System.out.println("phase3QueryTags: disconnect reader...");
                            reader.disconnect();
                            throw ose;
                        }
                    } else {
                        // Not worth retrying this failure.
                        System.out.println("phase3QueryTags: disconnect reader...");
                        reader.disconnect();
                        throw ose;
                    }
                }
            }
        }
    }

    private static void phase4StopTheRoSpec() throws OctaneSdkException {
        // Phase 4: stop the rospec
        boolean success = false;
        int attempts = 0;
        System.out.println("phase4StopTheRoSpec: stop rospec...");
        while (!success) {
            attempts++;
            try {
                reader.stop();
                success = true;
            } catch (OctaneSdkException ose) {
                System.out.println("phase4StopTheRoSpec: OctaneSdkException " + ose.getMessage());

                // Is this a failure that is worth retrying?
                if (isWorthRetrying(ose.getMessage())) {
                    if (attempts < 4) {
                        delayBeforeRetrying();
                        System.out.println("phase4StopTheRoSpec: Retry stop, attempt #" + attempts + "...");
                    } else {
                        // If we fail four times, that's it, we're done!
                        System.out.println("phase4StopTheRoSpec: disconnect reader...");
                        reader.disconnect();
                        throw ose;
                    }
                } else {
                    // Not worth retrying this failure.
                    System.out.println("phase4StopTheRoSpec: disconnect reader...");
                    reader.disconnect();
                    throw ose;
                }
            }
        }
    }
}

