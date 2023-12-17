package hexlet.code.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Getter
@Setter
@ToString
public class Url {
    private Long id;

    @ToString.Include
    private String name;
    private Date createdAt;

    public Url(String name, Date createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }
}
