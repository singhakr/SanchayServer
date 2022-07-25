package sanchay.server.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sanchay.common.SanchaySpringServerEndPoints;
import sanchay.server.dto.auth.model.domain.SanchayUserDTO;
import sanchay.server.dto.model.files.RemoteFile;
import sanchay.server.dto.payload.TextFileUpload;
import sanchay.server.dto.payload.UploadFileResponse;
import sanchay.server.dto.tree.impl.RemoteFileNode;
import sanchay.server.service.FileStorageService;
import sanchay.server.service.SanchayUserService;
import sanchay.server.utils.SanchayServerUtils;
import sanchay.server.utils.SanchayServiceUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    @Autowired
    private final FileStorageService fileStorageService;
    private final SanchayUserService userService;

//    @PostMapping("/uploadFile")
    @RequestMapping(method = {RequestMethod.POST}, value = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestBody TextFileUpload textFileUpload) {
//        public ResponseEntity<UploadFileResponse> uploadFile(@RequestParam("fileResource") MultipartFile file, @RequestParam("remoteFile") RemoteFile remoteFile) {
//    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("filePathOnServer") String filePathOnServer) {
//        String fileName = fileStorageService.storeFile(file, remoteFile.getAbsolutePathOnServer());
        RemoteFile remoteFile = textFileUpload.getRemoteFile();

        String textFileContents = textFileUpload.getTextFileContents();

        byte[] decodedBytes = Base64.getDecoder().decode(textFileContents);
        textFileContents = new String(decodedBytes);

        String filePath = fileStorageService.storeFile(textFileContents, remoteFile.getAbsolutePathOnServer());

//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/downloadFile/")
//                .path(fileName)
//                .toUriString();

        UploadFileResponse uploadFileResponse = new UploadFileResponse(remoteFile.getFileName(), remoteFile.getAbsolutePathOnServer(),
                "TEXT_PLAIN", 0);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(uploadFileResponse);
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("filePathOnServer") String filePathOnServer) {
//        return Arrays.asList(files)
//                .stream()
//                .map(file -> uploadFile(file, filePathOnServer))
//                .collect(Collectors.toList());
        return new ArrayList<UploadFileResponse>();
    }

