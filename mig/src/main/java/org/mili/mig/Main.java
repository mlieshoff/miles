package org.mili.mig;

import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.newdawn.easyogg.OggClip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Michael Lieshoff, 18.05.15
 */
public class Main {

    private volatile boolean running = true;

    private String _filter;
    private String _fileFilter;

    public static void main(String[] args) throws IOException {
        new Main().start(args);
    }

    private void start(String[] args) {
        _filter = getArgs(args, 1);
        _fileFilter = getArgs(args, 2);
        index(new File(getArgs(args, 0)));
        while(true) {
        }
    }

    private void index(final File dir) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("filter: " + _filter);
                while(running) {
                    Collection<File> entries = FileUtils.listFiles(dir, new String[]{"zip"}, true);
                    for (File file : entries) {
                        if (file.isFile() && file.getName().endsWith(".zip")) {
                            if (StringUtils.isBlank(_filter) || file.getName().toLowerCase().contains(_filter.toLowerCase())) {
                                System.out.println("play " + file.getName() + " ...");
                                try {
                                    ZipFile zipFile = new ZipFile(file);
                                    for (Enumeration<? extends ZipEntry> enumeration = zipFile.entries(); enumeration.hasMoreElements(); ) {
                                        ZipEntry zipEntry = enumeration.nextElement();
                                        System.out.println(zipEntry.getName());
                                        boolean ok = true;
                                        if (StringUtils.isNotBlank(_fileFilter)) {
                                            ok = zipEntry.getName().contains(_fileFilter);
                                        }
                                        if (ok) {
                                            try {
                                                if (zipEntry.getName().endsWith(".mp3")) {
                                                    System.out.println(zipFile.getName() + " --> " + zipEntry.getName());
                                                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                                                    AdvancedPlayer player = new AdvancedPlayer(
                                                            inputStream,
                                                            FactoryRegistry.systemRegistry().createAudioDevice()
                                                    );
                                                    player.play();
                                                } else if (zipEntry.getName().endsWith(".ogg")) {
                                                    System.out.println(zipFile.getName() + " --> " + zipEntry.getName());
                                                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                                                    OggClip oggClip = new OggClip(inputStream);
                                                    oggClip.play();
                                                    while (!oggClip.stopped()) {
                                                        // wait
                                                    }
                                                }
                                            } catch (Exception e) {
                                                System.out.println("< catch something: " + e.getMessage());
                                            }
                                        }
                                    }
                                    Thread.sleep(5000);
                                } catch (Exception e) {
                                    System.out.println("< catch something: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private static String getArgs(String[] args, int i) {
        String s = "";
        if (args != null && args.length >= i + 1) {
            return args[i];
        }
        return s;
    }

}
