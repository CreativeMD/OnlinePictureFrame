package com.creativemd.opf.client;

import com.porpit.lib.GifDecoder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;

@SideOnly(Side.CLIENT)
public class DownloadThread extends Thread {
    public static final File TEMP = new File(System.getProperty("java.io.tmpdir"), "opframe");

    public static HashMap<String, PictureTexture> loadedImages = new HashMap<String, PictureTexture>();

    public static ArrayList<String> loadingImages = new ArrayList<String>();

    private String url;

    private BufferedImage loadedImage;
    private GifDecoder loadedGif;
    private boolean failed;
    private boolean complete;

    public DownloadThread(String url) {
        this.url = url;
        setName("OpF Download Thread " + url);
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
                saveFile.delete();
                run();
                return;
            }
        }
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
                if (readType(is1).equals("gif")) {
                    GifDecoder gif = new GifDecoder();
                    if (gif.read(is2) == GifDecoder.STATUS_OK) {
                        loadedGif = gif;
                    } else {
                        failed = true;
                    }
                } else {
                    loadedImage = ImageIO.read(is2);
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
            }
        } else {
            failed = true;
        }
        complete = true;
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
            if (thread.loadedGif != null) {
                texture = new AnimatedPictureTexture(thread.loadedGif);
            } else if (thread.loadedImage != null) {
                texture = new OrdinaryTexture(thread.loadedImage);
            }
        }
        if (texture != null) {
            loadedImages.put(thread.url, texture);
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

    private static final int BYTES_PER_PIXEL = 4;

    public static int loadTexture(BufferedImage image) {
        long time = System.currentTimeMillis();

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                buffer.put((byte) (pixel & 0xFF));               // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }

        buffer.flip();

        int textureID = GL11.glGenTextures(); //Generate texture ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        //Send texel data to OpenGL
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        System.out.println("Took " + (System.currentTimeMillis() - time) + "ms to upload image");

        //Return the texture ID so we can bind it later again
        return textureID;
    }
}
