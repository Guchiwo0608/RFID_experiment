package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.ArrayList;
import java.util.Scanner;

public class WriteUserMemory {

    public static void main(String[] args) {
        try {
            // Pass in a reader hostname or IP address as a command line argument when running the example.
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }
            ImpinjReader reader = new ImpinjReader();

            // Connect to the reader.
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);

            // Set up listeners to hear stuff back from SDK. Normally the
            // application would enable this but we don't want too many
            // reports in our example.
            // reader.setTagReportListener(new TagReportListenerImplementation());

            // Assign the TagOpComplete event listener.
            // This specifies which object to inform when tag ops are completed.
            reader.setTagOpCompleteListener(new TagOpCompleteListenerImplementation());

            // Configure the reader with the default settings.
            reader.applyDefaultSettings();

            // Create a tag operation sequence.
            // You can add multiple read, write, lock, kill and QT operations to this sequence.
            TagOpSequence seq = new TagOpSequence();
            seq.setOps(new ArrayList<>());
            seq.setExecutionCount((short) 0); // forever
            seq.setState(SequenceState.Active);
            seq.setId(1);

            // Use target tag to only apply to some EPCs
            String targetEpc = System.getProperty(SampleProperties.targetTag);
            if (targetEpc != null) {
                seq.setTargetTag(new TargetTag());
                seq.getTargetTag().setBitPointer(BitPointers.Epc);
                seq.getTargetTag().setMemoryBank(MemoryBank.Epc);
                seq.getTargetTag().setData(targetEpc);
            } else {
                // or just send NULL to apply to all tags
                seq.setTargetTag(null);
            }

            // If you are using Monza 4, Monza 5 or Monza X tag chips, uncomment these two lines.
            // This enables 32-bit block writes which significantly improves write performance.
            //seq.setBlockWriteEnabled(true);
            //seq.setBlockWriteWordCount((short) 2);

            // Create a tag write operation.
            TagWriteOp writeOp = new TagWriteOp();
            // Write to user memory
            writeOp.setMemoryBank(MemoryBank.User);
            // Write two (16-bit) words
            writeOp.setData(TagData.fromHexString("ABBAD00D"));
            // Starting at word 0
            writeOp.setWordPointer((short) 0);

            // Add this tag write op to the tag operation sequence.
            seq.getOps().add(writeOp);

            // Add the tag operation sequence to the reader.
            // The reader supports multiple sequences.
            reader.addOpSequence(seq);

            // Start the reader
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            System.out.println("Stopping  " + hostname);
            reader.stop();

            // Disconnect from the reader.
            System.out.println("Disconnecting from " + hostname);
            reader.disconnect();

            System.out.println("Done");
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
