package org.teamscavengr.scavengr.mocklocation;

import android.content.Context;
import android.os.FileObserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Provides location from a file. That way you can write to the file over ADB and the location will
 * update when the update() method is called.
 *
 * The file should be one line which has two float literals separated by a space (lat and long).
 *
 * Created by zrneely on 4/5/15.
 */
public class FileMockLocationProvider extends MockLocationProvider {

    private final File file;
    private FileObserver fo;

    /**
     * @param providerName The name of this provider (should be unique among mlps)
     * @param ctx A context
     * @param file The file to read
     * @param autoUpdate If true, the location will update every time that file is touched.
     */
    public FileMockLocationProvider(final String providerName, final Context ctx,
                                       final File file, final boolean autoUpdate) {
        super(providerName, ctx);
        this.file = file;

        if(autoUpdate) {
            fo = new FileObserver(file.getPath(),
                    FileObserver.ATTRIB | FileObserver.MODIFY | FileObserver.CLOSE_WRITE) {
                @Override
                public void onEvent(final int event, final String path) {
                    update();
                }
            };
            fo.startWatching();
        }
    }

    @Override
    public void close() {
        fo.stopWatching();
        super.close();
    }

    @Override
    public void update() {
        // Read from the file
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();

            // Parse to get a location
            String[] parts = sb.toString().trim().split(" ");
            double lat = Double.parseDouble(parts[0]);
            double lon = Double.parseDouble(parts[1]);
            update(lat, lon);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
