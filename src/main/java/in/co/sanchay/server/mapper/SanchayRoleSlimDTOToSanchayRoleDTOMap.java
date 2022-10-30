package in.co.sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import in.co.sanchay.server.dto.auth.model.domain.SanchayRoleDTO;
import in.co.sanchay.server.dto.auth.model.domain.SanchayRoleSlimDTO;

public class SanchayRoleSlimDTOToSanchayRoleDTOMap extends PropertyMap<SanchayRoleSlimDTO, SanchayRoleDTO> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayRoleSlimDTOToSanchayRoleDTOMap(SanchayDeepModelMapper modelMapper)
    {
        super();

        modelMapper.addMappings(this);

        this.modelMapper = modelMapper;
    }

    protected void configure() {
        map().setId(source.getId());
        map().setVersion(source.getVersion());
        map().setName(source.getName());
    }
}
