package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.enums.UserInOutEnum;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInOutDTO {

    private UUID id;

    private String time;

    private UserInOutEnum status;


    public UserInOutDTO(UUID id, Timestamp time, UserInOutEnum status) {
        this.id = id;
        this.time = Objects.nonNull(time) ? time.toLocalDateTime().toLocalTime().format(RestConstants.TIME_FORMATTER) : null;
        this.status = status;
    }
}
