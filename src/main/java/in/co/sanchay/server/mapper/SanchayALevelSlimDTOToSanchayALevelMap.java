package in.co.sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import in.co.sanchay.server.dao.auth.model.domain.SanchayAnnotationLevel;
import in.co.sanchay.server.dto.auth.model.domain.SanchayAnnotationLevelSlimDTO;

public class SanchayALevelSlimDTOToSanchayALevelMap extends PropertyMap<SanchayAnnotationLevelSlimDTO, SanchayAnnotationLevel> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayALevelSlimDTOToSanchayALevelMap(SanchayDeepModelMapper modelMapper)
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
