package com.ravey.common.object.store.template;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public interface ObjectStoreTemplate {

    void uploadFileWithDownloadName(String fileName, byte[] fileData, String downloadName);

    void uploadFileWithDownloadName(String fileName, byte[] fileData, String downloadName, String bucketName);

    /**
     * 上传文件
     *
     * @param fileName 文件名称
     * @param fileData 文件数据
     */
    void uploadFile(String fileName, byte[] fileData);

    /**
     * 上传文件
     *
     * @param fileName 文件名称
     * @param file     文件
     */
    void uploadFile(String fileName, File file);

    /**
     * 上传文件
     *
     * @param fileName   文件名称
     * @param fileData   文件数据
     * @param bucketName bucket 名
     */
    void uploadFile(String fileName, byte[] fileData, String bucketName);

    /**
     * 查看文件列表
     *
     * @param keyPrefix 文件前缀
     * @return key list
     */
    List<String> listFile(String keyPrefix);

    /**
     * 下载文件
     *
     * @param objectName            文件名称
     * @param destinationObjectName 目标文件
     */
    void downloadFile(String objectName, String destinationObjectName);

    /**
     * 拷贝文件
     *
     * @param sourceObjectName      源文件名称
     * @param destinationObjectName 目标文件名称
     * @return Etag
     */
    String copyFile(String sourceObjectName, String destinationObjectName);

    /**
     * 删除文件
     *
     * @param objectName 文件名称
     */
    void deleteFile(String objectName);

    /**
     * 流式下载
     *
     * @param objectName 文件名称
     * @return 字节流
     */
    ByteArrayOutputStream downloadFile(String objectName);
}
