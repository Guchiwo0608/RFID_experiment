////////////////////////////////////////////////////////////////////////////////
//
//    Read Tags Periodic
//
////////////////////////////////////////////////////////////////////////////////

using System;
using System.Text;
using System.IO;
using Impinj.OctaneSdk;


namespace OctaneSdkExamples
{
    class Program
    {
        // Create an instance of the ImpinjReader class.
        static ImpinjReader reader = new ImpinjReader();
        static bool isColumnImported = true;
        static bool isFileInitialized = true;
        static bool isInfoGot = false;

        static void Main(string[] args)
        {
            try
            {
                // Connect to the reader.
                // Pass in a reader hostname or IP address as a 
                // command line argument when running the example
                if (args.Length != 1)
                {
                    Console.WriteLine("Error: No hostname specified.  Pass in the reader hostname as a command line argument when running the Sdk Example.");
                    return;
                }
                string hostname = args[0];
                reader.Connect(hostname);

                // Get the default settings
                // We'll use these as a starting point
                // and then modify the settings we're 
                // interested in.
                Settings settings = reader.QueryDefaultSettings();


                // Tell the reader to include the antenna number,
                // timestamps and tag seen count in all tag reports. 
                // Other fields can be added to the reports
                // in the same way by setting the 
                // appropriate Report.IncludeXXXXXXX property.
                settings.Report.IncludeAntennaPortNumber = true;
                settings.Report.IncludeFirstSeenTime = true;
                settings.Report.IncludeLastSeenTime = true;
                settings.Report.IncludeSeenCount = true;
                settings.Report.IncludePeakRssi = true;
                settings.Report.IncludePhaseAngle = true;
                settings.Report.IncludeFastId = true;
                settings.Report.IncludeDopplerFrequency = true;
                settings.Report.IncludeChannel = true;
                settings.Antennas.TxPowerMax = true;

                settings.TxFrequenciesInMhz.Add(920.4);

                settings.Antennas.RxSensitivityMax = true;
                // settings.Antennas.TxPowerInDbm = 10.0;

                // Send a tag report every time the reader stops (period is over).
                settings.Report.Mode = ReportMode.BatchAfterStop;

                // Reading tags for 5 seconds every 10 seconds
                settings.AutoStart.Mode = AutoStartMode.Periodic;
                settings.AutoStart.PeriodInMs = 1000;
                settings.AutoStop.Mode = AutoStopMode.Duration;
                settings.AutoStop.DurationInMs = 1000;

                // Apply the newly modified settings.
                reader.ApplySettings(settings);

                // Assign the TagsReported event handler.
                // This specifies which method to call
                // when tags reports are available.
                reader.TagsReported += OnTagsReported;

                // Wait for the user to press enter.
                Console.WriteLine("Press enter to exit.");
                Console.ReadLine();

                // Stop reading.
                reader.Stop();

                // Disconnect from the reader.
                reader.Disconnect();
            }
            catch (OctaneSdkException e)
            {
                // Handle Octane SDK errors.
                Console.WriteLine("Octane SDK exception: {0}", e.Message);
            }
            catch (Exception e)
            {
                // Handle other .NET errors.
                Console.WriteLine("Exception : {0}", e);
            }
        }

        static void OnTagsReported(ImpinjReader sender, TagReport report)
        {
            // This event handler is called asynchronously 
            // when tag reports are available.
            // Loop through each tag in the report 
            // and print the data.
            try
            {
                if (!isInfoGot)
                {
                    Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);
                    using (StreamWriter sw = new StreamWriter(@"results2.csv", true, Encoding.GetEncoding("Shift_JIS")))
                    {
                        if (!isFileInitialized)
                        {
                            FileClear(@"results2.csv");
                            isFileInitialized = true;
                        }
                        else
                        {
                            if (!isColumnImported)
                            {
                                File.WriteAllLines(@"results2.csv", new List<String>() { "Tag ID,RSSI,Phase,Frequency,Antenna No,Created At" }, Encoding.GetEncoding("shift-jis"));
                                isColumnImported = true;
                            }
                            else
                            {
                                foreach (Tag tag in report)
                                {
                                    // isInfoGot = true;
                                    if (tag.Epc.ToString().Equals("E200 470F 45D0 6023 A129 010E"))
                                    {
                                        sw.WriteLine(
                                            string.Format("{0}, {1}, {2}, {3}, {4}, {5}",
                                            tag.Epc,
                                            tag.PeakRssiInDbm,
                                            tag.PhaseAngleInRadians,
                                            tag.ChannelInMhz,
                                            tag.AntennaPortNumber,
                                            tag.LastSeenTime.LocalDateTime.ToString()
                                            )
                                        );
                                        Console.WriteLine(
                                            string.Format("{0}, {1}, {2}, {3}, {4}, {5}",
                                            tag.Epc,
                                            tag.PeakRssiInDbm,
                                            tag.PhaseAngleInRadians,
                                            tag.ChannelInMhz,
                                            tag.AntennaPortNumber,
                                            tag.LastSeenTime.LocalDateTime.ToString()
                                            )
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Console.Write(ex);
            }
        }
        static void FileClear(String path)
        {
            using (var fileStream = new FileStream(path, FileMode.Open))
            {
                fileStream.SetLength(0);
            }
        }
    }
}
