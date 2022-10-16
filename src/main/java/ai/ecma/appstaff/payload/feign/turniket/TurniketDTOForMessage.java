package ai.ecma.appstaff.payload.feign.turniket;

import ai.ecma.appstaff.enums.UserInOutEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TurniketDTOForMessage implements Serializable {

    private String turniketId;

    private UserInOutEnum status;

    private Long timeStamp;

    public Integer getTurniketId() {
        return Integer.parseInt(turniketId);
    }

    public Timestamp getTimeStamp() {
        return new Timestamp(timeStamp);
    }
}
