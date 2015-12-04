package it.oltrenuovefrontiere.tds.filer;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import it.oltrenuovefrontiere.tds.model.DbAdapter;

/**
 * Created by Utente on 01/12/2015.
 */
public class FileEnumerator {
    private String list;
    private String path;

    protected void FileEnumerator() {

    }

    public static void listToDB(DbAdapter adapter) {
        String type[] = { "Anello", "Biglierina", "Bracciale", "Ciondolo", "Collana", "Orecchini", "Orologio" };
        String initial_path = Environment.getExternalStorageDirectory().toString() + "/Documents/Schede Tecniche Bulgari/";
        for (String t : type) {
            String path = initial_path + t;

            List<File> allFiles = new ArrayList<File>();
            Queue<File> dirs = new LinkedList<File>();
            dirs.add(new File(path));
            while (!dirs.isEmpty()) {
                for (File f : dirs.poll().listFiles()) {
                    if (f.isDirectory()) {
                        dirs.add(f);
                    } else if (f.isFile()) {
                        if (f.getName().endsWith("pdf")) {
                            String parent = f.getParent().toString();
                            String linea = parent.substring(parent.lastIndexOf("/") + 1, parent.length());
                            adapter.createTechnical(f.getName().toString(), f.getAbsolutePath().toString(), t, linea);
                        }
                    }
                }
        }
        }


    }
}

