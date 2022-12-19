////////////////////////////////////////////////////////////////////////////////
//
//    Advanced GPO
//
////////////////////////////////////////////////////////////////////////////////

using System;
using System.Threading;
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
                if (args.Length != 1)
                {
                    Console.WriteLine("Error: No hostname specified.  Pass in the reader hostname as a command line argument when running the Sdk Example.");
                    return;
                }
                string hostname = args[0];
                reader.Connect(hostname);

                Console.WriteLine("Configuring general purpose outputs.");

                // Get the default settings. We'll use these as a starting point
                // and then modify the settings we're interested in.
                Settings settings = reader.QueryDefaultSettings();
               
                // The settings retrieved from the reader will tell us many things,
                // including the number of GPOs and GPIs supported by the reader.
                // Note: For Speedway Revolution products there are 4 GPOs and 4 GPIs, 
                // whereas for R700 products there are just 3 GPOs and 2 GPIs. 
                int numOfGPOs = settings.Gpos.Length;

                // GPO 1 will go high when tags when tags are read.
                settings.Gpos.GetGpo(1).Mode = GpoMode.ReaderInventoryTagsStatus;

                // GPO 2 will go high when a client application connects to the reader.
                settings.Gpos.GetGpo(2).Mode = GpoMode.LLRPConnectionStatus;

                // GPO 3 will pulse high for the specified period of time.
                settings.Gpos.GetGpo(3).Mode = GpoMode.Pulsed;
                settings.Gpos.GetGpo(3).GpoPulseDurationMsec = 1000;

                // Only set GPO4 if the retrieved settings told us there were 4 GPOs
                // This setting simply sets GPO 4 to behave as a plain vanilla GPO.
                if (numOfGPOs == 4 )
                    settings.Gpos.GetGpo(4).Mode = GpoMode.Normal;

                // Apply the newly modified settings.
                reader.ApplySettings(settings);

                // Start reading.
                reader.Start();

                // Set GPO 3 high, every three seconds.
                // The GPO will remain high for the period 
                // specified by GpoPulseDurationMsec. We will do 
                // this cycle 5 times. 
                for (int i = 0; i < 5; i++)
                {
                    reader.SetGpo(3, true);
                    Thread.Sleep(3000);
                }

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
    }
}
