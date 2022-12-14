package in.co.sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import in.co.sanchay.server.dto.auth.model.domain.SanchayResourceLanguageDTO;
import in.co.sanchay.server.dto.auth.model.domain.SanchayResourceLanguageSlimDTO;

public class SanchayLanguageSlimDTOToSanchayLanguageDTOMap extends PropertyMap<SanchayResourceLanguageSlimDTO, SanchayResourceLanguageDTO> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayLanguageSlimDTOToSanchayLanguageDTOMap(SanchayDeepModelMapper modelMapper)
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
