package ai.ecma.appstaff.payload.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewIdAndNameDTO {
    private UUID id;
    private String name;
}
