package com.creativemd.opf.client.cache;

import com.creativemd.opf.client.DownloadThread;
import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    private File cacheDirectory = new File(Minecraft.getMinecraft().mcDataDir, "opframe_cache");
    private File index = new File(cacheDirectory, "index");

    private Map<String, CacheEntry> entries = new HashMap<>();

    public TextureCache() {
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
        loadIndex();
    }

    public void save(String url, String etag, long time, long expireTime, byte[] data) {
        CacheEntry entry = new CacheEntry(url, etag, time, expireTime);
        boolean saved = false;
        try (OutputStream out = new FileOutputStream(entry.getFile())) {
            out.write(data);
            saved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (saved) {
            entries.put(url, entry);
            saveIndex();
        }
    }

    public byte[] load(String url) {
        CacheEntry entry = entries.get(url);
        if (entry != null) {
            try (InputStream in = new FileInputStream(entry.getFile())) {
                return IOUtils.toByteArray(in);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public CacheEntry getEntry(String url) {
        return entries.get(url);
    }

    private void loadIndex() {
        if (index.exists()) {
            Map<String, CacheEntry> previousEntries = entries;
            entries = new HashMap<>();
            try (DataInputStream in = new DataInputStream(new FileInputStream(index))) {
                int length = in.readUnsignedShort();
                for (int i = 0; i < length; i++) {
                    StringBuilder url = new StringBuilder();
                    int urlLength = in.readUnsignedByte();
                    for (int c = 0; c < urlLength; c++) {
                        url.append(in.readChar());
                    }
                    StringBuilder etag = new StringBuilder();
                    int etagLength = in.readUnsignedByte();
                    for (int c = 0; c < etagLength; c++) {
                        etag.append(in.readChar());
                    }
                    long time = in.readLong();
                    long expireTime = in.readLong();
                    CacheEntry entry = new CacheEntry(url.toString(), etag.length() > 0 ? etag.toString() : null, time, expireTime);
                    entries.put(entry.getUrl(), entry);
                }
            } catch (IOException e) {
                e.printStackTrace();
                entries = previousEntries;
            }
        }
    }

    private void saveIndex() {
        if (!index.exists()) {
            try {
                index.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(index))) {
            out.writeShort(entries.size());
            for (Map.Entry<String, CacheEntry> mapEntry : entries.entrySet()) {
                CacheEntry entry = mapEntry.getValue();
                String url = entry.getUrl();
                out.writeByte(url.length());
                for (char c : url.toCharArray()) {
                    out.writeChar(c);
                }
                String etag = entry.getEtag();
                if (etag == null) {
                    out.writeByte(0);
                } else {
                    out.writeByte(etag.length());
                    for (char c : etag.toCharArray()) {
                        out.writeChar(c);
                    }
                }
                out.writeLong(entry.getTime());
                out.writeLong(entry.getExpireTime());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class CacheEntry {
        private String url;
        private String etag;
        private long time;
        private long expireTime;

        public CacheEntry(String url, String etag, long time, long expireTime) {
            this.url = url;
            this.etag = etag;
            this.time = time;
            this.expireTime = expireTime;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void setExpireTime(long expireTime) {
            this.expireTime = expireTime;
        }

        public String getUrl() {
            return url;
        }

        public String getEtag() {
            return etag;
        }

        public long getTime() {
            return time;
        }

        public long getExpireTime() {
            return expireTime;
        }

        public File getFile() {
            return new File(DownloadThread.TEXTURE_CACHE.cacheDirectory, Base64.getUrlEncoder().encodeToString(url.getBytes()));
        }
    }
}
