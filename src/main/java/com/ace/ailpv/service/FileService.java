package com.ace.ailpv.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileUploadUtilService fileUploadUtilService;

    String courseFilePath = "src\\main\\resources\\static\\courses\\";

    public void createFolderForCourse(String courseName) {
        File theDir = new File(courseFilePath + courseName);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }
        File videoFolder = new File(courseFilePath + courseName + "\\video");
        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }
        File resourceFolder = new File(courseFilePath + courseName + "\\resource");
        if (!resourceFolder.exists()) {
            resourceFolder.mkdirs();
        }
    }

    public void createFolderForBatch(String path, Long batchId) {
        String strBatchId = Long.toString(batchId);
        File theDir = new File(path + strBatchId);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }
        File assignmentQuestionFolder = new File(path + strBatchId + "\\AssignmentQuestion");
        if (!assignmentQuestionFolder.exists()) {
            assignmentQuestionFolder.mkdirs();
        }
        File assignmentAnswerFolder = new File(path + strBatchId + "\\AssignmentAnswer");
        if (!assignmentAnswerFolder.exists()) {
            assignmentAnswerFolder.mkdirs();
        }
    }

    // added by me
    public void createFolderForAssignment(String courseName) {
        File assignmentFolder = new File(courseFilePath + courseName + "\\" + "assignmentQuesitions\\");
        if (!assignmentFolder.exists()) {
            assignmentFolder.mkdirs();
        }
    }

    public void createFolderForAssignmentAnswer(String questionFileName) {
        File assignmentFolder = new File(courseFilePath + questionFileName + "\\assignment" + "\\answer");
        if (!assignmentFolder.exists()) {
            assignmentFolder.mkdirs();
        }
    }
    // end

    public void createFile(MultipartFile file, String folderName) throws IllegalStateException, IOException {
        fileUploadUtilService.saveFile(courseFilePath + folderName + "\\", file.getOriginalFilename(), file);
    }

    public void deleteFolder(String folderName) throws IOException {
        File directory = new File(courseFilePath + folderName);
        FileUtils.deleteDirectory(directory);
    }

    public void deleteBatchFolder(String path,String folderName) throws IOException {
        File directory = new File(path + folderName);
        FileUtils.deleteDirectory(directory);
    }

    public Set<String> listFilesUsingJavaIO(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public boolean deleteFile(String fileName) throws IOException {
        boolean isDeleted = new File(fileName).delete();
        return isDeleted;
    }

    public void renameDir(String oldDir, String newDir) throws IOException {
        Path source = Paths.get(courseFilePath, oldDir);
        Files.move(source, source.resolveSibling(newDir));
    }

    public String generateFileName(String fileExtension) {
        return String.join("", UUID.randomUUID().toString().split("-")) + "." + fileExtension;
    }

}