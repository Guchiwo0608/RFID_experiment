////////////////////////////////////////////////////////////////////////////////
//
//       - Impinj Tag Authentication OctaneSDK .NET Example -
// 
//  This simple example shows the developer how to execute an authentication 
//  operation against an Impinj tag supporting the Authenticate command. For
//  this example the developer will need 1 or more Impinj M77x tags. 
//  Furthermore, to verify the tag's response with a trusted source, the 
//  developer will need to have access to the appropriate Impinj or 
//  Impinj partner Crypto API Service (CAS).
////////////////////////////////////////////////////////////////////////////////

using System;
using Impinj.OctaneSdk;
using System.Threading; 

namespace OctaneSdkExamples
{
    class Program
    {
        /* Create an instance of the ImpinjReader class.*/
        static ImpinjReader reader = new ImpinjReader();

        /* Uncomment and change the // line below if the AuthenticateOp should 
            *  only execute against a particular EPC or mutiple EPCs matching a pattern
            */
        // const string TARGET_EPC = "F00DC018DCDF";

        // We need to control access to the console so use simple lock
        static object lockOb = new object();

        static void Main(string[] args)
        {
            try
            {
                /* Connect to the reader.
                    * Pass in a reader hostname or IP address as a 
                    * command line argument when running the example
                    */
                if (args.Length != 1)
                {
                    Console.WriteLine("Error: No hostname specified.  Pass in the reader hostname as a command line argument when running the Sdk Example.");
                    return;
                }
                string hostname = args[0];
                reader.Connect(hostname);

                /* At the time of authoring this example only R700 readers support TagAuthenticate */
                var featureSet = reader.QueryFeatureSet();
                var model = featureSet.ModelName;
                if ( ! model.Contains("R7"))
                {
                    reader.Disconnect();
                    Console.WriteLine($"Apologies; your {model} does not support ImpinjTagAuthenticate. Expected: an R7xx Impinj reader.");
                    return;
                }

                /* Assign the TagOpComplete event handler.
                    * This specifies the method to call when a tag operation completes
                    */
                reader.TagOpComplete += OnTagOpComplete;

                /* Assign the TagsReported event handler.
                    * This specifies the method to call when a normal tag read occurs
                    */
                reader.TagsReported += OnTagReported;

                /* Configure the reader with the default settings. */
                reader.ApplyDefaultSettings();

                /* Create a tag operation sequence (TagOpSequence).
                    * You can add multiple read, write, lock, kill, QT and 
                    * TagAuthenticate operations to this sequence. This is a 
                    * quick sample so we're only going to do TagAuthenticate.
                    */
                TagOpSequence seq = new TagOpSequence();
                seq.TargetTag.Data = null;

                /* Specify a target tag if desired
                    * This is important when you have specific challenge message you want to 
                    * send to a specific tag. In this example, we don't care so leave the 
                    * lines of code below commented if desired. Otherwise uncomment the // lines.
                    * The target tag is selected by EPC.
                    */
                // seq.TargetTag.MemoryBank = MemoryBank.Epc;
                // seq.TargetTag.BitPointer = BitPointers.Epc;
                /* The EPC of the target tag. */
                // seq.TargetTag.Data = TARGET_EPC;


                /* Create a new TagImpinjAuthenticateOp */
                TagImpinjAuthenticateOp auth = new TagImpinjAuthenticateOp();
                /* We set IncludeTidInReply to true to have the tag append its
                    * TID to the crypto response.
                    */
                auth.IncludeTidInReply = true;

                /* Here we create a message -- the content is irrelevant -- to be encrypted
                    * Note that it is EXACTLY 12 Hex characters long. The challenge 
                    * message must be exactly 48 bits long. The first 6 bits will be 
                    * overwritten automatically with the correct header
                    */
                TagData myChallenge = TagData.FromHexString("A1B1C1D1E1F1");
                    
                /* And we MUST assign that message to the Authenticate operation. 
                    * Failure to do so will throw a null exception once the opspec 
                    * is being added to the reader!!
                    */
                auth.ChallengeMessage = myChallenge;

                /* Now we add the operation to the tag operation sequence. */
                seq.Ops.Add(auth);

                /* Then we add the tag operation sequence to the reader.
                    * Note: the reader supports multiple sequences.
                    */
                reader.AddOpSequence(seq);

                /* Start the reader */
                reader.Start();

                /* Wait for the user to press enter. */
                Console.WriteLine("Press enter to exit.");
                Console.ReadLine();

                /* Stop reading */
                reader.Stop();

                /* Disconnect from the reader. We're all done! */
                reader.Disconnect();
            }
            catch (OctaneSdkException e)
            {
                /* Handle Octane SDK errors. */
                Console.WriteLine("Octane SDK exception: {0}", e.StackTrace);
            }
            catch (Exception e)
            {
                /* Handle other .NET errors. */
                Console.WriteLine("Exception : {0}", e.StackTrace);
            }
        }

        /* Simply print out regular tag reads */
        static void OnTagReported(ImpinjReader reader, TagReport report)
        {
            lock (lockOb)
            {
                Console.WriteLine(report.Tags[0].Epc);
            }
        }

        /* This event handler is called when tag operations, 
            * either success or fail, have been attempted by the reader 
            */
        static void OnTagOpComplete(ImpinjReader reader, TagOpReport report)
        {
            /* Loop through all the completed tag operations, but we only care in this
                * example about Authenticate operations
                */
            foreach (TagOpResult result in report)
            {
                /* Is this the result of a TagImpinjAuthenticateOp? */
                if (result is TagImpinjAuthenticateOpResult)
                {
                    lock (lockOb)
                    {
                        /* Print the results */
                        TagImpinjAuthenticateOpResult tagAuthResult = result as TagImpinjAuthenticateOpResult;
                        Console.WriteLine($"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        Console.WriteLine($"                    OpId => {tagAuthResult.OpId}");
                        Console.WriteLine($"                    Epc  => {tagAuthResult.Tag.Epc}");
                        Console.WriteLine($"                  Result => {tagAuthResult.Result}");
                        Console.WriteLine($"          *---------------------------------------------*");
                        Console.WriteLine($"          * -----   RESULTS BELOW ONLY ON SUCCESS  -----*");
                        Console.WriteLine($"          *---------------------------------------------*");
                        Console.WriteLine($"                    Tag Tid => {tagAuthResult.ResponseTid}");
                        Console.WriteLine($" Original Challenge Message => {tagAuthResult.ChallengeMessage.ToHexWordString()}");
                        Console.WriteLine($"   Result of Tag Encryption => {tagAuthResult.ResponseCypherMessage}");
                        Console.WriteLine($"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                }
            }
        }
    }
}
