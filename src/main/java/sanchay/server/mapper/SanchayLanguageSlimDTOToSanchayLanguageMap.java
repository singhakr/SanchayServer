package sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import sanchay.server.dao.auth.model.domain.SanchayResourceLanguage;
import sanchay.server.dto.auth.model.domain.SanchayResourceLanguageDTO;
import sanchay.server.dto.auth.model.domain.SanchayResourceLanguageSlimDTO;

public class SanchayLanguageSlimDTOToSanchayLanguageMap extends PropertyMap<SanchayResourceLanguageSlimDTO, SanchayResourceLanguage> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayLanguageSlimDTOToSanchayLanguageMap(SanchayDeepModelMapper modelMapper)
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
