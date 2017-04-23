package com.creativemd.opf.client;

import com.porpit.lib.GifDecoder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;

@SideOnly(Side.CLIENT)
public class DownloadThread extends Thread {
    public static final File TEMP = new File(System.getProperty("java.io.tmpdir"), "opframe");
    public static final Object LOCK = new Object();
    public static final int MAXIMUM_ACTIVE_DOWNLOADS = 5;

    public static int activeDownloads = 0;

    public static HashMap<String, PictureTexture> loadedImages = new HashMap<String, PictureTexture>();
    public static ArrayList<String> loadingImages = new ArrayList<String>();

    private String url;

    private ProcessedImageData processedImage;
    private boolean failed;
    private boolean complete;

    public DownloadThread(String url) {
        this.url = url;
        synchronized (LOCK) {
            activeDownloads++;
        }
        setName("OPF Download \"" + url + "\"");
        setDaemon(true);
        start();
    }

    public boolean hasFinished() {
        return complete;
    }

    public boolean hasFailed() {
        return hasFinished() && failed;
    }

    @Override
    public void run() {
        File saveFile = getSaveFile(url);
        InputStream loadedStream = null;
        try {
            if (!saveFile.exists()) {
                URLConnection con = new URL(url).openConnection();
                con.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
                loadedStream = con.getInputStream();
            } else {
                loadedStream = new FileInputStream(saveFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (saveFile.exists()) {
                IOUtils.closeQuietly(loadedStream);
                //If we failed to load from saved file, delete it and retry
                saveFile.delete();
                run();
                return;
            }
        }
        boolean deleteSave = false;
        if (loadedStream != null) {
            InputStream is1 = null;
            InputStream is2 = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes = IOUtils.toByteArray(loadedStream);
                baos.write(bytes);
                baos.flush();
                is1 = new ByteArrayInputStream(baos.toByteArray());
                is2 = new ByteArrayInputStream(baos.toByteArray());
                String type = readType(is1);
                if (type.equals("gif")) {
                    GifDecoder gif = new GifDecoder();
                    if (gif.read(is2) == GifDecoder.STATUS_OK) {
                        processedImage = new ProcessedImageData(gif);
                    } else {
                        failed = true;
                        //Delete invalid gifs
                        deleteSave = true;
                    }
                } else {
                    try {
                        BufferedImage image = ImageIO.read(is2);
                        if (image != null) {
                            processedImage = new ProcessedImageData(image);
                        } else {
                            //Delete invalid image
                            deleteSave = true;
                            failed = true;
                        }
                    } catch (IOException e1) {
                        deleteSave = true;
                        e1.printStackTrace();
                    }
                }
                try (FileOutputStream writer = new FileOutputStream(saveFile)) {
                    writer.write(bytes);
                }
            } catch (IOException e) {
                failed = true;
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(is1);
                IOUtils.closeQuietly(is2);
                IOUtils.closeQuietly(loadedStream);
            }
        } else {
            failed = true;
        }
        if (deleteSave) {
            saveFile.delete();
        }
        complete = true;
        synchronized (LOCK) {
            activeDownloads--;
        }
    }

    public static String readType(InputStream input) throws IOException {
        ImageInputStream stream = ImageIO.createImageInputStream(input);
        Iterator iter = ImageIO.getImageReaders(stream);
        if (!iter.hasNext()) {
            return "";
        }
        ImageReader reader = (ImageReader) iter.next();
        ImageReadParam param = reader.getDefaultReadParam();
        reader.setInput(stream, true, true);
        try {
            reader.read(0, param);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.dispose();
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        input.reset();
        return reader.getFormatName();
    }

    public static PictureTexture loadImage(DownloadThread thread) {
        PictureTexture texture = null;
        if (!thread.hasFailed()) {
            if (thread.processedImage.isAnimated()) {
                texture = new AnimatedPictureTexture(thread.processedImage);
            } else {
                texture = new OrdinaryTexture(thread.processedImage);
            }
        }
        if (texture != null) {
            synchronized (LOCK) {
                loadedImages.put(thread.url, texture);
            }
        }
        return texture;
    }

    private static File getSaveFile(String url) {
        if (!TEMP.exists()) {
            TEMP.mkdirs();
        }
        String identifier = new String(Base64.getUrlEncoder().encode(url.getBytes()));
        return new File(TEMP, identifier);
    }
}
