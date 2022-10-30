package in.co.sanchay.server.repo;

import in.co.sanchay.server.dao.auth.model.domain.SanchayUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<SanchayUser, Long> {
    SanchayUser findByUsername(String name);
}
