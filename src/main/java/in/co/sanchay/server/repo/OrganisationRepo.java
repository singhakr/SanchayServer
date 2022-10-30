package in.co.sanchay.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import in.co.sanchay.server.dao.auth.model.domain.SanchayOrganisation;

public interface OrganisationRepo extends JpaRepository<SanchayOrganisation, Long> {
    SanchayOrganisation findByName(String name);
}
