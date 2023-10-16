package ru.netology.cs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cs.exception.ErrorInput;
import ru.netology.cs.model.userfile.FileInfo;
import ru.netology.cs.repository.CloudRepository;
import ru.netology.cs.repository.UserFileCrudRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Testcontainers
public class CloudRepositoryTests {

    private final static String text = "test content";
    private final static String login = "test";

    @Autowired
    private UserFileCrudRepository userFileCrudRepository;

    @Autowired
    CloudRepository cloudRepository;

    @Container
    public static PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:12-alpine")
            .withDatabaseName("app_db")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void Properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DB::getJdbcUrl);
        registry.add("spring.datasource.username", DB::getUsername);
        registry.add("spring.datasource.password", DB::getPassword);
    }

    @Test
    void test_login_thrown_WRN_USR_PASS() {
        String username = "test";
        String password = "test555";
        Assertions.assertThrows(ErrorInput.class, () -> cloudRepository.login(username, password));
    }

    @Test
    void test_login_thrown_USR_NOT_FOUND() {
        String username = "test555";
        String password = "test";
        Assertions.assertThrows(ErrorInput.class, () -> cloudRepository.login(username, password));
    }

    @Test
    void test_login_Success() {
        String username = "test";
        String password = "test";
        String token = cloudRepository.login(username, password);
        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_uploadFile_Success() {
        // given
        String filename1 = "test_uploadFile_Success.txt";
        MultipartFile file = new MockMultipartFile(filename1, text.getBytes(StandardCharsets.UTF_8));
        // when
        cloudRepository.uploadFile(filename1, file);
        Optional<Long> id = userFileCrudRepository.getIdUserFileByName(filename1, login);
        // then
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_uploadFile_throw_ERR_FILE_EXISTS() {
        // given
        String filename1 = "test_uploadFile_throw_ERR_FILE_EXISTS.txt";
        MultipartFile file = new MockMultipartFile(filename1, text.getBytes(StandardCharsets.UTF_8));
        // when
        cloudRepository.uploadFile(filename1, file);
        Optional<Long> id = userFileCrudRepository.getIdUserFileByName(filename1, login);
        // then
        Assertions.assertTrue(id.isPresent());
        Assertions.assertThrows(ErrorInput.class, () -> cloudRepository.uploadFile(filename1, file));
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_editFileName_Success() {
        // given
        String filename1 = "1111.txt";
        String filename2 = "test_editFileName_Success.txt";
        MultipartFile file = new MockMultipartFile(filename1, text.getBytes(StandardCharsets.UTF_8));
        cloudRepository.uploadFile(filename1, file);
        // when
        cloudRepository.editFileName(filename1, filename2);
        Optional<Long> id2 = userFileCrudRepository.getIdUserFileByName(filename2, login);
        Optional<Long> id1 = userFileCrudRepository.getIdUserFileByName(filename1, login);
        // then
        Assertions.assertTrue(id2.isPresent());
        Assertions.assertFalse(id1.isPresent());
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_editFileName_throw_ERR_FILE_EXISTS() {
        // given
        String filename1 = "test_editFileName_throw_ERR_FILE_EXISTS.txt";
        MultipartFile file = new MockMultipartFile(filename1, text.getBytes(StandardCharsets.UTF_8));
        cloudRepository.uploadFile(filename1, file);
        // then
        Assertions.assertThrows(ErrorInput.class, () -> cloudRepository.editFileName(filename1, filename1));
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_editFileName_throw_FILE_NOT_FOUND() {
        // given
        String filename1 = "test_editFileName_throw_FILE_NOT_FOUND.txt";
        // then
        Assertions.assertThrows(ErrorInput.class, () -> cloudRepository.editFileName(filename1, "new.txt"));
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_deleteFile_throw_FILE_NOT_FOUND() {
        // given
        String filename1 = "test_deleteFile_throw_FILE_NOT_FOUND.txt";
        // then
        Assertions.assertThrows(ErrorInput.class, () -> cloudRepository.deleteFile(filename1));
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_deleteFile_Success() {
        // given
        String filename1 = "test_deleteFile_Success.txt";
        MultipartFile file = new MockMultipartFile(filename1, text.getBytes(StandardCharsets.UTF_8));
        cloudRepository.uploadFile(filename1, file);
        // when
        cloudRepository.deleteFile(filename1);
        Optional<Long> id = userFileCrudRepository.getIdUserFileByName(filename1, login);
        // then
        Assertions.assertFalse(id.isPresent());
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_downloadFile_Success() {
        // given
        String filename1 = "test_downloadFile_Success.txt";
        MultipartFile file = new MockMultipartFile(filename1, text.getBytes(StandardCharsets.UTF_8));
        cloudRepository.uploadFile(filename1, file);
        // when
        Resource content = cloudRepository.downloadFile(filename1);
        // then
        Assertions.assertTrue(content.exists());
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_downloadFile_throw_FILE_NOT_FOUND() {
        // given
        String filename1 = "test_downloadFile_throw_FILE_NOT_FOUND.txt";
        // then
        Assertions.assertThrows(ErrorInput.class, () -> cloudRepository.downloadFile(filename1));
    }

    @Test
    @WithMockUser(username = login, authorities = {"USER"})
    void test_getUserFileList_Success() {
        // given
        String filename1 = "test_getUserFileList_1.txt";
        String filename2 = "test_getUserFileList_2.txt";
        int size1 = 1;
        int size2 = 2;
        MultipartFile file = new MockMultipartFile(filename1, text.getBytes(StandardCharsets.UTF_8));
        cloudRepository.uploadFile(filename1, file);
        MultipartFile file2 = new MockMultipartFile(filename2, text.getBytes(StandardCharsets.UTF_8));
        cloudRepository.uploadFile(filename2, file2);
        // when
        List<FileInfo> files1 = cloudRepository.getUserFileList(1);
        List<FileInfo> files2 = cloudRepository.getUserFileList(2);
        // then
        Assertions.assertEquals(size1, files1.size());
        Assertions.assertEquals(size2, files2.size());
    }

}