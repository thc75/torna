package cn.torna.sdk.common;


import cn.torna.sdk.util.FileUtil;
import cn.torna.sdk.util.MD5Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * 文件上传类
 * @author tanghc
 */
public class UploadFile implements Serializable {
    private static final long serialVersionUID = -1100614660944996398L;

    /**
     * @param name 表单名称，不能重复
     * @param file 文件
     * @throws IOException
     */
    public UploadFile(String name, File file) throws IOException {
        this(name, file.getName(), FileUtil.toBytes(file));
    }

    /**
     * @param name 表单名称，不能重复
     * @param fileName 文件名
     * @param input 文件流
     * @throws IOException
     */
    public UploadFile(String name, String fileName, InputStream input) throws IOException {
        this(name, fileName, FileUtil.toBytes(input));
    }

    /**
     * @param name 表单名称，不能重复
     * @param fileName 文件名
     * @param fileData 文件数据
     */
    public UploadFile(String name, String fileName, byte[] fileData) {
        super();
        this.name = name;
        this.fileName = fileName;
        this.fileData = fileData;
        this.md5 = MD5Util.encrypt(fileData);
    }

    private String name;
    private String fileName;
    private byte[] fileData;
    private String md5;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

}
