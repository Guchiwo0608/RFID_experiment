////////////////////////////////////////////////////////////////////////////////
//
//    Read Tags
//
////////////////////////////////////////////////////////////////////////////////

using System;
using Impinj.OctaneSdk;

namespace OctaneSdkExamples
{
    class Program
    {
        // Create an instance of the ImpinjReader class.
        static ImpinjReader reader = new ImpinjReader();

        static void Main(string[] args)
        {
            try
            {
                // Connect to the reader.
                // Pass in a reader hostname or IP address as a 
                // command line argument when running the example
                if (args.Length < 1)
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

                // Tell the reader to include the XPC Words if available
                // in all tag reports. Other fields can be added
                // to the reports in the same way by setting the 
                // appropriate Report.IncludeXXXXXXX property.
                settings.Report.IncludeXPCWords = (args.Length == 2 && args[1] == "XPCWordsReporting" ? true : false);

                // The reader can be set into various modes in which reader
                // dynamics are optimized for specific regions and environments.
                // The following mode, AutoSetDenseReaderDeepScan (1002), monitors RF noise
                // and interference then automatically and continuously optimizes
                // the reader’s configuration
                settings.RfMode = 1002;
                settings.SearchMode = SearchMode.DualTarget;
                settings.Session = 2;

                // Enable antenna #1. Disable all others.
                settings.Antennas.DisableAll();
                settings.Antennas.GetAntenna(1).IsEnabled = true;

                // Set the Transmit Power and 
                // Receive Sensitivity to the maximum.
                settings.Antennas.GetAntenna(1).MaxTxPower = true;
                settings.Antennas.GetAntenna(1).MaxRxSensitivity = true;

                // Apply the newly modified settings.
                reader.ApplySettings(settings);

                // Assign the TagsReported event handler.
                // This specifies which method to call
                // when tags reports are available.
                reader.TagsReported += OnTagsReported;

                // Start reading.
                reader.Start();

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
                Console.WriteLine("Exception : {0}", e.Message);
            }
        }

        static void OnTagsReported(ImpinjReader sender, TagReport report)
        {
            // This event handler is called asynchronously 
            // when tag reports are available.

            // Loop through each tag in the report 
            // and print the test output.
            foreach (Tag tag in report)
            {
                if (tag.IsXPCWordsPresent && tag.XPCWords.CountBytes > 0)
                {
                    Console.WriteLine("Reader : {0}, Tag report includes XPC Words", sender.Address);
                }
                else
                {
                    Console.WriteLine("Reader : {0}, Tag report does not include XPC Words", sender.Address);
                }
            }
            Console.WriteLine("Reader Tags Reported\n");
        }
    }
}
