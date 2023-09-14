package com.example.fileservice.service.implementations;

import com.example.fileservice.entity.ImageEntity;
import com.example.fileservice.exceptions.FtpConnectionException;
import com.example.fileservice.service.FtpService;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class FtpServiceImpl implements FtpService {

    @Value("${ftp.server}")
    private String FTP_SERVER;
    @Value("${ftp.username}")
    private String FTP_USERNAME;
    @Value("${ftp.password}")
    private String FTP_PASSWORD;
    @Value("${ftp.origin}")
    private String FTP_ORIGIN_DIRECTORY;
    @Value("${ftp.port}")
    private int FTP_PORT;

    @Override
    public ImageEntity uploadFileToFtp(MultipartFile file) throws FtpConnectionException, IOException {
        try {
            FTPClient ftpClient = getConnection();
            String remoteFilePath = FTP_ORIGIN_DIRECTORY
                    + "/"
                    + LocalDate.now()
                    + "/"
                    + file.getOriginalFilename();
            boolean uploaded = streamFile(file, ftpClient, remoteFilePath);

            if(!uploaded){
               createDir(ftpClient);
               if (!streamFile(file, ftpClient, remoteFilePath)){
                        throw new FtpConnectionException("Cannot connect to server");
               }
            }
            ftpClient.logout();
            ftpClient.disconnect();
            return ImageEntity.builder()
                    .path(remoteFilePath)
                    .uuid(UUID.randomUUID().toString())
                    .createAt(LocalDate.now())
                    .isUsed(false)
                    .build();

        } catch(IOException e) {
            throw new FtpConnectionException(e);
        }
    }

    @Override
    public boolean deleteFile(String path) throws IOException {
        FTPClient client = getConnection();
        boolean deleted = client.deleteFile(path);
        client.logout();
        client.disconnect();
        return deleted;
    }

    @Override
    public ByteArrayOutputStream getFile(ImageEntity imageEntity) throws IOException {
        FTPClient client = getConnection();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean downloaded = client.retrieveFile(imageEntity.getPath(), outputStream);
        client.logout();
        client.disconnect();
        if (downloaded) {
            return outputStream;
        }
        throw new FtpConnectionException("Cannot download file");
    }

    private FTPClient getConnection() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(FTP_SERVER,FTP_PORT);
        ftpClient.login(FTP_USERNAME, FTP_PASSWORD);

        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    private void createDir(FTPClient client) throws IOException {
        client.makeDirectory(FTP_ORIGIN_DIRECTORY
                                     + "/"
                                     + LocalDate.now());
    }
    private boolean streamFile(MultipartFile file,
                               FTPClient client,
                               String remoteFilePath) throws IOException {
        InputStream inputStream = file.getInputStream();
        boolean uploaded = client.storeFile(remoteFilePath, inputStream);
        inputStream.close();
        return uploaded;
    }
}
