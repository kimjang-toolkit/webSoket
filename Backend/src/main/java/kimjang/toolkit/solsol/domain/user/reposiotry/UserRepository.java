package kimjang.toolkit.solsol.domain.user.reposiotry;

import kimjang.toolkit.solsol.domain.user.dto.UserProfileDto;
import kimjang.toolkit.solsol.domain.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIdIn(List<Long> ids);

    @Query(value = "select u from User u join FETCH u.authorities a WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query(value = "select new kimjang.toolkit.solsol.domain.user.dto.UserProfileDto(" +
            " u.id,  u.email, u.name, u.imgUrl ) from User u WHERE u.email = :email")
    UserProfileDto findProfileByEmail(@Param("email") String email);

    Boolean existsByEmail(String email);
}