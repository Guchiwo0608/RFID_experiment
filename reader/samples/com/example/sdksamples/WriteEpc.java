package com.example.sdksamples;

import com.impinj.octane.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class WriteEpc implements TagReportListener, TagOpCompleteListener {

    static short EPC_OP_ID = 123;
    static short PC_BITS_OP_ID = 321;

    static int opSpecID = 1;

    static Random random = new Random();

    private ImpinjReader reader;

    // Create a random EPC from 1 to 6 words in length.
    static String getRandomEpc() {
        StringBuilder epc = new StringBuilder();

        // get the length of the EPC from 1 to 6 words
        int numWords = random.nextInt(6) + 1;

        for (int i = 0; i < numWords; i++) {
            Short s = (short) random.nextInt(Short.MAX_VALUE + 1);
            epc.append(String.format("%04X", s));
        }
        return epc.toString();
    }

    void programEpc(String currentEpc, short currentPcBits, String newEpc) throws Exception {

        // Check that the specified EPCs are a valid length
        if ((currentEpc.length() % 4 != 0) || (newEpc.length() % 4 != 0)) {
            throw new Exception("EPCs must be a multiple of 16 bits (4 hex chars): " + currentEpc + "  " + newEpc);
        }

        System.out.println("Adding a write operation to change the EPC from :");
        System.out.println(currentEpc + " to " + newEpc);

        // Create a tag operation sequence.
        // You can add multiple read, write, lock, kill and QT operations to this sequence.
        TagOpSequence seq = new TagOpSequence();
        seq.setOps(new ArrayList<>());

        seq.setExecutionCount((short) 1); // delete after one time
        seq.setState(SequenceState.Active);
        seq.setId(opSpecID++);

        // Specify a target tag based on the EPC.
        seq.setTargetTag(new TargetTag());
        seq.getTargetTag().setMemoryBank(MemoryBank.Epc);
        seq.getTargetTag().setBitPointer(BitPointers.Epc);
        seq.getTargetTag().setData(currentEpc);

        // If you are using Monza 4, Monza 5 or Monza X tag chips, uncomment these two lines.
        // This enables 32-bit block writes which significantly improves write performance.
        //seq.setBlockWriteEnabled(true);
        //seq.setBlockWriteWordCount((short) 2);

        // Create a tag write operation to change the EPC.
        TagWriteOp writeEpc = new TagWriteOp();
        // Set an ID so we can tell when this operation has executed.
        writeEpc.Id = EPC_OP_ID;
        // Write to EPC memory
        writeEpc.setMemoryBank(MemoryBank.Epc);
        // Specify the new EPC data
        writeEpc.setData(TagData.fromHexString(newEpc));
        // Starting writing at word 2 (word 0 = CRC, word 1 = PC bits)
        writeEpc.setWordPointer(WordPointers.Epc);

        // Add this tag write op to the tag operation sequence.
        seq.getOps().add(writeEpc);

        // Is the new EPC a different length than the current EPC?
        if (currentEpc.length() != newEpc.length()) {
            // We need adjust the PC bits and write them back to the
            // tag because the length of the EPC has changed.

            // Adjust the PC bits (4 hex characters per word).
            short newEpcLenWords = (short)(newEpc.length() / 4);
            short newPcBits = PcBits.AdjustPcBits(currentPcBits, newEpcLenWords);

            System.out.println("Adding a write operation to change the PC bits from :");
            System.out.println(String.format("%04X", currentPcBits) + " to "
                    + String.format("%04X", newPcBits));

            TagWriteOp pcWrite = new TagWriteOp();
            pcWrite.Id = PC_BITS_OP_ID;
            // The PC bits are in the EPC memory bank.
            pcWrite.setMemoryBank(MemoryBank.Epc);
            // Specify the data to write (the modified PC bits).
            pcWrite.setData(TagData.fromWord(newPcBits));
            // Start writing at the start of the PC bits.
            pcWrite.setWordPointer(WordPointers.PcBits);

            // Add this tag write op to the tag operation sequence.
            seq.getOps().add(pcWrite);
        }

        // Add the tag operation sequence to the reader.
        // The reader supports multiple sequences.
        reader.addOpSequence(seq);
    }

    public static void main(String[] args) {
        WriteEpc epcWriter = new WriteEpc();
        epcWriter.run();
    }

    void run() {
        try {
            // Pass in a reader hostname or IP address as a command line argument when running the example.
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }
            reader = new ImpinjReader();

            // Connect to the reader.
            System.out.println("Connecting to " + hostname);
            reader.connect(hostname);

            // Get the default settings.
            // We'll use these as a starting point and then modify the settings we're interested in.
            Settings settings = reader.queryDefaultSettings();

            // Enable antenna #1. Disable all others.
            settings.getAntennas().disableAll();
            settings.getAntennas().getAntenna((short) 1).setEnabled(true);

            // Tell the reader to include the Protocol Control bits in all tag reports.
            // We will need to modify the PC bits if we change the length of the EPC.
            settings.getReport().setIncludePcBits(true);

            // Some more available options that you could choose to configure...
            // Set session one so we see the tag only once every few seconds.
            //settings.getReport().setIncludeAntennaPortNumber(true);
            //settings.setRfMode(1002);
            //settings.setSearchMode(SearchMode.SingleTarget);
            //settings.setSession(1);

            // Set periodic mode so we reset the tag and it shows up with its new EPC.
            //settings.getAutoStart().setMode(AutoStartMode.Periodic);
            //settings.getAutoStart().setPeriodInMs(2000);
            //settings.getAutoStop().setMode(AutoStopMode.Duration);
            //settings.getAutoStop().setDurationInMs(1000);

            // Apply the newly modified settings.
            reader.applySettings(settings);

            // Assign the TagsReported event listener.
            // This specifies which object to inform when tags reports are available.
            reader.setTagReportListener(this);

            // Assign the TagOpComplete event listener.
            // This specifies which object to inform when tag ops are completed.
            reader.setTagOpCompleteListener(this);

            // Start the reader
            reader.start();

            // Wait for the user to press enter.
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();

            // Stop reading.
            System.out.println("Stopping " + hostname);
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

    public void onTagReported(ImpinjReader reader, TagReport report) {
        List<Tag> tags = report.getTags();

        // We've read the tag we want to write to, so we're not interested in tag reports any more.
        // Remove the listener.
        reader.removeTagReportListener();

        if (tags.size() >= 1) {
            // Change the EPC of the first tag we read to a random value.
            Tag tag = tags.get(0);
            try {
                programEpc(tag.getEpc().toHexString(), tag.getPcBits(), getRandomEpc());
            } catch (Exception ex) {
                System.out.println("Failed to program EPC: " + ex.getMessage());
            }
        }
    }

    public void onTagOpComplete(ImpinjReader reader, TagOpReport results) {
        // Loop through all the completed tag operations.
        for (TagOpResult tagOpResult : results.getResults()) {

            // Was this completed operation a tag write operation?
            if (tagOpResult instanceof TagWriteOpResult) {

                // Cast it to the correct type.
                TagWriteOpResult writeResult = (TagWriteOpResult) tagOpResult;

                if (writeResult.getOpId() == EPC_OP_ID) {
                    System.out.println("Write to EPC complete : " + writeResult.getResult());
                } else if (writeResult.getOpId() == PC_BITS_OP_ID) {
                    System.out.println("Write to PC bits complete : " + writeResult.getResult());
                }
                System.out.println("Number of words written : " + writeResult.getNumWordsWritten());
            }
        }
    }
}
