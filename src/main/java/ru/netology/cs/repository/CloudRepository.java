package ru.netology.cs.repository;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cs.constant.Constant;
import ru.netology.cs.exception.ErrorInput;
import ru.netology.cs.exception.ErrorServer;
import ru.netology.cs.model.history.History;
import ru.netology.cs.model.user.User;
import ru.netology.cs.model.userfile.FileInfo;
import ru.netology.cs.model.userfile.UserFile;
import ru.netology.cs.security.JwtProvider;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Transactional
@Repository
public class CloudRepository {

    private final UserFileCrudRepository userFileCrudRepository;
    private final UserCrudRepository userCrudRepository;
    private final HistoryCrudRepository historyCrudRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public CloudRepository(UserFileCrudRepository userFileCrudRepository,
                           UserCrudRepository userCrudRepository,
                           HistoryCrudRepository historyCrudRepository,
                           JwtProvider jwtProvider,
                           PasswordEncoder passwordEncoder) {
        this.userFileCrudRepository = userFileCrudRepository;
        this.userCrudRepository = userCrudRepository;
        this.historyCrudRepository = historyCrudRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public void addHistory(User user, String desc) {
        History history = History.builder()
                .user(user)
                .description(desc)
                .uploadDate(LocalDate.now())
                .build();
        historyCrudRepository.save(history);
    }

    public String makeErrorJson(String msg) {
        return "{\"message\":\"" + msg + "\",\"id\":\"0\"}";
    }

    public String getUserName() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }

    //login
    public String login(String login, String password) {
        User user = userCrudRepository.findByLogin(login)
                .orElseThrow(() -> new ErrorInput(Constant.USR_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new ErrorInput(Constant.WRN_USR_PASS);
        addHistory(user, "login");
        return jwtProvider.generateToken(login);
    }

    //Сохранить
    public void uploadFile(String filename, MultipartFile file) {
        User user = userCrudRepository.findByLogin(getUserName())
                .orElseThrow(() -> new ErrorInput(Constant.USR_NOT_FOUND));
        if (userFileCrudRepository.countByFileInfoFilenameAndUserId(filename, user.getId()) > 0)
            throw new ErrorInput(Constant.ERR_FILE_EXISTS);

        try {
            UserFile userFile = UserFile.builder()
                    .fileInfo(new FileInfo(filename, file.getSize()))
                    .content(file.getBytes())
                    .uploadDate(LocalDate.now())
                    .user(user)
                    .build();
            userFileCrudRepository.save(userFile);
            addHistory(user, "upload file=" + filename);
        } catch (IOException e) {
            throw new ErrorInput(Constant.ERR_UPLOAD);
        }
    }

    //Изменить имя
    public void editFileName(String filename, String name) {
        User user = userCrudRepository.findByLogin(getUserName())
                .orElseThrow(() -> new ErrorInput(Constant.USR_NOT_FOUND));

        long fileId = userFileCrudRepository.getIdUserFileByName(filename, user.getUsername())
                .orElseThrow(() -> new ErrorInput(Constant.FILE_NOT_FOUND));

        if (userFileCrudRepository.countByFileInfoFilenameAndUserId(name, user.getId()) > 0)
            throw new ErrorInput(Constant.ERR_FILE_EXISTS);

        if (userFileCrudRepository.updateFileNameById(name, fileId) == 0)
            throw new ErrorServer(Constant.ERR_EDIT_NAME);
        addHistory(user, "edit file name=" + filename + ", new name=" + name);
    }

    //Удалить
    public void deleteFile(String filename) {
        User user = userCrudRepository.findByLogin(getUserName())
                .orElseThrow(() -> new ErrorInput(Constant.USR_NOT_FOUND));

        long fileId = userFileCrudRepository.getIdUserFileByName(filename, user.getUsername())
                .orElseThrow(() -> new ErrorInput(Constant.FILE_NOT_FOUND));

        userFileCrudRepository.deleteById(fileId);
        if (userFileCrudRepository.countByIdAndUserLogin(fileId, user.getUsername()) > 0)
            throw new ErrorServer(Constant.ERR_DELETE);
        addHistory(user, "delete file =" + filename);
    }

    //Скачать
    public Resource downloadFile(String filename) {
        User user = userCrudRepository.findByLogin(getUserName())
                .orElseThrow(() -> new ErrorInput(Constant.USR_NOT_FOUND));

        byte[] content = userFileCrudRepository.findByFileInfoFilenameAndUserLogin(filename, user.getUsername())
                .orElseThrow(() -> new ErrorInput(Constant.FILE_NOT_FOUND))
                .getContent();
        addHistory(user, "download file =" + filename);
        return new ByteArrayResource(content);
    }

    //Список файлов
    public List<FileInfo> getUserFileList(int limit) {
        User user = userCrudRepository.findByLogin(getUserName())
                .orElseThrow(() -> new ErrorInput(Constant.USR_NOT_FOUND));

        PageRequest paging = PageRequest.of(0, limit, Sort.by("fileInfo.filename"));
        addHistory(user, "list of files");
        return userFileCrudRepository.findAllUserFiles(user.getUsername(), paging);
    }

}
