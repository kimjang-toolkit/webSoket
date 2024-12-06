package kimjang.toolkit.solsol.domain.user.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @Column(name = "friend_id") // 컬럼의 이름을 바꿀 수 있다.
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id_main")
    private User user;

    private String subUserName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id_sub")
    private User subUser;

    public static Friend of(User mainUser, User subUser){
        return Friend.builder()
                .user(mainUser)
                .subUser(subUser)
                .subUserName(subUser.getName())
                .build();
    }
}
