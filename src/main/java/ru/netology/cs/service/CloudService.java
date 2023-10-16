package ru.netology.cs.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cs.constant.Constant;
import ru.netology.cs.exception.ErrorInput;
import ru.netology.cs.model.userfile.FileInfo;
import ru.netology.cs.repository.CloudRepository;

import java.util.List;

@Service
public class CloudService {
    private final CloudRepository cloudRepository;

    public CloudService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    // Сохранить
    public void uploadFile(String filename, MultipartFile file) {
        if (filename == null || filename.isEmpty()) throw new ErrorInput(Constant.EMPTY_FILE_NAME);
        cloudRepository.uploadFile(filename, file);
    }

    // Изменить имя файла
    public void editFileName(String filename, String name) {
        if (filename == null || filename.isEmpty() || name == null || name.isEmpty())
            throw new ErrorInput(Constant.EMPTY_FILE_NAME);
        if (filename.equals(name)) throw new ErrorInput(Constant.EQL_FILE_NAMES);
        cloudRepository.editFileName(filename, name);
    }

    //Удалить
    public void deleteFile(String filename) {
        if (filename == null || filename.isEmpty()) throw new ErrorInput(Constant.EMPTY_FILE_NAME);
        cloudRepository.deleteFile(filename);
    }

    //Скачать
    public Resource downloadFile(String filename) {
        if (filename == null || filename.isEmpty()) throw new ErrorInput(Constant.EMPTY_FILE_NAME);
        return cloudRepository.downloadFile(filename);
    }

    //Список файлов
    public List<FileInfo> getUserFileList(int limit) {
        if (limit <= 0) throw new ErrorInput(Constant.WRN_LIST_LIM);
        return cloudRepository.getUserFileList(limit);
    }

    public String makeErrorJson(String msg) {
        return cloudRepository.makeErrorJson(msg);
    }

}
