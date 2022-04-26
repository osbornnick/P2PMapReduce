package util;


import com.healthmarketscience.rmiio.RemoteIterator;
import com.healthmarketscience.rmiio.SerialRemoteIteratorClient;
import com.healthmarketscience.rmiio.SerialRemoteIteratorServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RemoteFileIterator {
    public static RemoteIterator<String> iterator(Path path) throws IOException {
        if (!Files.isReadable(path)) {
            throw new IOException("can't read file");
        }

        BufferedReader buff = Files.newBufferedReader(path);
        Iterator<String> iter = new Iterator<>() {

            private String cachedLine;

            @Override
            public boolean hasNext() {
                if (cachedLine != null) return true;
                try {
                    cachedLine = buff.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return cachedLine != null;
            }

            @Override
            public String next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                String currentLine = cachedLine;
                cachedLine = null;
                return currentLine;
            }
        };

        SerialRemoteIteratorClient<String> stringClient;
        SerialRemoteIteratorServer<String> stringServer = new SerialRemoteIteratorServer<String>(iter);
        stringClient = new SerialRemoteIteratorClient<>(stringServer);
        return stringClient;
    }
}
