package ai.ecma.appstaff.payload.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ViewNameEditDTO {

    private UUID viewId;

    private String viewName;
}
