package sanchay.server.repo;

import sanchay.server.dao.auth.model.domain.SanchayRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<SanchayRole, Long> {
    SanchayRole findByName(String name);
}
