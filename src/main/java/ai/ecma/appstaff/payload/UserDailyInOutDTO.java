package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.utils.RestConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDailyInOutDTO {

    private EmployeeTurnikteInfoDTO employee;

    private List<UserInOutDTO> inOutHistory;

    private boolean isLate;//KECHIKKANMI

    private long workingTimes;//QANCHA BINODA BO'LGAN SONIYADA

    private String firstEnterTimeStamp;//ISHXONAGA 1-KECHIKMAY KELGAN VAQTI
    private String lastExitTimeStamp;//ISHXONAGA 1-KECHIKMAY KELGAN VAQTI

    private String date;//QAYSI KUN UCHUN

    private Long dateLong;

    private boolean inOffice;//OFFICEDAMI SHU ISHCHI

    private boolean timerOn;

    public boolean isTimerOn() {
        return inOffice;
    }


    public UserDailyInOutDTO(EmployeeTurnikteInfoDTO employee, List<UserInOutDTO> inOutHistory, boolean isLate, long workingTimes, Timestamp firstEnterTimeStamp, Timestamp lastExitTimeStamp, String date, Date dateLong, boolean inOffice) {
        this.employee = employee;
        this.inOutHistory = inOutHistory;
        this.isLate = isLate;
        this.workingTimes = workingTimes;
        this.firstEnterTimeStamp = Objects.nonNull(firstEnterTimeStamp) ? firstEnterTimeStamp.toLocalDateTime().toLocalTime().format(RestConstants.TIME_FORMATTER) : null;
        this.lastExitTimeStamp = Objects.nonNull(lastExitTimeStamp) ? lastExitTimeStamp.toLocalDateTime().toLocalTime().format(RestConstants.TIME_FORMATTER) : null;
        this.date = date;
        this.dateLong = Objects.nonNull(dateLong) ? dateLong.getTime() : null;
        this.inOffice = inOffice;
    }
}
