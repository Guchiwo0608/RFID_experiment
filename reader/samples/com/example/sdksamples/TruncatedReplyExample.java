package com.example.sdksamples;

import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;
import com.impinj.octane.TruncatedReplySettings;

import java.util.Scanner;


public class TruncatedReplyExample {
    public static void main(String[] args) {
        try {
            String hostname = "impinj-1d-00-28.i4j.io";

            if (hostname == null) {
                throw new Exception("Must specify the '"
                        + SampleProperties.hostname + "' property");
            }

            ImpinjReader reader = new ImpinjReader();

            // Connect
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);
            
            // Get the default settings
            Settings settings = reader.queryDefaultSettings();

            // Long comment TLDR: Be very careful with truncated replies
            // Be careful using truncated replies because there are certain
            // conditions assumed to exist in the tag population in order
            // for truncated reply to work well.
            //
            // These conditions are:
            // 1) all EPCs must be the same length. Tags with different lengths will
            //    still reply BUT they will NOT be decoded successfully
            // 2) all EPCs must have the same bit pattern you're using for the tag mask
            //    in order for the tags to respond
            // 3) if setGen2v2TagsOnly is true then ONLY Gen2V2 tags will respond
            // 4) if setTagMask is set (which it most likely always will be) then you
            //    CANNOT use the standard C1G2 filters in addition to a Truncate tagMask
            // In summary, truncate reply is designed to work with a uniform tag population
            //    where the only thing varying is the serialized portion of the EPC
            TruncatedReplySettings trs = settings.getTruncatedReply();

            trs.setIsEnabled(true);             // <- We're wanting tags to truncate their replies
            trs.setGen2v2TagsOnly(false);       // <- We're assuming NO Gen2v2 tags
            trs.setEpcLengthInWords((byte) 6);  // <- We're assuming only 96 (6 words*16 bits/word) bit EPCs
            trs.setBitPointer((short) 32);      // <- We're starting the mask at the beginning of the EPC memory
            trs.setTagMask("300833B2DDD90140"); // <- Example prefix, change as needed. Tags starting with this
                                                //    sequence will backscatter everything after this 64 bit
                                                //    (16 nibbles*4 bits/nibble) prefix. Therefore, a matching tag
                                                //    responds with just 96-64 = 32 bits (the back end) of the EPC.


            // Apply the new settings
            reader.applySettings(settings);

            // connect a listener
            reader.setTagReportListener(new TagReportListenerImplementation());

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