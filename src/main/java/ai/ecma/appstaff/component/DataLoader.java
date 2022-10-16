package ai.ecma.appstaff.component;

import ai.ecma.appstaff.enums.ViewTypeEnum;
import ai.ecma.appstaff.repository.EmployeeRepository;
import ai.ecma.appstaff.repository.view.ViewObjectRepository;
import ai.ecma.appstaff.service.WorkDayService;
import ai.ecma.appstaff.service.init.InitService;
import ai.ecma.appstaff.service.view.ViewService;
import ai.ecma.appstaff.utils.CommonUtils;
import ai.ecma.appstaff.utils.RestConstants;
import ai.ecma.appstaff.utils.TableMapList;
import ai.ecma.appstaff.utils.TableNameConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {


    @Value("${spring.profiles.active}")
    String profile;

    @Value("${spring.sql.init.mode}")
    String initMode;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    String hibernateDdlAuto;

    private final WorkDayService workDayService;
    private final ViewService viewService;
    private final InitService initService;
    private final Environment environment;

    @Override
    public void run(String... args) {

        setAttachmentPath();

        initService.savePageAndPermissionToAuthService();

//        viewService.createDefaultView(false, true, "Default View For Tariff Grid", ViewTypeEnum.TABLE, RestConstants.TARIFF_GRID, TableMapList.ENTITY_FIELDS.get(RestConstants.TARIFF_GRID), Optional.empty());

        if (initMode.equals("always")) {

            if (profile.equals("prod") && hibernateDdlAuto.equals("create")) {
                System.err.println("You can not run program");
                System.exit(1);
            }
//
            if (profile.equals("dev")) {

                // DEV uchun
                // hodim qo'shish uchun kerakli ma'lumotlarni qo'shib beradi
                initService.createElements();
//
            }

            workDayService.saveAllWeekDaysToWorkDay();


            //============ DEFAULT VIEW LAR OCHISH START<<<<<<<<<<=======
            viewService.createDefaultView(false, true, "Default View For Tariff Grid", ViewTypeEnum.TABLE, TableNameConstant.TARIFF_GRID, TableMapList.ENTITY_FIELDS.get(TableNameConstant.TARIFF_GRID), Optional.empty());
            viewService.createDefaultView(false, true, "Default", ViewTypeEnum.TABLE, TableNameConstant.EMPLOYEE, TableMapList.ENTITY_FIELDS.get(TableNameConstant.EMPLOYEE), Optional.empty());
            viewService.createDefaultView(false, true, "Default", ViewTypeEnum.TABLE, TableNameConstant.TIMESHEET_EMPLOYEE, TableMapList.ENTITY_FIELDS.get(TableNameConstant.TIMESHEET_EMPLOYEE), Optional.empty());
            viewService.createDefaultView(false, true, "Default", ViewTypeEnum.TABLE, TableNameConstant.TIMESHEET_EMPLOYEE_FOR_FINANCE, TableMapList.ENTITY_FIELDS.get(TableNameConstant.TIMESHEET_EMPLOYEE_FOR_FINANCE), Optional.empty());
            viewService.createDefaultView(false, true, "Default", ViewTypeEnum.TABLE, TableNameConstant.MENTOR, TableMapList.ENTITY_FIELDS.get(TableNameConstant.MENTOR), Optional.empty());
            //============ DEFAULT VIEW LAR OCHISH END<<<<<<<<<<=========
        }

//        viewService.createDefaultView(false, true, "Default", ViewTypeEnum.TABLE, TableNameConstant.MENTOR, TableMapList.ENTITY_FIELDS.get(TableNameConstant.MENTOR), Optional.empty());
//        viewService.updateDefaultView(RestConstants.EMPLOYEE,ViewTypeEnum.TABLE,TableMapList.ENTITY_FIELDS.get(RestConstants.EMPLOYEE));
//        viewService.updateDefaultView(RestConstants.EMPLOYEE,ViewTypeEnum.BOARD,TableMapList.ENTITY_FIELDS.get(RestConstants.EMPLOYEE));
//        viewService.updateDefaultView(RestConstants.EMPLOYEE,ViewTypeEnum.LIST,TableMapList.ENTITY_FIELDS.get(RestConstants.EMPLOYEE));
//        viewService.updateDefaultView(RestConstants.TIMESHEET_EMPLOYEE,ViewTypeEnum.TABLE,TableMapList.ENTITY_FIELDS.get(RestConstants.TIMESHEET_EMPLOYEE));
//        viewService.createDefaultView(false, true, "Default", ViewTypeEnum.TABLE, RestConstants.TIMESHEET_EMPLOYEE_FOR_FINANCE, TableMapList.ENTITY_FIELDS.get(RestConstants.TIMESHEET_EMPLOYEE_FOR_FINANCE), Optional.empty());

        // viewlar uchun
        initService.createOrUpdateUniqueQuery();
        initService.createOrUpdateQueryExecutorFunction();


        System.err.printf("Sql Init mode is '%s'\n", initMode);
    }

    //ATTACHMENT PATH NI ENVIRONMENT DAN OLADI
    private void setAttachmentPath() {
        CommonUtils.DOMAIN = environment.getProperty("attachment.main.domain." + profile);
        CommonUtils.ATTACHMENT_DOWNLOAD_PATH = environment.getProperty("attachment.download.path");
        CommonUtils.ATTACHMENT_MEDIUM_VIEW_PATH = environment.getProperty("attachment.view.medium.path");
    }



}