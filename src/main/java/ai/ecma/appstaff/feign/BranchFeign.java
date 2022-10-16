package ai.ecma.appstaff.feign;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.feign.BranchFeignDTO;
import ai.ecma.appstaff.payload.feign.CompanyFeignDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@FeignClient(name = RestConstants.BRANCH_SERVICE, configuration = FeignConfig.class)
public interface BranchFeign {

    String GET_PATH = "/get/{id}";
    String GET_ALL_BY_ID_LIST_PATH = "/branch/get-all-by-id-list";
    String GET_ACTIVE_BRANCHES_PATH = "/branch/get-active-branches";
    String CHECK_BY_ID_LIST_PATH = "/branch/check-by-id-list";

    //    COMPANY
    String CHECK_COMPANY_BY_ID_LIST_PATH = "/company/check-by-id-list";
    String GET_ACTIVE_COMPANIES_PATH = "/company/get-active-companies";

    @PostMapping(value = GET_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<BranchFeignDTO> getOneBranchById(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @PathVariable UUID id
    );

    @PostMapping(value = GET_ALL_BY_ID_LIST_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<BranchFeignDTO>> getAllBranchByIdList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody Collection<Long> idList
    );

    @GetMapping(value = GET_ACTIVE_BRANCHES_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<BranchFeignDTO>> getAllBranchList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token
    );

    @GetMapping(value = GET_ACTIVE_COMPANIES_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<CompanyFeignDTO>> getAllCompanyList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token
    );

    @PostMapping(value = CHECK_BY_ID_LIST_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<?> checkBranchByIdList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<Long> idList
    );

    @PostMapping(value = CHECK_COMPANY_BY_ID_LIST_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<?> checkCompanyByIdList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<Long> idList
    );
}
