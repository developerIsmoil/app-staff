package ai.ecma.appstaff.payload.feign;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachmentFeignDTO implements Serializable {
    private String id;
    private Long size;
    private String contentType;
    private String name;
    private String url;
    private String url_open;
    private String thumbnail_large;
    private String thumbnail_medium;
    private String thumbnail_small;
    private String description;
    private Long createdAt;
    private String fileId;
    private Boolean action;
}
