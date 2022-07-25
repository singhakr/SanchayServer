package sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import sanchay.server.dto.auth.model.domain.SanchayOrganisationDTO;
import sanchay.server.dto.auth.model.domain.SanchayOrganisationSlimDTO;

public class SanchayOrganisationSlimDTOToSanchayOrganisationDTOMap extends PropertyMap<SanchayOrganisationSlimDTO, SanchayOrganisationDTO> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayOrganisationSlimDTOToSanchayOrganisationDTOMap(SanchayDeepModelMapper modelMapper)
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
