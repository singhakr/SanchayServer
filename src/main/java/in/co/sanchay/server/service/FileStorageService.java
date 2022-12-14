package in.co.sanchay.server.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.web.multipart.MultipartFile;
import in.co.sanchay.server.dto.auth.model.domain.SanchayUserDTO;
import in.co.sanchay.server.dto.model.files.RemoteFile;

import java.util.List;

public interface FileStorageService {
    String storeFile(MultipartFile file, String charset, String filePathOnServer);
    String storeFile(String textContents, String charset, String filePathOnServer);
    EncodedResource loadFileAsEncodedResource(String filePath, String charset);
    List<RemoteFile> listFilesInDirectory(SanchayUserDTO user, RemoteFile parentRemoteFile);

}
