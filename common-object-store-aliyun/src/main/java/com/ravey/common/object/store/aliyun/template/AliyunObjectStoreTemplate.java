package com.ravey.common.object.store.aliyun.template;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.ravey.common.object.store.aliyun.properties.AliyunObjectStoreProperties;
import com.ravey.common.object.store.template.ObjectStoreTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AliyunObjectStoreTemplate implements ObjectStoreTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(AliyunObjectStoreTemplate.class);
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public AliyunObjectStoreTemplate(AliyunObjectStoreProperties properties) {
        this.endpoint = properties.getEndpoint();
        this.accessKeyId = properties.getAccessKeyId();
        this.accessKeySecret = properties.getAccessKeySecret();
        this.bucketName = properties.getBucketName();
    }

    @Override
    public void uploadFileWithDownloadName(String fileName, byte[] fileData, String downloadName) {
        OSS client = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentDisposition("attachment;filename=" + URLEncoder.encode(downloadName, "utf-8"));
            client.putObject(this.bucketName, fileName, new ByteArrayInputStream(fileData), meta);
        } catch (Exception e) {
            LOGGER.error("上传文件[{}]到OSS出错: ", fileName, e);
            throw new RuntimeException(e);
        } finally {
            client.shutdown();
        }

    }

    @Override
    public void uploadFileWithDownloadName(String fileName, byte[] fileData, String downloadName, String bucketName) {
        OSS client = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentDisposition("attachment;filename=" + URLEncoder.encode(downloadName, "utf-8"));
            client.putObject(bucketName, fileName, new ByteArrayInputStream(fileData), meta);
        } catch (Exception e) {
            LOGGER.error("上传文件[{}]到OSS出错: ", fileName, e);
            throw new RuntimeException(e);
        } finally {
            client.shutdown();
        }

    }

    @Override
    public void uploadFile(String fileName, byte[] fileData) {
        OSS client = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        try {
            client.putObject(this.bucketName, fileName, new ByteArrayInputStream(fileData));
        } catch (Exception e) {
            LOGGER.error("上传文件[{}]到OSS出错: ", fileName, e);
            throw new RuntimeException(e);
        } finally {
            client.shutdown();
        }

    }

    @Override
    public void uploadFile(String fileName, File file) {
        OSS client = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        try {
            client.putObject(this.bucketName, fileName, file);
        } catch (Exception e) {
            LOGGER.error("上传文件[{}]到OSS出错: ", fileName, e);
            throw new RuntimeException(e);
        } finally {
            client.shutdown();
        }

    }

    @Override
    public void uploadFile(String fileName, byte[] fileData, String bucketName) {
        OSS client = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        try {
            client.putObject(bucketName, fileName, new ByteArrayInputStream(fileData));
        } catch (Exception e) {
            LOGGER.error("上传文件[{}]到OSS出错: ", fileName, e);
            throw new RuntimeException(e);
        } finally {
            client.shutdown();
        }

    }

    @Override
    public List<String> listFile(String keyPrefix) {
        Integer maxKeys = 200;
        List<String> list = new ArrayList<>();
        OSS ossClient = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);
        String nextMarker = null;

        try {
            ObjectListing objectListing;
            try {
                do {
                    objectListing = ossClient.listObjects((new ListObjectsRequest(this.bucketName)).withPrefix(keyPrefix).withMarker(nextMarker).withMaxKeys(maxKeys));
                    List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
                    Iterator<OSSObjectSummary> var8 = sums.iterator();

                    while(var8.hasNext()) {
                        OSSObjectSummary s = var8.next();
                        list.add(s.getKey());
                    }

                    nextMarker = objectListing.getNextMarker();
                } while(objectListing.isTruncated());
            } catch (Exception e) {
                LOGGER.error("获取文件列表出错: ", e);
                throw new RuntimeException(e);
            }
        } finally {
            ossClient.shutdown();
        }

        return list;
    }

    @Override
    public void downloadFile(String objectName, String destinationObjectName) {
        OSS ossClient = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        try {
            ossClient.getObject(new GetObjectRequest(this.bucketName, objectName), new File(destinationObjectName));
        } catch (Exception e) {
            LOGGER.error("下载文件[{}]出错: ", objectName, e);
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }

    }

    @Override
    public String copyFile(String sourceObjectName, String destinationObjectName) {
        OSS ossClient = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        String var5;
        try {
            CopyObjectResult result = ossClient.copyObject(this.bucketName, sourceObjectName, this.bucketName, destinationObjectName);
            var5 = result.getETag();
        } catch (Exception e) {
            LOGGER.error("拷贝文件[{}],[{}]出错: ", sourceObjectName, destinationObjectName, e);
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }

        return var5;
    }

    @Override
    public void deleteFile(String objectName) {
        OSS ossClient = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        try {
            ossClient.deleteObject(this.bucketName, objectName);
        } catch (Exception e) {
            LOGGER.error("删除文件[{}]出错: ", objectName, e);
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }

    }

    @Override
    public ByteArrayOutputStream downloadFile(String objectName) {
        OSS ossClient = (new OSSClientBuilder()).build(this.endpoint, this.accessKeyId, this.accessKeySecret);

        ByteArrayOutputStream baos;
        try {
            OSSObject ossObject = ossClient.getObject(this.bucketName, objectName);
            baos = new ByteArrayOutputStream();

            int temp;
            while((temp = ossObject.getObjectContent().read()) != -1) {
                baos.write(temp);
            }

            return baos;
        } catch (Exception e) {
            LOGGER.error("下载文件[{}]出错: ", objectName, e);
            baos = null;
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }
    }
}
