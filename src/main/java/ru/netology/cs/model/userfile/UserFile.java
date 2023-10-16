package ru.netology.cs.model.userfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netology.cs.model.user.User;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class UserFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    private FileInfo fileInfo;
    private LocalDate uploadDate;
    @Lob
    private byte[] content;
    @ManyToOne
    private User user;

}