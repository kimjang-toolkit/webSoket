package kimjang.toolkit.solsol.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kimjang.toolkit.solsol.domain.user.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String email;

    @Pattern(regexp = "^[/\\,'₩`{}|\"#\\$%^&*()]+$", message = "Name contains invalid characters.")
    @Size(max = 10, message = "Name must be less than 10 characters.")
    private String name;
    public static UserDto toDto(User user){
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }
    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
