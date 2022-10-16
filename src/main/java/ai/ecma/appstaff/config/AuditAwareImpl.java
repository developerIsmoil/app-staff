package ai.ecma.appstaff.config;

import ai.ecma.appstaff.payload.UserDTO;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;
import java.util.UUID;

import static ai.ecma.appstaff.utils.CommonUtils.getUserDTOFromRequestForAuditing;

public class AuditAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        UserDTO userDTOFromRequest = getUserDTOFromRequestForAuditing();
        if (userDTOFromRequest != null) {
            return Optional.of(userDTOFromRequest.getId());
        }
        return Optional.empty();
    }
}
