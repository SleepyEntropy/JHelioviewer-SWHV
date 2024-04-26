package org.helioviewer.jhv.gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.TransferHandler;

import org.helioviewer.jhv.Log;
import org.helioviewer.jhv.base.Regex;
import org.helioviewer.jhv.io.FileUtils;
import org.helioviewer.jhv.io.Load;

@SuppressWarnings("serial")
class DropHandler extends TransferHandler {

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        Transferable transferable = support.getTransferable();
        return support.isDrop() && (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor) ||
                transferable.isDataFlavorSupported(DataFlavor.stringFlavor));
    }

    private static void classifyUri(URI uri, List<URI> imageUris, List<URI> jsonUris, List<URI> cdfUris) {
        String loc = uri.toString().toLowerCase(Locale.ENGLISH);
        if (loc.endsWith(".json"))
            jsonUris.add(uri);
        else if (loc.endsWith(".cdf"))
            cdfUris.add(uri);
        else
            imageUris.add(uri);
    }

    private static void classifyFile(File file, List<URI> imageUris, List<URI> jsonUris, List<URI> cdfUris) {
        if (file.isFile() && file.canRead()) {
            classifyUri(file.toURI(), imageUris, jsonUris, cdfUris);
        } else if (file.isDirectory()) {
            try {
                FileUtils.listDir(file.toPath()).forEach(uri -> classifyUri(uri, imageUris, jsonUris, cdfUris));
            } catch (Exception e) {
                Log.warn("Error reading directory: " + file, e);
            }
        }
    }


    @Override
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support))
            return false;

        try {
            Transferable transferable = support.getTransferable();
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<?> objects = (List<?>) transferable.getTransferData(DataFlavor.javaFileListFlavor);

                List<URI> imageUris = new ArrayList<>(objects.size());
                List<URI> jsonUris = new ArrayList<>(objects.size());
                List<URI> cdfUris = new ArrayList<>(objects.size());
                for (Object o : objects) {
                    if (o instanceof File file) {
                        classifyFile(file, imageUris, jsonUris, cdfUris);
                    }
                }

                // jsonUris.forEach(Load.request::get);
                Load.SunJSON.getAll(jsonUris);
                Load.CDF.getAll(cdfUris);
                Load.Image.getAll(imageUris);
                return true;
            } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String loc = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                String[] words = Regex.MultiCommaSpace.split(loc);

                List<URI> imageUris = new ArrayList<>(words.length);
                List<URI> jsonUris = new ArrayList<>(words.length);
                List<URI> cdfUris = new ArrayList<>(words.length);
                for (String word : words) {
                    try {
                        URI uri = new URI(word); // attempt to check if it's an URI
                        if (uri.getScheme() == null) { // maybe on filesystem
                            classifyFile(new File(word), imageUris, jsonUris, cdfUris);
                        } else
                            classifyUri(uri, imageUris, jsonUris, cdfUris);
                    } catch (Exception e) {
                        Log.warn("Not found: " + word, e);
                    }
                }

                Load.SunJSON.getAll(jsonUris);
                Load.CDF.getAll(cdfUris);
                Load.Image.getAll(imageUris);
                return true;
            }
        } catch (Exception e) {
            Log.warn("Import error", e);
        }

        return false;
    }

}
