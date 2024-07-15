package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.Scanner;


public class OptimizedRead {

    public static void main(String[] args) {

        try {
            // Pass in a reader hostname or IP address as a
            // command line argument when running the example
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();

            // Connect to the reader.
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);

            // Get the default settings
            // We'll use these as a starting point and then modify the settings we're
            // interested in.
            Settings settings = reader.queryDefaultSettings();

            // Tell the reader to include the antenna number in all tag reports. Other fields can be added
            // to the reports in the same way by calling the appropriate ReportConfig.setIncludeXXXXXXX() method.
            settings.getReport().setIncludeAntennaPortNumber(true);

            // Create a tag read operation for User memory.
            // "Read two words from the start of user memory on all tags."
            TagReadOp readUser = new TagReadOp();
            // Read from user memory
            readUser.setMemoryBank(MemoryBank.User);
            // Read two (16-bit) words
            readUser.setWordCount((short) 2);
            // Starting at word 0
            readUser.setWordPointer((short) 0);
            // Use the default operation id, or set it to you preference.
            readUser.Id = 222;

            // Create a tag read operation for TID memory.
            // "Read the non-serialzed part of the TID (first 2 words)"
            TagReadOp readTid = new TagReadOp();
            // Read from TID memory
            readTid.setMemoryBank(MemoryBank.Tid);
            // Read two (16-bit) words
            readTid.setWordCount((short) 2);
            // Starting at word 0
            readTid.setWordPointer((short) 0);
            // Use the default operation id, or set it to you preference.
            readTid.Id = 333;

            // Add these operations to the reader as Optimized Read ops.
            // Optimized Read ops apply to all tags, unlike Tag Operation Sequences, which
            // can be applied to specific tags.
            // Speedway Revolution supports up to two Optimized Read operations.
            settings.getReport().getOptimizedReadOps().add(readUser);
            settings.getReport().getOptimizedReadOps().add(readTid);

            // Log the operation ids, so can identify the tag op complete information in log.
            System.out.println("User operation id = " + readUser.Id);
            System.out.println("Tid operation id = " + readTid.Id);

            // Connect a tag report listener. This listener will detect when tag reports are available.
            reader.setTagReportListener(new TagReportListenerImplementation());

            // Set the TagOpComplete listener.
            // The listener will be notified when tag operations complete.
            reader.setTagOpCompleteListener(
                    new TagOpCompleteListenerImplementation());

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Start reading.
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
