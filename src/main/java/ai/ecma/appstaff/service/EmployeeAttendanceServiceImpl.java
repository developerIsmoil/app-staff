package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.*;
import ai.ecma.appstaff.enums.AttendanceEnum;
import ai.ecma.appstaff.entity.template.AbsUUIDUserAuditEntity;
import ai.ecma.appstaff.enums.PaymentCriteriaTypeEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.EmployeeAttendanceDTO;
import ai.ecma.appstaff.payload.TariffGridDTO;
import ai.ecma.appstaff.projection.IEmployeeAttendance;
import ai.ecma.appstaff.repository.EmployeeAttendanceRepository;
import ai.ecma.appstaff.repository.TimeSheetEmployeeRepository;

import ai.ecma.appstaff.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeAttendanceServiceImpl implements EmployeeAttendanceService {

    private final EmployeeAttendanceRepository employeeAttendanceRepository;
    private final TimeSheetEmployeeRepository timeSheetEmployeeRepository;
    private final TariffGridService tariffGridService;

    @Autowired
    public EmployeeAttendanceServiceImpl(
            EmployeeAttendanceRepository employeeAttendanceRepository,
            TimeSheetEmployeeRepository timeSheetEmployeeRepository,
            @Lazy TariffGridService tariffGridService) {
        this.employeeAttendanceRepository = employeeAttendanceRepository;
        this.timeSheetEmployeeRepository = timeSheetEmployeeRepository;
        this.tariffGridService = tariffGridService;
    }

    @Override
    public void changeEmployeeAttendance(EmployeeAttendanceDTO employeeAttendanceDTO) {

        EmployeeAttendance employeeAttendance = getEmployeeAttendanceFromDBByTimesheetEmployeeIdAndDay(
                employeeAttendanceDTO.getTimesheetEmployeeId(),
                employeeAttendanceDTO.getDay());

        if (Objects.nonNull(employeeAttendanceDTO.getWorkHour())) {
            employeeAttendance.setAttendance(AttendanceEnum.WORKING);

            employeeAttendance.setWorkHour(employeeAttendanceDTO.getWorkHour());
        }

        if (Objects.nonNull(employeeAttendanceDTO.getAttendance())) {
            employeeAttendance.setWorkHour(0d);

            employeeAttendance.setAttendance(employeeAttendanceDTO.getAttendance());

        }

        employeeAttendanceRepository.save(employeeAttendance);

        reCalculateTimeSheetEmployeeWorkedHourAndWorkedDay(employeeAttendance);

    }


    public void reCalculateTimeSheetEmployeeWorkedHourAndWorkedDay(EmployeeAttendance employeeAttendance) {

        TimeSheetEmployee timeSheetEmployee = employeeAttendance.getTimeSheetEmployee();


        EmploymentInfo employmentInfo = timeSheetEmployee.getEmploymentInfo();

        PaymentCriteriaTypeEnum paymentCriteriaType = employmentInfo.getPaymentCriteriaType();

        IEmployeeAttendance iEmployeeAttendances =
                employeeAttendanceRepository.findTimeSheetEmployeeByIdWorkdayAndWorkHour(
                        timeSheetEmployee.getId().toString(),
                        AttendanceEnum.WORKING.name()
                );

        timeSheetEmployee.setWorkedDays(iEmployeeAttendances.getWorkDay());
        timeSheetEmployee.setWorkedHours(iEmployeeAttendances.getWorkHour());

        if (PaymentCriteriaTypeEnum.WORK.equals(paymentCriteriaType)) {
// todo o'ylaaa
        }
        if (PaymentCriteriaTypeEnum.HOUR.equals(paymentCriteriaType)) {
//
            TariffGridDTO tariffGridDTO = new TariffGridDTO(
                    employmentInfo.getBranchId(),
                    employmentInfo.getCompanyId(),
                    employmentInfo.getDepartment().getId(),
                    employmentInfo.getPosition().getId(),
                    employmentInfo.getEmployeeCategory().getId(),
                    employmentInfo.getPaymentCriteriaType()
            );

            TariffGrid oneTariffGrid = tariffGridService.getOneTariffGrid(tariffGridDTO);

            Double paymentAmount = oneTariffGrid.getPaymentAmount();
            Integer workHour = iEmployeeAttendances.getWorkHour();

            double totalSalary = (Objects.isNull(paymentAmount) ? 0 : paymentAmount) * (Objects.isNull(workHour) ? 0 : workHour);

            timeSheetEmployee.setSalary(totalSalary);
        }

        timeSheetEmployeeRepository.save(timeSheetEmployee);

    }

    @Override
    public void calculateTimeSheetEmployeeWorkedHourAndWorkedDay(List<TimeSheet> timeSheetList) {

        List<UUID> timeSheetIdList = timeSheetList
                .stream()
                .map(AbsUUIDUserAuditEntity::getId)
                .collect(Collectors.toList());

        List<IEmployeeAttendance> iEmployeeAttendanceList =
                employeeAttendanceRepository.findAllTimeSheetEmployeeWorkdayAndWorkHour(timeSheetIdList);

        List<UUID> uuidList = iEmployeeAttendanceList
                .stream()
                .map(iEmployeeAttendance -> UUID.fromString(iEmployeeAttendance.getTimeSheetEmployeeId()))
                .collect(Collectors.toList());

        List<TimeSheetEmployee> timeSheetEmployeeList = timeSheetEmployeeRepository.findAllById(uuidList);

        for (TimeSheetEmployee timeSheetEmployee : timeSheetEmployeeList) {

            for (IEmployeeAttendance iEmployeeAttendance : iEmployeeAttendanceList) {

                if (Objects.equals(timeSheetEmployee.getId(), UUID.fromString(iEmployeeAttendance.getTimeSheetEmployeeId()))) {

                    // bu faqat bir marta ishlaydi.
                    // ya'ni qancha ishlashi kerak edi degan ma'lumot
                    timeSheetEmployee.setWorkDays(iEmployeeAttendance.getWorkDay());
                    timeSheetEmployee.setWorkHours(iEmployeeAttendance.getWorkHour());


                    // bu keyinchalik o'zgarishi mumkin. timesheetdan columnlarni edit qilganda o'zgaradi
                    // ya'ni qancha ishladi degan ma'lumot
                    timeSheetEmployee.setWorkedDays(iEmployeeAttendance.getWorkDay());
                    timeSheetEmployee.setWorkedHours(iEmployeeAttendance.getWorkHour());
                }
            }
        }

        timeSheetEmployeeRepository.saveAll(timeSheetEmployeeList);
    }

    @Override
    public void calculateTimeSheetEmployeeWorkedHourAndWorkedDayAndPayment(List<TimeSheet> timeSheetList, List<TariffGrid> tariffGridList) {


        List<UUID> timeSheetIdList = timeSheetList
                .stream()
                .map(AbsUUIDUserAuditEntity::getId)
                .collect(Collectors.toList());

        List<IEmployeeAttendance> iEmployeeAttendanceList =
                employeeAttendanceRepository.findAllTimeSheetEmployeeWorkdayAndWorkHour(timeSheetIdList);

        List<UUID> uuidList = iEmployeeAttendanceList
                .stream()
                .map(iEmployeeAttendance -> UUID.fromString(iEmployeeAttendance.getTimeSheetEmployeeId()))
                .collect(Collectors.toList());

        List<TimeSheetEmployee> timeSheetEmployeeList = timeSheetEmployeeRepository.findAllById(uuidList);

        for (TimeSheetEmployee timeSheetEmployee : timeSheetEmployeeList) {

            for (IEmployeeAttendance iEmployeeAttendance : iEmployeeAttendanceList) {

                if (Objects.equals(timeSheetEmployee.getId(), UUID.fromString(iEmployeeAttendance.getTimeSheetEmployeeId()))) {

                    EmploymentInfo employmentInfo = timeSheetEmployee.getEmploymentInfo();

                    for (TariffGrid tariffGrid : tariffGridList) {
                        if (Objects.equals(employmentInfo.getDepartment().getId(), tariffGrid.getDepartment().getId())
                                && Objects.equals(employmentInfo.getPosition().getId(), tariffGrid.getPosition().getId())

                                && ((Objects.isNull(employmentInfo.getCompanyId()) && Objects.isNull(tariffGrid.getCompanyId()))
                                || Objects.equals(employmentInfo.getCompanyId(), tariffGrid.getCompanyId()))

                                && ((Objects.isNull(employmentInfo.getBranchId()) && Objects.isNull(tariffGrid.getBranchId()))
                                || Objects.equals(employmentInfo.getBranchId(), tariffGrid.getBranchId()))

                                && Objects.equals(employmentInfo.getEmployeeCategory().getId(), tariffGrid.getEmployeeCategory().getId())
                                && Objects.equals(employmentInfo.getPaymentCriteriaType(), tariffGrid.getPaymentCriteriaType())) {

                            // bu faqat bir marta ishlaydi.
                            // ya'ni qancha ishlashi kerak edi degan ma'lumot
                            timeSheetEmployee.setWorkDays(iEmployeeAttendance.getWorkDay());
                            timeSheetEmployee.setWorkHours(iEmployeeAttendance.getWorkHour());


                            // bu keyinchalik o'zgarishi mumkin. timesheetdan columnlarni edit qilganda o'zgaradi
                            // ya'ni qancha ishladi degan ma'lumot
                            timeSheetEmployee.setWorkedDays(iEmployeeAttendance.getWorkDay());
                            timeSheetEmployee.setWorkedHours(iEmployeeAttendance.getWorkHour());

                            if (Objects.equals(tariffGrid.getPaymentCriteriaType(), PaymentCriteriaTypeEnum.HOUR)) {

                                Double paymentAmount = tariffGrid.getPaymentAmount();
                                Integer workHour = timeSheetEmployee.getWorkHours();

                                double totalSalary = (Objects.isNull(paymentAmount) ? 0 : paymentAmount) * (Objects.isNull(workHour) ? 0 : workHour);

                                timeSheetEmployee.setSalary(totalSalary);
                            }
                            if (Objects.equals(tariffGrid.getPaymentCriteriaType(), PaymentCriteriaTypeEnum.WORK)) {

                                timeSheetEmployee.setSalary(tariffGrid.getPaymentAmount());
                            }
                        }
                    }
                }
            }
        }

        timeSheetEmployeeRepository.saveAll(timeSheetEmployeeList);

    }

    @Override
    public List<IEmployeeAttendance> getAllByTimesheetEmployeeId(List<String> idList) {

        List<UUID> uuidList = idList
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());

        return employeeAttendanceRepository.findAllByTimeSheetEmployeeIdIn(uuidList);
    }


    private EmployeeAttendance getEmployeeAttendanceFromDBByTimesheetEmployeeIdAndDay(UUID timesheetEmployeeId, Date day) {

        Optional<EmployeeAttendance> optionalEmployeeAttendance =
                employeeAttendanceRepository.findByTimeSheetEmployeeIdAndDay(
                        timesheetEmployeeId,
                        day
                );

        if (optionalEmployeeAttendance.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_EMPLOYMENT_INFO_NOT_FOUND);
        }

        return optionalEmployeeAttendance.get();
    }
}
