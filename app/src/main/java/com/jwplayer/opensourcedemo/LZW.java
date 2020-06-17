package com.jwplayer.opensourcedemo;

import com.netflix.msl.io.LZWInputStream;
import com.netflix.msl.io.LZWOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LZW {
    public static byte[] compress(byte[] bArr) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length);
        LZWOutputStream lZWOutputStream = new LZWOutputStream(byteArrayOutputStream);
        try {
            lZWOutputStream.write(bArr);
            lZWOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Throwable th) {
            lZWOutputStream.close();
            throw th;
        }
    }

    public static byte[] uncompress(byte[] bArr, int i) throws IOException {
        LZWInputStream lZWInputStream = new LZWInputStream(new ByteArrayInputStream(bArr));
        try {
            int length = bArr.length;
            byte[] bArr2 = new byte[length];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length);
            while (true) {
                if (length <= 0) {
                    break;
                }
                int read = lZWInputStream.read(bArr2);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr2, 0, read);
            }
            return byteArrayOutputStream.toByteArray();
        } finally {
            lZWInputStream.close();
        }
    }
}
