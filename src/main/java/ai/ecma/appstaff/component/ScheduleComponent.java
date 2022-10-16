package ai.ecma.appstaff.component;

import ai.ecma.appstaff.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ScheduleComponent {

    private final TimesheetService timeSheetService;

    // THAT SHOULD EXECUTE ON 1ST DAY EVERY MONTH 00:00
    @Scheduled(cron = "0 0 0 1 1/1 *")
    public void everyMonthFirstDay() {

        // TIMESHEET YARATISH UCHUN
        timeSheetService.createTimeSheet();
    }
}