//    @GetMapping("/downloadFile/{fileName:.+}")
//    @GetMapping("/downloadFile")
    @RequestMapping(method = {RequestMethod.POST}, value = "/download", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<Resource> downloadFile(@RequestBody RemoteFile remoteFile) {
//    public ResponseEntity<Resource> downloadFile(@RequestBody FileForm fileForm, HttpServletRequest request, HttpServletResponse response) {
//    @PostMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestBody RemoteFile remoteFile, HttpServletRequest request, HttpServletResponse response) {
        RemoteFile annotationDir = getAnnotationDirectory();
        String downloadFilePath = String.valueOf(Paths.get(annotationDir.getAbsolutePathOnServer(), remoteFile.getRelativePath()).normalize());
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(downloadFilePath);
//        Resource resource = fileStorageService.loadFileAsResource(remoteFile.getRelativePath());
//        Resource resource = fileStorageService.loadFileAsResource(request.getParameter("filePath"));
//        Resource resource = fileStorageService.loadFileAsResource(remoteFile.getRelativePath());

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        contentType = MediaType.APPLICATION_JSON.getType();

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @RequestMapping(method = {RequestMethod.GET}, value = "/base",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RemoteFile getAnnotationDirectory()
    {
        RemoteFile remoteFile = new RemoteFile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        SanchayUserDTO currentUser = userService.getUserDTO(username, true);
        String annotationFolderPath = SanchayServerUtils.buildServerAnnotationFolderPath(currentUser);
        String relativePath = ".";

        remoteFile.setRelativePath(relativePath);
        remoteFile.setFileName((new File(remoteFile.getRelativePath()).getName()));
        remoteFile.setDirectory(true);

        String absolutePath = Paths.get(annotationFolderPath, relativePath).normalize().toAbsolutePath().toString();
        log.info("Absolute path for listing directories: {}", absolutePath);

        remoteFile.setAbsolutePathOnServer(absolutePath);

        return remoteFile;
    }

    @RequestMapping(method = {RequestMethod.POST}, value = "/is-directory", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean isDirectory(@RequestBody RemoteFile remoteFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        SanchayUserDTO currentUser = userService.getUserDTO(username, true);

        String annotationDir = SanchayServerUtils.buildServerAnnotationFolderPath(currentUser);

        File file = new File(annotationDir, remoteFile.getRelativePath());

        return file.isDirectory();
    }

//    @PostMapping(value = "/listFiles", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
//        produces = MediaType.TEXT_PLAIN_VALUE)
    @RequestMapping(method = {RequestMethod.POST}, value = "/list-files", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping("/listFiles")
//    public List<String> listFilesInDirectory(@RequestBody FileForm fileForm, HttpServletRequest request, HttpServletResponse response) throws IOException {
    public List<RemoteFile> listFilesInDirectory(@RequestBody RemoteFile parentRemoteFile, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        SanchayUserDTO currentUser = userService.getUserDTO(username, true);
        String annotationFolderPath = SanchayServerUtils.buildServerAnnotationFolderPath(currentUser);
        String relativePath = parentRemoteFile.getRelativePath();

        String absolutePath = Paths.get(annotationFolderPath, relativePath).normalize().toAbsolutePath().toString();
        log.info("Absolute path for listing directories: {}", absolutePath);

        relativePath = SanchayServerUtils.buildRelativePath(absolutePath, annotationFolderPath);

        String fileName = String.valueOf(Paths.get(absolutePath).normalize().getFileName());
        log.info("File name of parent directory: {}", fileName);
        parentRemoteFile.setFileName(fileName);
        parentRemoteFile.setAbsolutePathOnServer(absolutePath);
        log.info("Relative path of parent directory: {}", relativePath);
        parentRemoteFile.setRelativePath(relativePath);

//    public List<String> listFilesInDirectory(@RequestBody RemoteFile remoteFile) throws IOException {
        return fileStorageService.listFilesInDirectory(currentUser, parentRemoteFile);
//        return fileStorageService.listFilesInDirectory(request.getParameter("filePath"));
//        return fileStorageService.listFilesInDirectory(remoteFile.getRelativePath());
    }

    @GetMapping("/fileTree")
    public RemoteFileNode getFileTree(@RequestBody String dir) throws IOException {
        File file = new File(dir);
        String absPathServer = "RFS/" + dir;
        RemoteFile rfile = new RemoteFile(file.getName(), dir, absPathServer, null, true);
        RemoteFileNode rootRemoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, RemoteFileNode.SPRING_MODE);

        Set<String> fileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    absPathServer = "RFS/" + dir;
                    rfile = new RemoteFile(path.getFileName().toString(), dir, absPathServer, null, true);
                    RemoteFileNode remoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, RemoteFileNode.SPRING_MODE);

                    rootRemoteFileNode.add(remoteFileNode);
                } else {
                    absPathServer = "RFS/" + path.getFileName();
                    rfile = new RemoteFile(path.getFileName().toString(), path.toString(), absPathServer, null, true);
                    RemoteFileNode remoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, RemoteFileNode.SPRING_MODE);

                    rootRemoteFileNode.add(remoteFileNode);

                    List<RemoteFileNode> remoteFileNodeList = getRemoteFileNodeList(path);

                    int size = remoteFileNodeList.size();

                    for (int i = 0; i < size; i++)
                    {
                        remoteFileNode.add(remoteFileNodeList.get(i));
                    }
                }
            }
        }

        return rootRemoteFileNode;
    }

    private List<RemoteFileNode> getRemoteFileNodeList(Path path) throws IOException {

        List<RemoteFileNode> remoteFileNodeList = new ArrayList<>();

        try (DirectoryStream<Path> innerStream = Files.newDirectoryStream(path)) {
            for (Path innerPath : innerStream) {
                String absPathServer = "RFS/" + path.getFileName();
                RemoteFile rfile = new RemoteFile(innerPath.getFileName().toString(), path.toString(), absPathServer, null, true);
                RemoteFileNode remoteFileNode = RemoteFileNode.getRemoteFileNodeInstance(null, null, rfile, RemoteFileNode.SPRING_MODE);

                remoteFileNodeList.add(remoteFileNode);
            }
        }

        return remoteFileNodeList;
    }
}

@Data
class FileForm
{
    private String filePath;
}

@Data
class RemoteFileForm
{
    private RemoteFile remoteFile;
}
