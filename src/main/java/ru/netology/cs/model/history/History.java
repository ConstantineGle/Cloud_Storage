package ru.netology.cs.model.history;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netology.cs.model.user.User;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDate uploadDate;
    @ManyToOne
    private User user;
    private String description;
}
