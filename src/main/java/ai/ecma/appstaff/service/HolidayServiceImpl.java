package ai.ecma.appstaff.service;

import ai.ecma.appstaff.entity.Holiday;
import ai.ecma.appstaff.exceptions.RestException;
import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.HolidayDTO;
import ai.ecma.appstaff.repository.HolidayRepository;
  
import ai.ecma.appstaff.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HolidayServiceImpl   implements HolidayService {

    private final HolidayRepository holidayRepository;

    /**
     * Holiday qo'shish
     *
     * @param holidayDTO a
     * @return a
     */
    @Override
    public ApiResult<HolidayDTO> addHoliday(HolidayDTO holidayDTO) {
        // log.info("class HolidayServiceImpl => addHoliday => method entered => DTO : {}", holidayDTO);

        checkHolidayExist(holidayDTO, Optional.empty());

        // HOLIDAYNI YARATIB OLISH
        Holiday newHoliday = makeHoliday(new Holiday(), holidayDTO);

        try {
            // YANGI BO'LIMNI DATABASEGA SAQLAYAPMIZ
            holidayRepository.save(newHoliday);
        } catch (Exception e) {
            // log.info("class HolidayServiceImpl => addHoliday => error saving holiday");
            throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_SAVING);
        }
        return ApiResult.successResponse(HolidayDTO.fromHoliday(newHoliday), ResponseMessage.SUCCESS_HOLIDAY_SAVED);

    }

    /**
     * Holidayni ID bo'yicha tahrirlash
     *
     * @param id         a
     * @param holidayDTO a a
     * @return a
     */
    @Override
    @Transactional
    public ApiResult<HolidayDTO> editHoliday(UUID id, HolidayDTO holidayDTO) {
        // log.info("class HolidayServiceImpl => editHoliday => method entered => ID : {} DTO : {}", id, holidayDTO);

        checkHolidayExist(holidayDTO, Optional.of(id));

        // ID BO'YICHA DATABASEDAN HOLIDAYNI OLAMIZ
        Holiday holidayFromDB = getHolidayFromDB(id, false);

        // LOGGA YOZISH UCHUN
//        // createHistoryLog(holidayFromDB, holidayDTO);

        // DTO DA KELGAN MA'LUMOTLARNI DATABASEDAN OLGAN HOLIDAYIMIZGA SET QILAMIZ
        Holiday holiday = makeHoliday(holidayFromDB, holidayDTO);

        try {
            // HOLIDAYNI DATABASEGA SAQLAYMIZ
            holidayRepository.save(holiday);
        } catch (Exception e) {
            // log.info("class HolidayServiceImpl => editHoliday => error saving holiday");
            throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_EDITING);
        }
        return ApiResult.successResponse(HolidayDTO.fromHoliday(holiday), ResponseMessage.SUCCESS_HOLIDAY_EDITED);
    }

    /**
     * Tizimdagi barcha holidaylarni olish uchun
     *
     * @param page a
     * @param size a a
     * @return a
     */
    @Override
    public ApiResult<List<HolidayDTO>> getAllHoliday(Integer page, Integer size) {
        // log.info("class HolidayServiceImpl => getAllHoliday => method entered => PAGE : {} SIZE : {} ", page, size);

        List<Holiday> holidayList = holidayRepository.findAll(Sort.by(Sort.Direction.DESC,"createdAt"));
        return ApiResult.successResponse(getHolidayDTOList(holidayList));
    }

    @Override
    public ApiResult<List<HolidayDTO>> getAllHolidayForSelect(Integer page, Integer size) {
        // log.info("class HolidayServiceImpl => getAllHolidayForSelect => method entered => PAGE : {} SIZE : {} ", page, size);

        List<Holiday> holidayList = holidayRepository.findAllByActiveTrue();
        return ApiResult.successResponse(getHolidayDTOListForSelect(holidayList));
    }

    /**
     * ID bo'yicha bitta holidayni olish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<HolidayDTO> getOneHoliday(UUID id) {
        // log.info("class HolidayServiceImpl => getOneHoliday => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN HOLIDAYNI OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        Holiday holidayFromDB = getHolidayFromDB(id, false);

        return ApiResult.successResponse(HolidayDTO.fromHoliday(holidayFromDB));

    }

    /**
     * ID bo'yicha holidayni o'chirish
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> deleteHoliday(UUID id) {
        // log.info("class HolidayServiceImpl => deleteHoliday => method entered => ID : {} ", id);

        try {
            holidayRepository.deleteById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_HOLIDAY_DELETED);
    }

    @Override
    public ApiResult<?> deleteHolidayByIdList(List<UUID> id) {
        // log.info("class HolidayServiceImpl => deleteHolidayByIdList => method entered => ID : {} ", id);

        try {
            holidayRepository.deleteAllById(id);
        } catch (Exception e) {
            throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_NOT_FOUND);
        }
        return ApiResult.successResponse(ResponseMessage.SUCCESS_HOLIDAY_DELETED);

    }

    /**
     * Holiday holatini o'zgartirish uchun
     *
     * @param id a
     * @return a
     */
    @Override
    public ApiResult<?> changeActiveHoliday(UUID id) {
        // log.info("class HolidayServiceImpl => changeActiveHoliday => method entered => ID : {} ", id);

        // DATABASEDAN BERILGAN ID GA TENG BO'LGAN HOLIDAYNI OLADI.
        //AGAR TOPA OLMASA XATOLIKKA TUSHADI
        Holiday holidayFromDB = getHolidayFromDB(id, false);

        // DATABASEDAN OLINGAN HOLIDAYNI HOLATINI TESKARI KO'RINISHGA O'TKAZISH (TRUE => FALSE) (FALSE => TRUE)
        boolean changedActive = !holidayFromDB.isActive();
        holidayFromDB.setActive(changedActive);

        // HOLATI O'ZGARGAN HOLIDAYNI DATABASEGA SAQLASH
        // log.info("class HolidayServiceImpl => changeActiveHoliday => changed active => ACTIVE : {} ", changedActive);
        holidayRepository.save(holidayFromDB);
        return ApiResult.successResponse(ResponseMessage.SUCCESS_HOLIDAY_STATUS_CHANGE);

    }

    @Override
    public List<HolidayDTO> getAllActiveHolidayFromDB() {

        List<Holiday> holidayList = holidayRepository.findAllByActiveTrue();
        return holidayList
                .stream()
                .map(HolidayDTO::fromHolidayForSelect)
                .collect(Collectors.toList());
    }

    /**
     * Holidayni ID orqali databsedan olish uchun.
     * Databasedan holidayni ololmasa xatolikka tushadi
     *
     * @param id a
     * @return a
     */
    @Override
    public Holiday getHolidayFromDB(UUID id, boolean onlyActive) {
        // AGAR ID NULL BO'LSA XATOLIK QAYTARGANMIZ
        if (Objects.isNull(id)) {
            throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_NOT_FOUND);
        }
        Optional<Holiday> optionalHoliday = holidayRepository.findById(id);

        // SHU ID LIK HOLIDAY MAVJUD BO'LSA UNI RETURN QILGANMIZ. AKS HOLDA XATOLIK QAYTARGANMIZ
        if (optionalHoliday.isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_NOT_FOUND);
        }

        Holiday holiday = optionalHoliday.get();

        // FAQAT ACTIVE = TRUE BO'LGAN HOLIDAYNI OLISH UCHUN
        if (onlyActive) {
            if (!holiday.isActive()) {
                throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_NOT_ACTIVE);
            }
        }
        return holiday;
    }

    private void checkHolidayExist(HolidayDTO holidayDTO, Optional<UUID> optionalId) {

        boolean exist;

        if (optionalId.isEmpty()) {

            //SHU NOMLI BO'LIM DATABASEDA BOR YOKI YO'QLIGI TEKSHIRILGAN
            exist = holidayRepository.existsByName(holidayDTO.getName());

        } else {

            //SHU NOMLI VA BERILGAN ID DAN BOSHQA BO'LIM DATABASEDA BOR YOKI YO'QLIGI TEKSHIRILGAN
            exist = holidayRepository.existsByNameAndIdNot(holidayDTO.getName(), optionalId.get());

        }

        if (exist) {

            // AGAR MAVJUD BO'LSA XATOLIK QAYTARAMIZ
            throw RestException.restThrow(ResponseMessage.ERROR_HOLIDAY_ALREADY_EXIST);

        }
    }

    /**
     * HolidayDto orqali Holiday yaratib olish uchun
     *
     * @param holiday    a
     * @param holidayDTO a a
     * @return a
     */
    private Holiday makeHoliday(Holiday holiday, HolidayDTO holidayDTO) {

        if (Objects.nonNull(holidayDTO.getName()))
            holiday.setName(holidayDTO.getName());

        holiday.setCalcMonthlySalary(holidayDTO.isCalcMonthlySalary());

        if (Objects.nonNull(holidayDTO.getDates()) && holidayDTO.getDates().isEmpty()) {
            throw RestException.restThrow(ResponseMessage.ERROR_MINIMUM_ONE_DATE_REQUIRED);
        }

        Set<Date> dates = holidayDTO.getDates()
                .stream()
                .filter(Objects::nonNull)
                .map(Date::new)
                .collect(Collectors.toSet());

        holiday.setDateList(dates);

        holiday.setActive(holidayDTO.isActive());

        return holiday;
    }

    /**
     * Holidaylar LISTidan HolidayDto lar LISTini olish uchun
     *
     * @param holidayList a
     * @return a
     */
    private List<HolidayDTO> getHolidayDTOList(List<Holiday> holidayList) {
        return holidayList.stream().map(HolidayDTO::fromHoliday).collect(Collectors.toList());
    }

    /**
     * Holidaylar LISTidan HolidayDto lar LISTini olish uchun
     *
     * @param holidayList a
     * @return a
     */
    private List<HolidayDTO> getHolidayDTOListForSelect(List<Holiday> holidayList) {
        return holidayList.stream().map(HolidayDTO::fromHolidayForSelect).collect(Collectors.toList());
    }


//    @Transactional
//    public void // createHistoryLog(Holiday holiday, HolidayDTO holidayDTO) {
//
//        List<HistoryLog> historyLogList = new ArrayList<>();
//
//        if (!holiday.getName().equals(holidayDTO.getName())) {
//            historyLogList.add(
//                    new HistoryLog(
//                            EntityFieldNameEnum.HOLIDAY_NAME,
//                            holiday.getName(),
//                            holidayDTO.getName(),
//                            EntityNameEnum.HOLIDAY
//                    )
//            );
//        }
//
//        if (holiday.isActive() != holidayDTO.getActive()) {
//            historyLogList.add(
//                    new HistoryLog(
//                            EntityFieldNameEnum.HOLIDAY_ACTIVE,
//                            holiday.isActive() + "",
//                            holidayDTO.getName() + "",
//                            EntityNameEnum.HOLIDAY
//                    )
//            );
//        }
//
//        historyLogRepository.saveAll(historyLogList);
//    }
}
