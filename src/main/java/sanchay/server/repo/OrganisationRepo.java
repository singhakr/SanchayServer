package sanchay.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sanchay.server.dao.auth.model.domain.SanchayOrganisation;

public interface OrganisationRepo extends JpaRepository<SanchayOrganisation, Long> {
    SanchayOrganisation findByName(String name);
}
