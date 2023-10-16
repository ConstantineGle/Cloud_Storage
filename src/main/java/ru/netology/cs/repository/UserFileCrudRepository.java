package ru.netology.cs.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.netology.cs.model.userfile.FileInfo;
import ru.netology.cs.model.userfile.UserFile;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFileCrudRepository extends JpaRepository<UserFile, Long> {

    @Query("select f.id from UserFile f where f.fileInfo.filename = :filename and f.user.login = :username")
    Optional<Long> getIdUserFileByName(@Param("filename") String filename, @Param("username") String username);

    int countByIdAndUserLogin(long fileId, String username);

    int countByFileInfoFilenameAndUserId(String filename, long userId);

    Optional<UserFile> findByFileInfoFilenameAndUserLogin(String filename, String username);

    @Modifying
    @Query(value = "update UserFile f set f.fileInfo.filename = :name where f.id = :id")
    int updateFileNameById(@Param("name") String name, @Param("id") long id);

    @Query("select f from UserFile f where f.user.login = :username")
    List<UserFile> getUserFileList( @Param("username") String username);

    @Query(value = "select f.fileInfo from UserFile f where f.user.login = ?1")
    List<FileInfo> findAllUserFiles(String username, PageRequest pageable);

}