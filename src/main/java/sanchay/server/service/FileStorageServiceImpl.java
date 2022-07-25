package sanchay.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sanchay.server.dto.auth.model.domain.SanchayUserDTO;
import sanchay.server.dto.model.files.RemoteFile;
import sanchay.server.exceptions.FileStorageException;
import sanchay.server.exceptions.SanchayFileNotFoundException;
import sanchay.server.properties.FileStorageProperties;
import sanchay.server.utils.SanchayServerUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String filePathOnServer) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public String storeFile(String textContents, String filePathOnServer)
    {
        try {
            FileWriter fileWriter = new FileWriter(filePathOnServer);
            fileWriter.write(textContents);
            fileWriter.close();

            return filePathOnServer;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + filePathOnServer + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String filePath) {
        try {
//            Path path = Paths.get(".", "temp.txt");
            Path path = Paths.get(filePath);
            path = path.normalize();
//            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(path.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new SanchayFileNotFoundException("File not found " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new SanchayFileNotFoundException("File not found " + filePath, ex);
        }
    }

    private boolean isPermittedOnPath()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {

        }

        return true;
    }

    public List<RemoteFile> listFilesInDirectory(SanchayUserDTO user, RemoteFile parentRemoteFile)
    {
        List<RemoteFile> remoteFiles = new ArrayList<>();

        File file = new File(parentRemoteFile.getAbsolutePathOnServer());

        if(file.isDirectory())
        {
            File[] files = file.listFiles();

            for(int i = 0; i < files.length; i++)
            {
                RemoteFile remoteFile = new RemoteFile();
                String absolutePath = String.valueOf(Paths.get(files[i].getAbsolutePath()).normalize());
                log.info("Absolute path of inner file: {}", absolutePath);
                remoteFile.setAbsolutePathOnServer(absolutePath);
                String fileName = (new File(absolutePath)).getName();
                log.info("File name of inner file: {}", fileName);

                if(fileName.equals("$USER_HOME$"))
                {
                    break;
                }

                remoteFile.setFileName(fileName);

                String relativePath = SanchayServerUtils.buildRelativePath(remoteFile.getAbsolutePathOnServer(),
                        SanchayServerUtils.buildServerAnnotationFolderPath(user));
                log.info("Relative path of inner file: {}", relativePath);

                remoteFile.setRelativePath(relativePath);

                if(files[i].isDirectory())
                {
                    remoteFile.setDirectory(true);
                }
                else
                {
                    remoteFile.setDirectory(false);
                }

                remoteFiles.add(remoteFile);
            }
        }

        return remoteFiles;
    }
}
