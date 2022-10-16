package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OneDayTurniketDTO {
    @NotNull
    private UUID employeeId;

    @NotNull
    private String date;

    public Date getDate() {
        return Date.valueOf(date);
    }
}
