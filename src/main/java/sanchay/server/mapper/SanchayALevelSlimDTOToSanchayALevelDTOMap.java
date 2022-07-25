package sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import sanchay.server.dto.auth.model.domain.SanchayAnnotationLevelDTO;
import sanchay.server.dto.auth.model.domain.SanchayAnnotationLevelSlimDTO;

public class SanchayALevelSlimDTOToSanchayALevelDTOMap extends PropertyMap<SanchayAnnotationLevelSlimDTO, SanchayAnnotationLevelDTO> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayALevelSlimDTOToSanchayALevelDTOMap(SanchayDeepModelMapper modelMapper)
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
