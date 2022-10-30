package in.co.sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import in.co.sanchay.server.dao.auth.model.domain.SanchayRole;
import in.co.sanchay.server.dto.auth.model.domain.SanchayRoleSlimDTO;

public class SanchayRoleSlimDTOToSanchayRoleMap extends PropertyMap<SanchayRoleSlimDTO, SanchayRole> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayRoleSlimDTOToSanchayRoleMap(SanchayDeepModelMapper modelMapper)
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
