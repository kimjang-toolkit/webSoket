package kimjang.toolkit.solsol.domain.user.reposiotry;

import kimjang.toolkit.solsol.domain.user.dto.UserProfileDto;
import kimjang.toolkit.solsol.domain.user.entities.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query(value = "SELECT distinct new kimjang.toolkit.solsol.domain.user.dto.UserProfileDto( " +
            " f.subUser.id,  f.subUser.email, f.subUser.name, f.subUser.imgUrl ) FROM Friend f " +
            " JOIN User u ON f.user = u" +
            " WHERE u.email = :email") // 이메일이 email인 유저가 main인 f만 존재~
    List<UserProfileDto> findFriendsByEmail(@Param("email") String email);

    void deleteByUser_EmailAndSubUser_IdIn(String email, List<Long> id);
}
