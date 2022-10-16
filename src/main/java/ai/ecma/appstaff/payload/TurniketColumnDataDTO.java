package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TurniketColumnDataDTO {

    private String name;

    private String arrivedTime;//KELGAN VAQTI

    private boolean isLate;//O'Z VAQTIDA KELDIMI

    private long timerSeconds;//HOZIRGACHA ISHXONADAGI SONIYALAR SONI

    private boolean timerOn;//TIMER ISHLASINMI

    private String strValue;//XODIM YOKI DEPARTMENT BO'LSA UNI NOMI SHU KEY BILAN KETADI

    private boolean inOffice;//USER OFFICEDAMI

    public TurniketColumnDataDTO(String name, String strValue) {
        this.name = name;
        this.strValue = strValue;
    }

    public TurniketColumnDataDTO(String name, String arrivedTime, boolean isLate, long timerSeconds, boolean timerOn, String strValue) {
        this.name = name;
        this.arrivedTime = arrivedTime;
        this.isLate = isLate;
        this.timerSeconds = timerSeconds;
        this.timerOn = timerOn;
        this.strValue = strValue;
    }
}
