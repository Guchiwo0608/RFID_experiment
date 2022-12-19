////////////////////////////////////////////////////////////////////////////////
//
//    Retry Connection
//
// In this example, the application is simulating a situation where the reader may be
// accessed by multiple workers over time, each worker connecting, running the rospec,
// querying tags, and then disconnecting.
// Whenever communicating across a network, there is a possibility of failure.  Here we
// will try to detect these failures and retry requests.
//
////////////////////////////////////////////////////////////////////////////////

using System;
using System.Threading;
using Impinj.OctaneSdk;

namespace OctaneSdkExamples
{
    class Program
    {
        private static ImpinjReader _reader;
        private static int _totalConnections = 0;
        private static int _totalTimeoutsDetected = 0;
        private static int _totalSessionFailures = 0;
        private static bool _setTimer = false;
        private static int _durationInMinutes = 0;
        private static System.Timers.Timer _timer = null;
        private static Thread _watcherThread= null;

        static void Main(string[] args)
        {
            try
            {
                // Pass in a reader hostname or IP address as a 
                // command line argument when running the example
                if (args.Length < 1)
                {
                    Console.WriteLine("Error: No hostname specified.  Pass in the reader hostname as a command line argument when running the Sdk Example.");
                    return;
                }
                string hostname = args[0];

                // Optionally, pass in a duration, to indicate how long the example should run.
                if (args.Length > 1)
                {
                    string duration = args[1];

                    try
                    {
                        _durationInMinutes = Int32.Parse(duration);

                        if (_durationInMinutes > 0)
                        {
                            _setTimer = true;
                            Console.WriteLine("App will run for " + duration + " minutes.");
                        }
                    }
                    catch (FormatException)
                    {
                        throw new Exception("Invalid value for duration: " + duration);
                    }
                }


                // If user specified a duration, start a timer.
                if (_setTimer)
                {
                    _timer = new System.Timers.Timer(_durationInMinutes * 60 * 1000)
                    {
                        AutoReset = false
                    };
                    _timer.Elapsed += (s, e) => {
                        Console.WriteLine("Timer expired... stopping application...");
                        Console.WriteLine("Total connections = " + _totalConnections);
                        Console.WriteLine("Total timeouts detected = " + _totalTimeoutsDetected);
                        Console.WriteLine("Total session failures detected = " + _totalSessionFailures);
                        // Stop rospec, disconnect the reader, and exit.
                        try
                        {
                            _reader.Stop();
                        }
                        catch (OctaneSdkException ose)
                        {
                            Console.WriteLine("TimerTask: OctaneSdkException " + ose.Message);
                        }
                        Console.WriteLine("TimerTask: disconnect reader...");
                        _reader.Disconnect();
                        Environment.Exit(0);
                    };
                    _timer.Start();
                }

                _watcherThread = new Thread(new ThreadStart(WaitForEnterKey));
                _watcherThread.Start();

                _reader = new ImpinjReader();

                // Listen for tag reports.
                _reader.TagsReported += OnTagsReported;

                // Make the initial connection, configure the reader, and disconnect.
                if (InitializeReader(hostname))
                {
                    // Just looping forever, until user hits 'Enter', or timer expires.
                    while (true)
                    {
                        // Connect to reader, start the ROSpec, and then 'query tags' N times, with
                        // a 1-second pause between queries.  Then disconnect the reader.
                        SimulateWorker(hostname);
                    }
                }
                else
                {
                    Console.WriteLine("App terminated... failed to initialize the reader.");
                    Environment.Exit(-3);
                }
            }
            catch (OctaneSdkException ose)
            {
                Console.WriteLine("App terminated... " + ose.Message);
                Environment.Exit(-1);
            }
            catch (Exception ex)
            {
                Console.WriteLine("App terminated... " + ex.Message);
                Console.WriteLine(ex.StackTrace);
                Environment.Exit(-2);
            }
        }

