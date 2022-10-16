package ai.ecma.appstaff.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeDTO {
    private String hour;
    private String minute;

    private OptionDTO<String> hours;
    private OptionDTO<String> minutes;

    public TimeDTO(OptionDTO<String> hours, OptionDTO<String> minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    public TimeDTO(String hour, String minute) {
        this.hour = hour;
        this.minute = minute;
    }
}
