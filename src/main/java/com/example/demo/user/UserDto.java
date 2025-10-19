package com.example.demo.user;

import com.example.demo.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private int age;
    private String occupation;
    private boolean enabled;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.age = user.getAge();
        this.occupation = user.getOccupation();
        this.enabled = user.isEnabled();
    }
}