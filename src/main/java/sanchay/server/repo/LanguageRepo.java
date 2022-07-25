package sanchay.server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sanchay.server.dao.auth.model.domain.SanchayResourceLanguage;

public interface LanguageRepo extends JpaRepository<SanchayResourceLanguage, Long> {
    SanchayResourceLanguage findByName(String name);
}
