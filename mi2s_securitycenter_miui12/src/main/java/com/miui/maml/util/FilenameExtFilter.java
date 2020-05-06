package com.miui.maml.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;

public class FilenameExtFilter implements FilenameFilter {
    private HashSet<String> mExts = new HashSet<>();

    public FilenameExtFilter(String[] strArr) {
        if (strArr != null) {
            this.mExts.addAll(Arrays.asList(strArr));
        }
    }

    public boolean accept(File file, String str) {
        if (new File(file + File.separator + str).isDirectory()) {
            return true;
        }
        int lastIndexOf = str.lastIndexOf(46);
        if (lastIndexOf != -1) {
            return contains(((String) str.subSequence(lastIndexOf + 1, str.length())).toLowerCase());
        }
        return false;
    }

    public boolean contains(String str) {
        return this.mExts.contains(str.toLowerCase());
    }
}
