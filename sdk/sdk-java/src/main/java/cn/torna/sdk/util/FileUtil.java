package cn.torna.sdk.util;

import java.io.*;

public class FileUtil {

    /**
     * The default buffer size to use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final int EOF = -1;

    /**
     * 将文件流转换成byte[]
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toBytes(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int n = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 将文件转换成数据流
     * @param file 文件
     * @return 返回数据流
     * @throws IOException
     */
    public static byte[] toBytes(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            return toBytes(input);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {
            }
        }
    }
}
