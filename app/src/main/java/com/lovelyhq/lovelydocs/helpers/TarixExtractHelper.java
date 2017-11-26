package com.lovelyhq.lovelydocs.helpers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

public class TarixExtractHelper {
    public static void writeToFile(String tgzPath, int blockNum, int numBlocks, int offset, String fullFilePath) {
        try {
            int numBytes;
            FileInputStream fileInputStream = new FileInputStream(tgzPath);
            FileOutputStream fileOutputStream = new FileOutputStream(tgzPath.replace(".tgz", ".tar"));
            InflaterInputStream inflaterInputStream = new InflaterInputStream(fileInputStream, new Inflater(true));
            byte[] buffer = new byte[512];
            fileInputStream.skip((long) offset);
            int bytesDecompressed = 0;
            while (inflaterInputStream.available() > 0 && bytesDecompressed < numBlocks * 512) {
                numBytes = inflaterInputStream.read(buffer);
                if (numBytes > 0) {
                    fileOutputStream.write(buffer, 0, numBytes);
                    bytesDecompressed += numBytes;
                }
            }
            fileInputStream.close();
            fileOutputStream.close();
            TarArchiveInputStream tarInputStream = new TarArchiveInputStream(new FileInputStream(tgzPath.replace(".tgz", ".tar")));
            ArchiveEntry entry = tarInputStream.getNextEntry();
            File file = new File(fullFilePath);
            file.getParentFile().mkdirs();
            FileOutputStream newFileStream = new FileOutputStream(file);
            while (tarInputStream.available() > 0) {
                numBytes = tarInputStream.read(buffer);
                if (numBytes > 0) {
                    newFileStream.write(buffer, 0, numBytes);
                }
            }
            tarInputStream.close();
            newFileStream.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    public static byte[] unpackFileUsingTarix(String tgzPath, int blockNum, int numBlocks, int offset) {
        try {
            int numBytes;
            FileInputStream fileInputStream = new FileInputStream(tgzPath);
            FileOutputStream fileOutputStream = new FileOutputStream(tgzPath.replace(".tgz", ".tar"));
            InflaterInputStream inflaterInputStream = new InflaterInputStream(fileInputStream, new Inflater(true));
            byte[] buffer = new byte[512];
            fileInputStream.skip((long) offset);
            int bytesDecompressed = 0;
            while (inflaterInputStream.available() > 0 && bytesDecompressed < numBlocks * 512) {
                numBytes = inflaterInputStream.read(buffer);
                if (numBytes > 0) {
                    fileOutputStream.write(buffer, 0, numBytes);
                    bytesDecompressed += numBytes;
                }
            }
            fileInputStream.close();
            fileOutputStream.close();
            TarArchiveInputStream tarInputStream = new TarArchiveInputStream(new FileInputStream(tgzPath.replace(".tgz", ".tar")));
            ArchiveEntry entry = tarInputStream.getNextEntry();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (tarInputStream.available() > 0) {
                numBytes = tarInputStream.read(buffer);
                if (numBytes > 0) {
                    byteArrayOutputStream.write(buffer, 0, numBytes);
                }
            }
            tarInputStream.close();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (FileNotFoundException e) {
            return null;
        } catch (UnsupportedEncodingException e2) {
            return null;
        } catch (IOException e3) {
            return null;
        }
    }
}
