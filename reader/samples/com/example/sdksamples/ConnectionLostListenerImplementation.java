package com.example.sdksamples;

import com.impinj.octane.ConnectionLostListener;
import com.impinj.octane.ImpinjReader;

public class ConnectionLostListenerImplementation implements
        ConnectionLostListener {

    @Override
    public void onConnectionLost(ImpinjReader reader) {
        System.out.printf("Connection lost : %s (%s) ... Disconnecting%n", reader.getName(), reader.getAddress());
        reader.disconnect();
        System.exit(0);
    }
}
