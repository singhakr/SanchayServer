package sanchay.server.mapper;

import org.modelmapper.PropertyMap;
import sanchay.server.dao.auth.model.domain.SanchayResourceLanguage;
import sanchay.server.dto.auth.model.domain.SanchayResourceLanguageDTO;

public class SanchayLanguageDTOToSanchayLanguageMap extends PropertyMap<SanchayResourceLanguageDTO, SanchayResourceLanguage> {
    private SanchayDeepModelMapper modelMapper;

    public SanchayLanguageDTOToSanchayLanguageMap(SanchayDeepModelMapper modelMapper)
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
