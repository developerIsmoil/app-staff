package ai.ecma.appstaff.payload.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * VIEW NI NOMI, PUBLIC LIGI , FAVOURITE GA OLISHINI O'ZGARTIRISH UCHUN
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewEditDTO {

    @NotNull(message = "VIEW_ID_REQUIRED")
    private UUID viewId;

    private String viewName;

    private Boolean publicly;

    private Boolean favourite;
}
