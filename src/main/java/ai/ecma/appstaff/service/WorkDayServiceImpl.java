package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.WorkDay;
import ai.ecma.appstaff.enums.WeekDayEnum;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.EnumDTO;
import ai.ecma.appstaff.repository.WorkDayRepository;
  
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkDayServiceImpl   implements WorkDayService {


    private final WorkDayRepository workDayRepository;


    @Override
    public ApiResult<?> changeActive(UUID id) {

        Optional<WorkDay> optionalWorkDay = workDayRepository.findById(id);
        if (optionalWorkDay.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_WORK_DAY_NOT_FOUND);
        }
        WorkDay workDay = optionalWorkDay.get();

        boolean active = !workDay.isActive();
        workDay.setActive(active);

        try {
            workDayRepository.save(workDay);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_WORK_DAY_ACTIVE_CHANGING);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_WORK_DAY_ACTIVE_CHANGED);

    }

    @Override
    public ApiResult<?> getAll() {
        List<WorkDay> allByActiveTrue = workDayRepository.findAll(Sort.by("createdAt").ascending());
        List<EnumDTO> listEnumDtoFromWorkDayList = getListEnumDtoFromWorkDayList(allByActiveTrue);

        return ApiResult.successResponse(listEnumDtoFromWorkDayList);

    }

    @Override
    public ApiResult<?> getAllActive() {
        List<WorkDay> allByActiveTrue = workDayRepository.findAllByActiveTrue();
        List<EnumDTO> listEnumDtoFromWorkDayList = getListEnumDtoFromWorkDayList(allByActiveTrue);

        return ApiResult.successResponse(listEnumDtoFromWorkDayList);
    }

    @Override
    public void saveAllWeekDaysToWorkDay() {

        List<WorkDay> workDayList = new ArrayList<>();

        workDayList.add(
                new WorkDay(
                        WeekDayEnum.MONDAY,
                        false,
                        1
                ));
        workDayList.add(
                new WorkDay(
                        WeekDayEnum.TUESDAY,
                        false,
                        2
                ));
        workDayList.add(
                new WorkDay(
                        WeekDayEnum.WEDNESDAY,
                        false,
                        3
                ));
        workDayList.add(
                new WorkDay(
                        WeekDayEnum.THURSDAY,
                        false,
                        4
                ));
        workDayList.add(
                new WorkDay(
                        WeekDayEnum.FRIDAY,
                        false,
                        5
                ));
        workDayList.add(
                new WorkDay(
                        WeekDayEnum.SATURDAY,
                        false,
                        6
                ));
        workDayList.add(
                new WorkDay(
                        WeekDayEnum.SUNDAY,
                        false,
                        7
                ));

        workDayRepository.saveAll(workDayList);

    }

    private EnumDTO getEnumDtoFromWorkDay(WorkDay workDay) {
        return new EnumDTO(
                workDay.getWeekDay().toString(),
                workDay.getId(),
                workDay.isActive()
        );
    }

    private List<EnumDTO> getListEnumDtoFromWorkDayList(List<WorkDay> workDayList) {
        return workDayList
                .stream()
                .map(this::getEnumDtoFromWorkDay)
                .collect(Collectors.toList());
    }
}
