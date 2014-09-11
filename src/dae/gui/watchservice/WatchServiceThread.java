/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.gui.watchservice;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class WatchServiceThread extends Thread {

    private WatchService service;
    private ArrayList<WatchKey> allKeys = new ArrayList<WatchKey>();
    private BiMap<Path, WatchKey> mapKeys = HashBiMap.create();
    private ArrayList<WatchServiceListener> listeners =
            new ArrayList<WatchServiceListener>();

    public WatchServiceThread(WatchService service) {
        this.service = service;
    }

    public void clearWatchService() {
        for (WatchKey key : allKeys) {
            key.cancel();
        }
        allKeys.clear();
        mapKeys.clear();
    }

    @Override
    public void run() {
        for (;;) {

            // wait for key to be signaled
            WatchKey key;
            try {
                key = service.take();
            } catch (InterruptedException x) {
                return;
            } catch( ClosedWatchServiceException ex){
                return;
            }
            Path dir = mapKeys.inverse().get(key);
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == OVERFLOW) {
                    Logger.getLogger("DArtE").log(Level.SEVERE, "Too many entries in the watch service: overflow error");
                    continue;
                }

                // The filename is the
                // context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path file = ev.context();
               
                if (ev.kind() == ENTRY_CREATE) {

                    Path subDir = dir.resolve(file);
                    
                    if (Files.isDirectory(subDir)) {
                        register(subDir);
                        for (WatchServiceListener l : listeners) {
                            l.pathCreated(subDir);
                        }
                    }else{
                        for (WatchServiceListener l : listeners) {
                            l.assetCreated(subDir);
                        }
                    }
                } else if (ev.kind() == ENTRY_DELETE) {
                    Path subDir = dir.resolve(file);
                    WatchKey toCancel = mapKeys.get(subDir);
                    if (toCancel != null) {
                        toCancel.cancel();
                        mapKeys.remove(subDir);

                        for (WatchServiceListener l : listeners) {
                            l.pathDeleted(subDir);
                        }
                    }else{
                        for (WatchServiceListener l : listeners) {
                            l.assetDeleted(subDir);
                        }
                    }
                } else if (ev.kind() == ENTRY_MODIFY) {
                    Path subDir = dir.resolve(file);
                    if (Files.isDirectory(subDir)) {
                        for (WatchServiceListener l : listeners) {
                            l.pathModified(subDir);
                        }
                    } else {
                        for (WatchServiceListener l : listeners) {
                            l.assetModified(subDir);
                        }
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    public void register(Path toWatch) {
        WatchKey key;
        try {
            key = toWatch.register(service, ENTRY_CREATE,ENTRY_DELETE,ENTRY_MODIFY);
            mapKeys.put(toWatch, key);
            allKeys.add(key);
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, "Could not register {0}", toWatch);
            Logger.getLogger("DArtE").log(Level.SEVERE, "Register exception: ", ex);
        }

    }

    public void addWatchServiceListener(WatchServiceListener listener) {
        listeners.add(listener);
    }
}
