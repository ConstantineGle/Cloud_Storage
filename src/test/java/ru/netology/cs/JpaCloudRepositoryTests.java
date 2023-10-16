package ru.netology.cs;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cs.model.user.User;
import ru.netology.cs.model.userfile.FileInfo;
import ru.netology.cs.model.userfile.UserFile;
import ru.netology.cs.repository.UserCrudRepository;
import ru.netology.cs.repository.UserFileCrudRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Testcontainers
public class JpaCloudRepositoryTests {
    private final static String filename = "test1.txt";
    private final static String filename2 = "test2.txt";
    private final static String filename3 = "test3.txt";
    private final static String text = "test content";
    private final static String username = "test";
    private final static List<Long> filesId = new ArrayList<>();

    @Autowired
    private UserFileCrudRepository userFileCrudRepository;

    @Autowired
    private UserCrudRepository userCrudRepository;

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

    private void addFile(String name, String login) {
        User user = userCrudRepository.findByLogin(login)
                .orElseThrow(()->new RuntimeException("user not found"));
        UserFile userFile = UserFile.builder()
                .fileInfo(new FileInfo(name, 10L))
                .content(text.getBytes())
                .uploadDate(LocalDate.now())
                .user(user)
                .build();
        filesId.add(userFileCrudRepository.save(userFile).getId());
    }

    @BeforeEach
    void init() {
        addFile(filename, username);
        addFile(filename2, username);
    }

    @Test
    void test_userCrudRepository_findByLogin() {
        Optional<User> user = userCrudRepository.findByLogin("test");
        Assertions.assertTrue(user.isPresent());
    }

    @Test
    void test_userCrudRepository_findByLogin_Null() {
        Optional<User> user = userCrudRepository.findByLogin("test555");
        Assertions.assertFalse(user.isPresent());
    }

    @Test
    void test_userFileCrudRepository_getIdUserFileByName() {
        Optional<Long> id = userFileCrudRepository.getIdUserFileByName(filename, username);
        Assertions.assertTrue(id.isPresent());
    }

    @Test
    void test_userFileCrudRepository_countByIdAndUserLogin() {
        // given
        long id = userFileCrudRepository.getIdUserFileByName(filename, username)
                .orElseThrow(()->new RuntimeException("file not found"));
        int expect = 1;
        // when
        int cnt = userFileCrudRepository.countByIdAndUserLogin(id, username);
        // then
        Assertions.assertEquals(expect, cnt);
    }

    @Test
    void test_userFileCrudRepository_countByFileInfoFilenameAndUserId() {
        // given
        User user = userCrudRepository.findByLogin(username)
                .orElseThrow(()->new RuntimeException("user not found"));
        int expect = 1;
        // when
        int cnt = userFileCrudRepository.countByFileInfoFilenameAndUserId(filename, user.getId());
        // then
        Assertions.assertEquals(expect, cnt);
    }

    @Test
    @Transactional
    void test_userFileCrudRepository_findByFileInfoFilenameAndUserLogin() {
        // given
        int expect = 1;
        // when
        Optional<UserFile> userFile = userFileCrudRepository.findByFileInfoFilenameAndUserLogin(filename, username);
        // then
        Assertions.assertTrue(userFile.isPresent());
    }

    @Test
    @Transactional
    void test_userFileCrudRepository_updateFileNameById() {
        // given
        long id = userFileCrudRepository.getIdUserFileByName(filename, username)
                .orElseThrow(()->new RuntimeException("file not found"));
        int expect = 1;
        // when
        int cnt = userFileCrudRepository.updateFileNameById(filename3, id);
        // then
        Assertions.assertEquals(expect, cnt);
    }

    @Test
    @Transactional
    void test_userFileCrudRepository_getUserFileList() {
        // given
        long id = userFileCrudRepository.getIdUserFileByName(filename, username)
                .orElseThrow(()->new RuntimeException("file not found"));
        int expect = 2;
        // when
        List<UserFile> files = userFileCrudRepository.getUserFileList(username);
        // then
        Assertions.assertEquals(expect, files.size());
    }

    @Test
    void test_userFileCrudRepository_findAllUserFiles_limit5() {
        // given
        int limit = 5;
        long id = userFileCrudRepository.getIdUserFileByName(filename, username)
                .orElseThrow(()->new RuntimeException("file not found"));
        int expect = 2;
        // when
        PageRequest paging = PageRequest.of(0, limit, Sort.by("fileInfo.filename"));
        List<FileInfo> files = userFileCrudRepository.findAllUserFiles(username, paging);
        // then
        Assertions.assertEquals(expect, files.size());
    }

    @Test
    void test_userFileCrudRepository_findAllUserFiles_limit1() {
        // given
        int limit = 1;
        long id = userFileCrudRepository.getIdUserFileByName(filename, username)
                .orElseThrow(()->new RuntimeException("file not found"));
        int expect = 1;
        // when
        PageRequest paging = PageRequest.of(0, limit, Sort.by("fileInfo.filename"));
        List<FileInfo> files = userFileCrudRepository.findAllUserFiles(username, paging);
        // then
        Assertions.assertEquals(expect, files.size());
    }

    @AfterEach
    void clean() {
        userFileCrudRepository.deleteAllById(filesId);
        filesId.clear();
    }

}