        public static bool InitializeReader(string hostname)
        {
            // The default value for the ConnectTimeout is DEFAULT_TIMEOUT_MS = 5000 ms.  So using a value of 500 ms
            // is a deliberately short value, for the purpose of this exercise.
            _reader.ConnectTimeout = 500;

            // The default value for the MessageTimeout is DEFAULT_TIMEOUT_MS = 5000 ms.  So using a value of 500 ms
            // is a deliberately short value, for the purpose of this exercise.
            _reader.MessageTimeout = 500;

            // Note: the reader's own response time may vary, depending on model, etc.  These timeouts have to be
            // large enough to allow for the reader to respond, plus normal transmission of the request and response.

            // Connect to the reader.  If it fails, keep retrying every 5 seconds.  User will have to
            // press 'enter' to get out of the loop if connection continues to fail (or wait for timer to expire).
            Console.WriteLine("InitializeReader: connect to the reader (" + hostname + ")...");
            while (true)
            {
                try
                {
                    _reader.Connect(hostname);
                    _totalConnections++;
                    break;
                }
                catch (OctaneSdkException e)
                {
                    Console.WriteLine("InitializeReader: connection failure, OctaneSdkException " + e.Message);
                    DelayBeforeRetrying();
                    Console.WriteLine("InitializeReader: retrying connection...");
                }
            }

            // Query the reader's status, get its feature set, and get the default settings.
            // Then configure the settings as we like, apply and save.
            // In theory, any of these can fail due to a network issue, or the reader doesn't respond
            // in time, etc.  Let's put them all in the same try-catch, and retry a few times if there
            // are exceptions that are worth retrying -- since repeating these requests won't cause
            // any problems.
            Settings settings;
            Status status;
            FeatureSet featureSet;
            bool success = false;
            int attempts = 0;
            while (!success)
            {
                attempts++;
                try
                {
                    Console.WriteLine("InitializeReader: query status...");
                    status = _reader.QueryStatus();
                    Console.WriteLine("InitializeReader: query feature set...");
                    featureSet = _reader.QueryFeatureSet();
                    // Get the default settings as a starting point.
                    Console.WriteLine("InitializeReader: query default settings...");
                    settings = _reader.QueryDefaultSettings();

                    Console.WriteLine("=> isConnected: " + status.IsConnected + ", temperature: " + status.TemperatureInCelsius +
                        ", firmwareVersion: " + featureSet.FirmwareVersion + ", modelNumber: " + featureSet.ModelNumber +
                        ", modelName: " + featureSet.ModelName);

                    // Configure the antennas ... since we want to load this example
                    // with tag reports, let's configure all of the antennas.
                    AntennaConfigGroup antennas = settings.Antennas;
                    antennas.DisableAll();
                    foreach (AntennaConfig antennaConfig in antennas)
                    {
                        antennaConfig.IsEnabled = true;
                        antennaConfig.MaxRxSensitivity = false;
                        antennaConfig.RxSensitivityInDbm = -80; // -30 (low) .. -60 .. -80, -90 (high)
                        antennaConfig.MaxTxPower = false;
                        antennaConfig.TxPowerInDbm = 30; // 10 (low) .. 20 .. 30 (high)
                        Console.WriteLine("  port: " + antennaConfig.PortNumber +
                                ", isEnabled: " + antennaConfig.IsEnabled +
                                ", Rx: " + antennaConfig.MaxRxSensitivity + " / " + antennaConfig.RxSensitivityInDbm +
                                ", Tx: " + antennaConfig.MaxTxPower + " / " + antennaConfig.TxPowerInDbm);
                    }

                    // Configure report
                    ReportConfig report = settings.Report;
                    report.IncludeAntennaPortNumber = true;
                    report.IncludeChannel = false;
                    report.IncludeCrc = false;
                    report.IncludeDopplerFrequency = false;
                    report.IncludeFastId = false;
                    report.IncludeFirstSeenTime = false;
                    report.IncludeGpsCoordinates = false;
                    report.IncludeLastSeenTime = false;
                    report.IncludePcBits = false;
                    report.IncludePeakRssi = true;
                    report.IncludePhaseAngle = true;
                    report.IncludeSeenCount = true;
                    report.Mode = ReportMode.WaitForQuery; // ReportMode: BatchAfterStop, Individual, WaitForQuery

                    //  Manage autoStart and auto Stop
                    AutoStartConfig autoStartConfig = settings.AutoStart;
                    autoStartConfig.Mode = AutoStartMode.None; // Immediate/None, Periodic, [GpiTrigger]
                    AutoStopConfig autoStopConfig = settings.AutoStop;
                    autoStopConfig.Mode = AutoStopMode.None; // Duration, [GpiTrigger,] None

                    // Configure keepalives and link monitor mode
                    KeepaliveConfig keepAliveConfig = settings.Keepalives;
                    keepAliveConfig.Enabled = true;
                    keepAliveConfig.PeriodInMs = 3000;
                    keepAliveConfig.EnableLinkMonitorMode = true;
                    keepAliveConfig.LinkDownThreshold = 5;

                    // Apply settings
                    Console.WriteLine("InitializeReader: apply settings...");
                    _reader.ApplySettings(settings);

                    // Save settings
                    Console.WriteLine("InitializeReader: save settings...");
                    _reader.SaveSettings();
                    success = true;
                }
                catch (OctaneSdkException e)
                {
                    Console.WriteLine("InitializeReader: OctaneSdkException " + e.Message);

                    // Is this a failure that is worth retrying?
                    if (IsWorthRetrying(e.Message))
                    {
                        // If we fail four times, that's it, we're done!
                        if (attempts == 4)
                        {
                            return false;
                        }
                        else
                        {
                            DelayBeforeRetrying();
                            Console.WriteLine("InitializeReader: Retry attempt #" + attempts + "...");
                        }
                    }
                    else
                    {
                        // Not worth retrying this failure.
                        // Disconnect the reader, and exit.
                        Console.WriteLine("InitializeReader: disconnect reader...");
                        _reader.Disconnect();
                        return false;
                    }
                }
            }

            // Once configuration done, disconnect the reader so it is available for workers to use.
            Console.WriteLine("InitializeReader: disconnect()");
            _reader.Disconnect();

            return true;
        }

