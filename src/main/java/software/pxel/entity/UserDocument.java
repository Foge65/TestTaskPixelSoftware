package software.pxel.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.time.LocalDate;

@Document(indexName = "users")
@Getter
@Setter
public class UserDocument implements Serializable {
    @Id
    private String id;

    private String name;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
}
