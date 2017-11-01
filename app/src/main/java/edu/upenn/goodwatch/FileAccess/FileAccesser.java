package edu.upenn.goodwatch.FileAccess;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by raph on 31/10/17.
 */

public class FileAccesser {

    private static Map<String, Map<String, String>> savedProps = new HashMap<>();

    protected static String getProperty(Context context, String property, String fileName) {
        if (savedProps.get(fileName) == null) {
            savedProps.put(fileName, new HashMap<String, String>());
        }

        Map<String, String> fileProps = savedProps.get(fileName);
        String value = fileProps.get(property);
        if (value != null) {
            return value;
        }

        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = context.getAssets().open(fileName);
            prop.load(input);

            value = prop.getProperty(property);
            fileProps.put(property, value);
            return value;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
