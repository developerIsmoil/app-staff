package ai.ecma.appstaff.projection;

import ai.ecma.appstaff.entity.Position;

import java.util.List;
import java.util.UUID;

public interface PositionProjection {
    List<Position> getObjects();

    UUID getParentId();
}