        // This is the 'work' that a client will do with a reader.  It will connect to the reader, start the rospec,
        // query tags a few times, stop the rospec, and disconnect.
        private static void SimulateWorker(String hostname)
        {
            // There are five distinct phases to the work... 1) connect to reader, 2) start rospec
            // 3) query tags, 4) stop rospec, 5) disconnect from reader
            // Starting an rospec twice might cause an error, so we don't want to casually redo that
            // step if, say, a 'query tags' request times out.  Therefore we need to break these up into
            // separate retry loops.

            // Phase 1: Connect to the reader.
            Phase1ConnectToReader(hostname);

            // Phase 2: Start the rospec.
            Phase2StartTheRoSpec();

            // Phase 3: Query tags
            Phase3QueryTags();

            // Phase 4: Stop the rospec
            Phase4StopTheRoSpec();

            // Phase 5: Disconnect the reader.
            Console.WriteLine("SimulateWorker: disconnect reader...");
            _reader.Disconnect();
        }

        private static void Phase1ConnectToReader(string hostname)
        {
            // Phase 1: Connect to the reader.
            bool success = false;
            int attempts = 0;
            Console.WriteLine("Phase1ConnectToReader: connect to the reader (" + hostname + ")...");
            while (!success)
            {
                attempts++;
                try
                {
                    _reader.Connect(hostname);
                    _totalConnections++;
                    success = true;
                }
                catch (OctaneSdkException ose)
                {
                    Console.WriteLine("Phase1ConnectToReader: OctaneSdkException " + ose.Message);

                    // Is this a failure that is worth retrying?
                    if (IsWorthRetrying(ose.Message))
                    {
                        if (attempts < 4)
                        {
                            DelayBeforeRetrying();
                            Console.WriteLine("Phase1ConnectToReader: Retry connection, attempt #" + attempts + "...");
                        }
                        else
                        {
                            // If we fail four times, that's it, we're done!
                            throw;
                        }
                    }
                    else
                    {
                        // Not worth retrying this failure.
                        throw;
                    }
                }
            }
        }

        private static void Phase2StartTheRoSpec()
        {
            // Phase 2: Start the rospec.
            bool success = false;
            int attempts = 0;
            Console.WriteLine("Phase2StartTheRoSpec: starting rospec...");
            while (!success)
            {
                attempts++;
                try
                {
                    // Enable and start the rospec.
                    _reader.Start();
                    success = true;
                }
                catch (OctaneSdkException ose)
                {
                    Console.WriteLine("Phase2StartTheRoSpec: OctaneSdkException " + ose.Message);

                    // Is this a failure that is worth retrying?
                    if (IsWorthRetrying(ose.Message))
                    {
                        if (attempts < 4)
                        {
                            DelayBeforeRetrying();
                            Console.WriteLine("Phase2StartTheRoSpec: Retry start, attempt #" + attempts + "...");
                            // Note: Just in case our start request actually got through to the reader, we
                            // might want to do a reader.stop() here, and swallow any OctaneSdkException
                            // that might be thrown.
                        }
                        else
                        {
                            // If we fail four times, that's it, we're done!
                            Console.WriteLine("Phase2StartTheRoSpec: disconnect reader...");
                            _reader.Disconnect();
                            throw;
                        }
                    }
                    else
                    {
                        // Not worth retrying this failure.
                        Console.WriteLine("Phase2StartTheRoSpec: disconnect reader...");
                        _reader.Disconnect();
                        throw;
                    }
                }
            }
        }

