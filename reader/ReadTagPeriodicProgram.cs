////////////////////////////////////////////////////////////////////////////////
//
//    Read Tags Periodic
//
////////////////////////////////////////////////////////////////////////////////


using System.Text;
using Impinj.OctaneSdk;
using OctaneSdk.Impinj.OctaneSdk;

namespace OctaneSdkExamples
{
    class Program
    {
        // Create an instance of the ImpinjReader class.
        static ImpinjReader reader = new ImpinjReader();
        // static bool isInfoGot = false;
        static String csvFile = "";
        static int readNumber = 0;

        private static void Main(string[] args)
        {
            try
            {
                // Connect to the reader.
                // Pass in a reader hostname or IP address as a 
                // command line argument when running the example
                if (args.Length != 2)
                {
                    Console.WriteLine("Error: No hostname specified.  Pass in the reader hostname as a command line argument when running the Sdk Example.");
                    return;
                }
                csvFile = @args[1];
                string hostname = args[0];
                reader.Connect(hostname);

                Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);
                using (StreamWriter sw = new StreamWriter(csvFile, true, Encoding.GetEncoding("Shift_JIS")))
                {
                    FileClear();
                    File.WriteAllLines(csvFile, new List<String>() { "Tag ID,RSSI,Phase,Frequency,Antenna No.,Data Number" }, Encoding.GetEncoding("shift-jis"));
                }

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
                settings.Antennas.RxSensitivityMax = true;

                settings.TxFrequenciesInMhz.Add(920.4);

                settings.Antennas.RxSensitivityMax = true;

                settings.Antennas.DisableAll();

                settings.Antennas.GetAntenna(1).IsEnabled = true;
                settings.Antennas.GetAntenna(1).MaxTxPower = true;
                settings.Antennas.GetAntenna(1).MaxRxSensitivity = true;
                settings.Antennas.GetAntenna(2).IsEnabled = true;
                settings.Antennas.GetAntenna(2).MaxTxPower = true;
                settings.Antennas.GetAntenna(2).MaxRxSensitivity = true;
                // settings.Antennas.GetAntenna(3).IsEnabled = true;
                // settings.Antennas.GetAntenna(3).MaxTxPower = true;
                // settings.Antennas.GetAntenna(3).MaxRxSensitivity = true;
                // settings.Antennas.GetAntenna(4).IsEnabled = true;
                // settings.Antennas.GetAntenna(4).MaxTxPower = true;
                // settings.Antennas.GetAntenna(4).MaxRxSensitivity = true;

                // Tell the reader not to send tag reports.
                // We will ask for them.
                // settings.Report.Mode = ReportMode.WaitForQuery;

                settings.RfMode = 1002;
                settings.SearchMode = SearchMode.ReaderSelected;
                settings.Session = 3;
                settings.TagPopulationEstimate = 1;

                // Send a tag report every time the reader stops (period is over).
                settings.Report.Mode = ReportMode.BatchAfterStop;

                // Reading tags for 5 seconds every 10 seconds
                settings.AutoStart.Mode = AutoStartMode.Periodic;
                settings.AutoStart.PeriodInMs = 300;
                settings.AutoStop.Mode = AutoStopMode.Duration;
                settings.AutoStop.DurationInMs = 150;

                settings.Filters.Mode = TagFilterMode.UseTagSelectFilters;

                // TagSelectFilter tagSelectFilter1 = new TagSelectFilter
                // {
                //     MatchAction = StateUnawareAction.Select,
                //     NonMatchAction = StateUnawareAction.Unselect,
                //     TagMask = "E200471081D06023B4E90113",
                //     BitPointer = BitPointers.Epc + 0,
                //     MemoryBank = MemoryBank.Epc
                // };
                // settings.Filters.TagSelectFilters.Add(tagSelectFilter1);
                // TagSelectFilter tagSelectFilter2 = new TagSelectFilter
                // {
                //     MatchAction = StateUnawareAction.Select,
                //     NonMatchAction = StateUnawareAction.Unselect,
                //     TagMask = "E200470FC1906023A8E5010E",
                //     BitPointer = BitPointers.Epc + 0,
                //     MemoryBank = MemoryBank.Epc
                // };
                // settings.Filters.TagSelectFilters.Add(tagSelectFilter2);
                // TagSelectFilter tagSelectFilter3 = new TagSelectFilter
                // {
                //     MatchAction = StateUnawareAction.Select,
                //     NonMatchAction = StateUnawareAction.Unselect,
                //     TagMask = "E200470FC1B06023A8E7010D",
                //     BitPointer = BitPointers.Epc + 0,
                //     MemoryBank = MemoryBank.Epc
                // };
                // settings.Filters.TagSelectFilters.Add(tagSelectFilter3);
                // TagSelectFilter tagSelectFilter4 = new TagSelectFilter
                // {
                //     MatchAction = StateUnawareAction.Select,
                //     NonMatchAction = StateUnawareAction.Unselect,
                //     TagMask = "E200470E7F40602394C0010B",
                //     BitPointer = BitPointers.Epc + 0,
                //     MemoryBank = MemoryBank.Epc
                // };
                // settings.Filters.TagSelectFilters.Add(tagSelectFilter4);
                // TagSelectFilter tagSelectFilter5 = new TagSelectFilter
                // {
                //     MatchAction = StateUnawareAction.Select,
                //     NonMatchAction = StateUnawareAction.Unselect,
                //     TagMask = "E200470FC1C06023A8E8010E",
                //     BitPointer = BitPointers.Epc + 0,
                //     MemoryBank = MemoryBank.Epc
                // };
                // settings.Filters.TagSelectFilters.Add(tagSelectFilter5);
                // TagSelectFilter tagSelectFilter6 = new TagSelectFilter
                // {
                //     MatchAction = StateUnawareAction.Select,
                //     NonMatchAction = StateUnawareAction.Unselect,
                //     TagMask = "E200470F45F06023A12B010A",
                //     BitPointer = BitPointers.Epc + 0,
                //     MemoryBank = MemoryBank.Epc
                // };
                // settings.Filters.TagSelectFilters.Add(tagSelectFilter6);
                TagSelectFilter tagSelectFilter6 = new TagSelectFilter
                {
                    MatchAction = StateUnawareAction.Select,
                    NonMatchAction = StateUnawareAction.Unselect,
                    TagMask = "E200470FC1A06023A8E6010F",
                    BitPointer = BitPointers.Epc + 0,
                    MemoryBank = MemoryBank.Epc
                };
                settings.Filters.TagSelectFilters.Add(tagSelectFilter6);

                // settings.Filters.TagFilter1.MemoryBank = MemoryBank.Epc;
                // settings.Filters.TagFilter1.BitPointer = BitPointers.Epc + 0;
                // settings.Filters.TagFilter1.TagMask = "E200470FC1C06023A8E8010E";
                // settings.Filters.TagFilter2.MemoryBank = MemoryBank.Epc;
                // settings.Filters.TagFilter2.BitPointer = BitPointers.Epc + 0;
                // settings.Filters.TagFilter2.TagMask = "E200470F45F06023A12B010A";

                // Apply the newly modified settings.
                reader.ApplySettings(settings);

                // Assign the TagsReported event handler.
                // This specifies which method to call
                // when tags reports are available.
                reader.TagsReported += OnTagsReported;

                // Assign an event handler that will
                // be called when the tag report buffer is almost full.
                // reader.ReportBufferWarning += OnReportBufferWarning;

                // Assign an event handler that will
                // be called when the tag report buffer has overflowed.
                // reader.ReportBufferOverflow += OnReportBufferOverflow;

                // Wait for the user to press enter.
                Console.WriteLine("host:{0} is connected.", hostname);

                // Wait a while.
                // Console.WriteLine("Waiting while the reader reads tags.");
                // Thread.Sleep(5000);

                // Ask for the tag reports.
                // reader.QueryTags();

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

        static void OnReportBufferOverflow(ImpinjReader reader, ReportBufferOverflowEvent e)
        {
            Console.WriteLine("The tag report buffer has overflowed!");
        }

        static void OnReportBufferWarning(ImpinjReader reader, ReportBufferWarningEvent e)
        {
            Console.WriteLine("The tag report buffer is {0}% full!", e.PercentFull);
        }

        static void OnTagsReported(ImpinjReader sender, TagReport report)
        {

            Dictionary<ushort, Tag> antennasReported = new Dictionary<ushort, Tag>();
            ushort key;

            // This event handler is called asynchronously 
            // when tag reports are available.
            // Loop through each tag in the report 
            // and print the data.
            try
            {
                Encoding.RegisterProvider(CodePagesEncodingProvider.Instance);
                using (StreamWriter sw = new StreamWriter(csvFile, true, Encoding.GetEncoding("Shift_JIS")))
                {
                    Boolean isIncludeAntenna1 = false;
                    Boolean isIncludeAntenna2 = false;
                    Boolean isIncludeAntenna3 = true;
                    Boolean isIncludeAntenna4 = true;
                    foreach (Tag tag in report)
                    {
                        if (tag.AntennaPortNumber == 1)
                        {
                            isIncludeAntenna1 = true;
                        }
                        if (tag.AntennaPortNumber == 2)
                        {
                            isIncludeAntenna2 = true;
                        }
                        // if (tag.AntennaPortNumber == 3)
                        // {
                        //     isIncludeAntenna3 = true;
                        // }
                        // if (tag.AntennaPortNumber == 4)
                        // {
                        //     isIncludeAntenna4 = true;
                        // }
                    }
                    if ((isIncludeAntenna1 && isIncludeAntenna2 && isIncludeAntenna3 && isIncludeAntenna4))
                    {
                        foreach (Tag tag in report)
                        {
                            key = tag.AntennaPortNumber;
                            if (!antennasReported.ContainsKey(key))
                            {
                                sw.WriteLine(
                                    string.Format("{0}, {1}, {2}, {3}, {4}, {5}",
                                    tag.Epc,
                                    tag.PeakRssiInDbm,
                                    tag.PhaseAngleInRadians,
                                    tag.ChannelInMhz,
                                    tag.AntennaPortNumber,
                                    readNumber
                                    )
                                );
                                Console.WriteLine(
                                    string.Format("{0}, {1}, {2}, {3}, {4}, {5}",
                                    tag.Epc,
                                    tag.PeakRssiInDbm,
                                    tag.PhaseAngleInRadians,
                                    tag.ChannelInMhz,
                                    tag.AntennaPortNumber,
                                    readNumber
                                    )
                                );
                                antennasReported.Add(key, tag);
                            }
                        }
                        readNumber += 1;
                    }
                }
            }
            catch (Exception ex)
            {
                Console.Write(ex);
            }
        }
        static void FileClear()
        {
            using (var fileStream = new FileStream(csvFile, FileMode.Open))
            {
                fileStream.SetLength(0);
            }
        }
    }
}
