package main;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileUploadProperties {
    private String uploadDir;
    private String mysqlSecureDir;

    public String getMysqlSecureDir() {
        return mysqlSecureDir;
    }

    public void setMysqlSecureDir(String mysqlSecureDir) {
        this.mysqlSecureDir = mysqlSecureDir;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
