package sanchay.server.repo;

import sanchay.server.dao.auth.model.domain.SanchayUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<SanchayUser, Long> {
    SanchayUser findByUsername(String name);
}
