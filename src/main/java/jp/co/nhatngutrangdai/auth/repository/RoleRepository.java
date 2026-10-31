package jp.co.nhatngutrangdai.auth.repository;

import jp.co.nhatngutrangdai.auth.entity.Role;
import jp.co.nhatngutrangdai.auth.entity.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, RoleCode> {
}
