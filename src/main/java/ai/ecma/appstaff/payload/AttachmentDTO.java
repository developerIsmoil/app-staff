package ai.ecma.appstaff.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttachmentDTO {

    private UUID id;

    // ATTACHMENT SERVICEGA SAQLANGAN FILENING URLI
    @NotBlank(message = "REQUIRED.FILE.ID")
    private String fileId;

    // SHU FILE HAQIDA QIQQACHA MA'LUMOT
//    @NotBlank(message = "REQUIRED.DESCRIPTION")
    private String description;

    //NAME
    private String name;

    //FILE TYPE
    private String type;

    //FILE SIZE
    private Long size;

    //CREATED DATE
    private Long createDate;

    private Boolean action;

    public AttachmentDTO(String fileId) {
        this.fileId = fileId;
    }

    public AttachmentDTO(UUID id, String fileId, String description, Boolean action) {
        this.id = id;
        this.fileId = fileId;
        this.description = description;
        this.action = action;
    }
}
