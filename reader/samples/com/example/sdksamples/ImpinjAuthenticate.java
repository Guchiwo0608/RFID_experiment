package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.ArrayList;
import java.util.Scanner;

// Demonstrates Impinj Authenticate feature for Impinj R77x tags
public class ImpinjAuthenticate {
    // Uncomment and change the line below if the AuthenticateOp should only execute against a particular EPC or multiple
    // EPCs matching a pattern
    //static String TARGET_EPC = "F00DC018DCDF";

    public static void main(String[] args) {
        try {
            String hostname = System.getProperty(SampleProperties.hostname);
            String targetAntennaStr = System.getProperty(SampleProperties.targetAntenna);
            short targetAntenna;

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            if (targetAntennaStr == null) {
                System.out.println("No target antenna set. Using antenna 1.");
                targetAntenna = 1;
            } else {
                try {
                    targetAntenna = Short.parseShort(targetAntennaStr);
                } catch (NumberFormatException e) {
                    throw new Exception("The specified target antenna '" + targetAntennaStr +
                            "' could not be converted to a short");
                }
            }

            ImpinjReader reader = new ImpinjReader();

            // Connect
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);

            // At the time of authoring this example, only R700 readers support TagAuthenticate
            FeatureSet featureSet = reader.queryFeatureSet();
            String model = featureSet.getModelName();
            if (!model.contains("R7")) {
                reader.disconnect();
                System.out.println(String.format("Your %s model does not support the ImpinjTagAuthenticate. Expected: an R7xx Impinj reader.", model));
                return;
            }

            // Set up the listener for the tag operation
            reader.setTagOpCompleteListener(
                    new TagOpCompleteListenerImplementation());

            // Apply default settings
            reader.applyDefaultSettings();

            // Create a tag operation sequence (TagOpSequence)
            // You can add multiple read, write, lock, kill, QT and TagAuthenticate operations to this sequence. For this
            // example, we will just be using TagAuthenticate.
            TagOpSequence seq = new TagOpSequence();
            seq.setAntennaId(targetAntenna);
            seq.setOps(new ArrayList<TagOp>());
            seq.setExecutionCount((short) 1);
            seq.setState(SequenceState.Active);
            seq.setId(1);
            seq.setTargetTag(null);

            // Specify a target tag, if desired.
            // This is important when you have a specific challenge message you want to send to a specific tag. In this
            // example, we don't care so leave the lines of code below commented if desired. Otherwise uncomment the lines below.
            //seq.getTargetTag().setMemoryBank(MemoryBank.Epc);
            //seq.getTargetTag().setBitPointer(BitPointers.Epc);
            //seq.getTargetTag().setData(TARGET_EPC);

            // Create a new TagImpinjAuthenticateOp
            TagImpinjAuthenticateOp authOp = new TagImpinjAuthenticateOp();

            // We set IncludeTidInReply to true to have the tag append its TID to the crypto response
            authOp.setIncludeTidInReply(true);

            // Here we create a message -- the content is irrelevant -- to be encrypted. Note that it is EXACTLY 12 Hex
            // characters long. The challenge message must be exactly 48 bits long. The first 6 bits will be overwritten
            // automatically with the correct header
            TagData myChallenge = TagData.fromHexString("A1B1C1D1E1F1");

            // And we MUST assign that message to the Authenticate operation. Failure to do so will throw a null reference
            // exception once the op spec is being added to the reader
            authOp.setChallengeMessage(myChallenge);

            // Now we add the operation to the tag operation sequence
            seq.getOps().add(authOp);

            // Then we add the tag operation sequence to the reader. The reader supports multiple sequences
            reader.addOpSequence(seq);

            // Start the reader
            reader.start();

            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            System.out.println("Stopping  " + hostname);
            reader.stop();

            System.out.println("Disconnecting from " + hostname);
            reader.disconnect();

            System.out.println("Done");
        } catch (OctaneSdkException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
