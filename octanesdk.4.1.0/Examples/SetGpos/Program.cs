////////////////////////////////////////////////////////////////////////////////
//
//    Set GPO
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
            ushort i;

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

                //Retrieve the current set
                Settings settings = reader.QueryDefaultSettings();

                // The settings retrieved from the reader will tell us many things,
                // including the number of GPOs and GPIs supported by the reader.
                // Note: For Speedway Revolution products there are 4 GPOs and 4 GPIs, 
                // whereas for R700 products there are just 3 GPOs and 2 GPIs. 
                int numOfGPOs = settings.Gpos.Length;

                // Configure the reader with the default settings. We are doing this as a quick way to 
                // set all of the settings -- despite what may have been done to the reader previously --
                // to their default values. This gives us a known starting point. 
                reader.ApplyDefaultSettings();

                // Turn the general purpose outputs 
                // (GPOs) on one at a a time
                Console.WriteLine("Setting general purpose outputs...");
                for (i = 1; i <= numOfGPOs; i++)
                {
                    reader.SetGpo(i, true);
                    Thread.Sleep(1500);
                    reader.SetGpo(i, false);
                }

                // ImpinReader.ApplySettings will reset to factory settings before applying
                // the provided settings, which, among other things, will drive all GPOs to low.
                // Alternatively, the user can specify to NOT reset to factory settings.
                settings = reader.QueryDefaultSettings();
                // Set the GPO high (true).
                reader.SetGpo(1, true);
                // Apply settings, with a factory reset.
                reader.ApplySettings(settings);
                // If we had a way to retrieve the current state of GPO 1, it would now be low (false).
                // Set the GPO high (true) again.
                reader.SetGpo(1, true);
                // Apply settings, without a factory reset.
                reader.ApplySettingsWithoutFactoryReset(settings);
                // If we had a way to retrieve the current state of GPO 1, it should still be high (true).

                // Wait for the user to press enter.
                Console.WriteLine("Press enter to exit.");
                Console.ReadLine();

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
