package ai.ecma.appstaff.test;

import ai.ecma.appstaff.payload.TurniketFilterDTO;
import ai.ecma.appstaff.service.otherService.TurniketServiceImpl;
import ai.ecma.appstaff.utils.RestConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class TestCon {

//    private final TurniketServiceImpl turniketService;
//    public static final String DOWNLOAD = RestConstants.BASE_PATH_V1 + "/test";
//
//    @GetMapping(path = TestCon.DOWNLOAD)
//    public ResponseEntity<Resource> downloadExcel() {
//        ArrayList<TurniketFilterDTO> turniketFilterDTOS = new ArrayList<>(List.of(turniketService.getCompanyFilter()));
//        TurniketFilterDTO dateRangeFilter = turniketService.getDateRangeFilter();
//        turniketFilterDTOS.add(dateRangeFilter);
//        return turniketService.downloadExcel(turniketFilterDTOS,null);
//    }
}
