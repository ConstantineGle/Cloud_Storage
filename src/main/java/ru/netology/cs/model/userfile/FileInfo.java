package ru.netology.cs.model.userfile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Embeddable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FileInfo {
    private String filename;
    private Long size;
}