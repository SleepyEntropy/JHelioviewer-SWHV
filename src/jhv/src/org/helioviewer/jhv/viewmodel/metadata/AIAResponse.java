package org.helioviewer.jhv.viewmodel.metadata;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.helioviewer.jhv.base.DownloadStream;
import org.helioviewer.jhv.base.FileUtils;
import org.helioviewer.jhv.base.JSONUtils;
import org.json.JSONObject;

class AIAResponse {

    private static final String extPath = "https://github.com/mjpauly/aia/blob/master/";
    private static final String intPath = "/data/";
    private static final String dataFile = "aia_rescaling_data.json";

    private static volatile boolean loaded;
    private static JSONObject responseData;
    private static JSONObject referenceData;
    private static String firstDate;
    private static String lastDate;

    private static URL getExternalURL() throws MalformedURLException {
        return new URL(extPath + dataFile);
    }

    private static URL getInternalURL() throws MalformedURLException {
        return FileUtils.getResourceUrl(intPath + dataFile);
    }

    private static JSONObject getResponseData(URL url) throws IOException {
        return JSONUtils.getJSONStream(new DownloadStream(url).getInput());
    }

    private static void load() throws Exception {
        JSONObject data = getResponseData(getInternalURL());
        String[] keys = JSONObject.getNames(data);
        Arrays.sort(keys);

        firstDate = keys[0];
        lastDate = keys[keys.length - 1];
        responseData = data;
        referenceData = data.getJSONObject("2010-05-01");
    }

    static double get(String date, String pass) {
        try {
            if (!loaded) {
                load();
                loaded = true;
            }

            if (lastDate.compareTo(date) < 0)
                date = lastDate;
            else if (firstDate.compareTo(date) > 0)
                date = firstDate;

            String key = pass + "_filtered";
            if (!referenceData.has(key) || !responseData.getJSONObject(date).has(key)) // exception if date missing
                return 1;

            double factor, ratio = referenceData.getDouble(key) / responseData.getJSONObject(date).getDouble(key);
            if ("171".equals(pass) || "1700".equals(pass))
                factor = Math.sqrt(ratio);
            else
                factor = Math.log10(10 + ratio);
            // System.out.println(">>> " + date + " " + factor);
            return factor;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

}
