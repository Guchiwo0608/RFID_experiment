package com.example.sdksamples;

import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.RshellEngine;
import com.impinj.octane.RshellReply;

import java.util.Scanner;

public class RshellExample {

    public static void main(String[] args) {
        try {
            // Pass in a reader hostname or IP address as a
            // command line argument when running the example
            String hostname = System.getProperty(SampleProperties.hostname);
            if (hostname == null) {
                throw new Exception("Must specify the '" + SampleProperties.hostname + "' property");
            }

            // Open up an RShell connection on the reader.
            // Specify the reader address, user name, password and connection timeout.
            // (login can take some time to give username and password)
            RshellEngine rshell = new RshellEngine();
            rshell.openSecureSession(hostname, "root", "impinj", 5000);

            // Send an RShell command. An exception is thrown if the command fails.
            String cmd = "show network summary";
            System.out.println("Sending command '" + cmd + "' to " + hostname);
            String reply = rshell.send(cmd);
            System.out.println("RShell command executed successfully.\n");

            // Print out the entire reply.  Note that in the raw format, the information is
            // formatted such as:
            // Status='0,Success'\r\nPrimaryInterface='eth:eth0'\r\n
            // etc.
            System.out.println("RShell command reply :\n\n" + reply + "\n");

            // Parse the output. This allows you to retrieve specific values from the reply,
            // accessing by the key name to get the value.  Refer to the raw reply (printed
            // above) to see the keys that are available for this rshell command.
            RshellReply r = new RshellReply(reply);

            // Check the status
            String status = r.get("StatusString");
            if (status != null) {
                System.out.println("Status string is: " + status);

                if (status.equals("Success")) {
                    String hostname1 = r.get("Hostname");
                    if (hostname1 != null) {
                        System.out.println("Hostname for unit is: " + hostname1);
                    }
                }
            }

            // Close the RShell connection.
            rshell.close();

            // Wait for the user to press enter.
            System.out.println("Press Enter to exit.");
            Scanner s = new Scanner(System.in);
            s.nextLine();
        } catch (OctaneSdkException ex) {
            System.out.println("Octane SDK exception : " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception : " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
    }
}
