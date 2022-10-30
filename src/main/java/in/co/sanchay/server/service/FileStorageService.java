package in.co.sanchay.server.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import in.co.sanchay.server.dto.auth.model.domain.SanchayUserDTO;
import in.co.sanchay.server.dto.model.files.RemoteFile;

import java.util.List;

public interface FileStorageService {
    String storeFile(MultipartFile file, String filePathOnServer);
    String storeFile(String textContents, String filePathOnServer);
    Resource loadFileAsResource(String filePath);
    List<RemoteFile> listFilesInDirectory(SanchayUserDTO user, RemoteFile parentRemoteFile);

}
