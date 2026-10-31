package jp.co.nhatngutrangdai.auth.repository;

import java.util.Optional;
import java.util.UUID;

import jp.co.nhatngutrangdai.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);
}
