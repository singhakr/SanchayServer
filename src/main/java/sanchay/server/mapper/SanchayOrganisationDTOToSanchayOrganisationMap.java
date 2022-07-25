package sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import sanchay.server.dao.auth.model.domain.SanchayOrganisation;
import sanchay.server.dto.auth.model.domain.SanchayOrganisationDTO;

public class SanchayOrganisationDTOToSanchayOrganisationMap extends PropertyMap<SanchayOrganisationDTO, SanchayOrganisation> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayOrganisationDTOToSanchayOrganisationMap(SanchayDeepModelMapper modelMapper)
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
