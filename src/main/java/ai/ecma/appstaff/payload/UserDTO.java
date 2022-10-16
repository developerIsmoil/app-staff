package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private boolean admin;
    private List<String> permissions;
    private String photoId;
    private Long roleId;
    private List<Long> roleIdList;

    public UserDTO(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserDTO(UUID id, String firstName, String lastName, String phoneNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