        private static void Phase3QueryTags()
        {
            // Five queries, one second apart.
            for (int i = 0; i < 5; i++)
            {
                Thread.Sleep(1000);

                // Phase 3: query tags
                bool success = false;
                int attempts = 0;
                Console.WriteLine("Phase3QueryTags: query tags...");
                while (!success)
                {
                    attempts++;
                    try
                    {
                        _reader.QueryTags();
                        success = true;
                    }
                    catch (OctaneSdkException ose)
                    {
                        Console.WriteLine("Phase3QueryTags: OctaneSdkException " + ose.Message);

                        // Is this a failure that is worth retrying?
                        if (IsWorthRetrying(ose.Message))
                        {
                            if (attempts < 4)
                            {
                                DelayBeforeRetrying();
                                Console.WriteLine("Phase3QueryTags: Retry query tags, attempt #" + attempts + "...");
                            }
                            else
                            {
                                // If we fail four times, that's it, we're done!
                                Console.WriteLine("Phase3QueryTags: disconnect reader...");
                                _reader.Disconnect();
                                throw;
                            }
                        }
                        else
                        {
                            // Not worth retrying this failure.
                            Console.WriteLine("Phase3QueryTags: disconnect reader...");
                            _reader.Disconnect();
                            throw;
                        }
                    }
                }
            }
        }

        private static void Phase4StopTheRoSpec()
        {
            // Phase 4: stop the rospec
            bool success = false;
            int attempts = 0;
            Console.WriteLine("Phase4StopTheRoSpec: stop rospec...");
            while (!success)
            {
                attempts++;
                try
                {
                    _reader.Stop();
                    success = true;
                }
                catch (OctaneSdkException ose)
                {
                    Console.WriteLine("Phase4StopTheRoSpec: OctaneSdkException " + ose.Message);

                    // Is this a failure that is worth retrying?
                    if (IsWorthRetrying(ose.Message))
                    {
                        if (attempts < 4)
                        {
                            DelayBeforeRetrying();
                            Console.WriteLine("Phase4StopTheRoSpec: Retry stop, attempt #" + attempts + "...");
                        }
                        else
                        {
                            // If we fail four times, that's it, we're done!
                            Console.WriteLine("Phase4StopTheRoSpec: disconnect reader...");
                            _reader.Disconnect();
                            throw;
                        }
                    }
                    else
                    {
                        // Not worth retrying this failure.
                        Console.WriteLine("Phase4StopTheRoSpec: disconnect reader...");
                        _reader.Disconnect();
                        throw;
                    }
                }
            }
        }

        // A timeout or failure to get a session can sometimes be resolved by trying again.
        private static bool IsWorthRetrying(string exceptionMessage)
        {
            bool retry = false;
            if (exceptionMessage.Contains("timeout"))
            {
                retry = true;
                _totalTimeoutsDetected++;
            }
            else if (exceptionMessage.Contains("Failed to get the session"))
            {
                retry = true;
                _totalSessionFailures++;
            }
            return retry;
        }

        private static void DelayBeforeRetrying()
        {
            Thread.Sleep(5 * 1000);
        }


        public static void WaitForEnterKey()
        {
            // Wait for the user to press enter.
            Console.WriteLine("Press Enter to exit" + (_setTimer ? ", or wait for specified duration.\n" : ".\n"));
            Console.ReadLine();
            _timer?.Stop();

            Console.WriteLine("Total connections = " + _totalConnections);
            Console.WriteLine("Total timeouts detected = " + _totalTimeoutsDetected);
            Console.WriteLine("Total session failures detected = " + _totalSessionFailures);
            // Stop rospec, disconnect the reader, and exit.
            Console.WriteLine("WaitForEnterKey: stop rospec...");
            try
            {
                _reader.Stop();
            }
            catch (OctaneSdkException ose)
            {
                Console.WriteLine("WaitForEnterKey: OctaneSdkException " + ose.Message);
            }
            Console.WriteLine("WaitForEnterKey: disconnect reader...");
            _reader.Disconnect();
            Environment.Exit(0);
        }
        static void OnTagsReported(ImpinjReader sender, TagReport report)
        {
            // This event handler is called asynchronously 
            // when tag reports are available.
            // Loop through each tag in the report 
            // and print the data.
            foreach (Tag tag in report)
            {
                Console.WriteLine("EPC: {0} antenna: {1} count: {2} peak_rssi: {3} phase angle: {4}",
                                  tag.Epc, tag.AntennaPortNumber, tag.TagSeenCount, 
                                  tag.PeakRssiInDbm, tag.PhaseAngleInRadians);
            }
        }
    }
}
