package hs_kempten.ibrush.filters;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Antoine on 29.10.2016.
 */

public class ExtFilter implements FilenameFilter {

    private final String ext;

    public ExtFilter(String ext) {
        this.ext = ext;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(ext);
    }
}
