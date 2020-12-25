package io.moresushant48.callrec.Helpers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.moresushant48.callrec.CallBr;
import io.moresushant48.callrec.MainActivity;

public class GetRecordings {

    public static ArrayList<HashMap<String, String>> getRecordings() {

        ArrayList<HashMap<String, String>> fileList = new ArrayList<>();

        try {

            File[] files = MainActivity.rootFolder.listFiles();

            for (File file :
                    files) {
                if (!file.isDirectory()) {
                    if (file.getName().endsWith(CallBr.AUDIO_EXTENSION)) {
                        HashMap<String, String> song = new HashMap<>();
                        song.put("song_name", file.getName());
                        song.put("song_path", file.getAbsolutePath());
                        fileList.add(song);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList; // RETURN LIST POPULATED WITH RECORDINGS.
    }
}
