package in.co.sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import in.co.sanchay.server.dao.auth.model.domain.SanchayRole;
import in.co.sanchay.server.dto.auth.model.domain.SanchayRoleDTO;

public class SanchayRoleDTOToSanchayRoleMap extends PropertyMap<SanchayRoleDTO, SanchayRole> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayRoleDTOToSanchayRoleMap(SanchayDeepModelMapper modelMapper)
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
