package sanchay.server.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import sanchay.server.repo.*;

public class SanchayDeepModelMapper extends ModelMapper {

    private UserRepo userRepo;
    private RoleRepo roleRepo;
    //    private final PrivilegeRepo privilegeRepo;
    private LanguageRepo languageRepo;

    private OrganisationRepo organisationRepo;

    private AnnotationLevelRepo annotationLevelRepo;

    public SanchayDeepModelMapper(UserRepo userRepo, RoleRepo roleRepo, LanguageRepo languageRepo,
                                  OrganisationRepo organisationRepo, AnnotationLevelRepo annotationLevelRepo)
    {
        super();

        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.languageRepo = languageRepo;
        this.organisationRepo = organisationRepo;
        this.annotationLevelRepo = annotationLevelRepo;
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        Object tmpSource = source;
//        if(source == null){
//            tmpSource = new Object();
//        }

        return super.map(tmpSource, destinationType);
    }
}
