package ai.ecma.appstaff.payload;

import ai.ecma.appstaff.entity.Holiday;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HolidayDTO {

    private UUID id;

    private String name;

    private boolean active;

    private boolean calcMonthlySalary;

    private Set<Long> dates;

    public static HolidayDTO fromHolidayForSelect(Holiday holiday) {
        return new HolidayDTO(
                holiday.getId(),
                holiday.getName()
        );
    }

    public static HolidayDTO fromHoliday(Holiday holiday) {
        return new HolidayDTO(
                holiday.getId(),
                holiday.getName(),
                holiday.isActive(),
                holiday.isCalcMonthlySalary(),
                holiday.getDateList()
                        .stream()
                        .map(Date::getTime)
                        .collect(Collectors.toSet())
        );
    }

    public HolidayDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
