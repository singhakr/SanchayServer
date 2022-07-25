package sanchay.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sanchay.server.dao.auth.model.domain.SanchayAnnotationLevel;

public interface AnnotationLevelRepo extends JpaRepository<SanchayAnnotationLevel, Long> {
    SanchayAnnotationLevel findByName(String name);
}
