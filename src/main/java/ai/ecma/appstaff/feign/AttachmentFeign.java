package ai.ecma.appstaff.feign;

import ai.ecma.appstaff.payload.ApiResult;
import ai.ecma.appstaff.payload.feign.AttachmentFeignDTO;
import ai.ecma.appstaff.utils.RestConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@FeignClient(name = RestConstants.ATTACHMENT_SERVICE, configuration = FeignConfig.class)
public interface AttachmentFeign {

    String GET_FILE_INFO_BY_ID_LIST_PATH = "attachment/get-file-info-by-id-list";
    String CHECKING_PATH = "/attachment/checking";

    /**
     * ATTACHMENT SERVICEDAN FILELARNI OLIB KELIASH UCHUN YO'L
     *
     * @param fileIdList OLIB KELISH KERAK BO'LGAN FILELAR ID SI
     * @return List<AttachmentFeignDTO>
     */
    @GetMapping(value = GET_FILE_INFO_BY_ID_LIST_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<List<AttachmentFeignDTO>> getFileInfoByIdList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<String> fileIdList
    );

    /**
     * ATTACHMENT SERVICEDAN FILELARNI TEKSHIRIB KELIASH UCHUN YO'L
     *
     * @param uuidSet TEKSHIRIB KELISH KERAK BO'LGAN FILELAR ID SI
     * @return true | false
     */
    @PostMapping(value = CHECKING_PATH, produces = RestConstants.FEIGN_PRODUCES)
    ApiResult<?> checkAttachmentByIdList(
            @RequestHeader(RestConstants.AUTHORIZATION_HEADER) String token,
            @RequestBody List<String> uuidSet
    );
}
