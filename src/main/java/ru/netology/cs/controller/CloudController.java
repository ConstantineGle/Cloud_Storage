package ru.netology.cs.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cs.constant.Constant;
import ru.netology.cs.exception.ErrorInput;
import ru.netology.cs.model.userfile.FileInfo;
import ru.netology.cs.service.CloudService;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = {"${settings.cors_origin}"}, allowedHeaders = "*", allowCredentials = "true")
@RestController
public class CloudController {
    private final CloudService cloudService;

    public CloudController(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    /*** POST сохранить файл **/
    @PostMapping(value = "/file", consumes = "multipart/form-data")
    public String uploadFile(@RequestParam("filename") String filename, @RequestBody MultipartFile file) {
        cloudService.uploadFile(filename, file);
        return Constant.SUCCESS_UPLOAD;
    }

    /*** PUT изменить имя файла **/
    @PutMapping(value = "/file", consumes = "application/json")
    public String editFileName(@RequestParam("filename") String filename, @RequestBody Map<String, String> bodyParams) {
        cloudService.editFileName(filename, bodyParams.get("filename"));
        return Constant.SUCCESS_UPLOAD;
    }

    /*** DELETE удалить файл **/
    @DeleteMapping("/file")
    public String deleteFile(@RequestParam("filename") String filename) {
        cloudService.deleteFile(filename);
        return Constant.SUCCESS_DEL;
    }

    /*** GET скачать файл **/
    @GetMapping("/file")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename) {
        Resource resource = cloudService.downloadFile(filename);
        return ResponseEntity.ok().body(resource);
    }

    /*** получить список файлов **/
    @GetMapping("/list")
    public List<FileInfo> getUserFileList(@RequestParam("limit") int limit) {
        return cloudService.getUserFileList(limit);
    }

    @ExceptionHandler(ErrorInput.class)
    ResponseEntity<String> handlerErrorInputData(ErrorInput errorInput) {
        return new ResponseEntity<>(cloudService.makeErrorJson(errorInput.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<String> handlerRuntimeException(RuntimeException runtimeException) {
        runtimeException.printStackTrace();
        return new ResponseEntity<>(cloudService.makeErrorJson(Constant.SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
