package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@AllArgsConstructor
public class User {

    private int id;

    @Email(message = "Email should be format \"xxx@mm.com\"")
    @NotNull(message = "Email should not be empty")
    private String email;

    @NotBlank
    @NotNull
    private String login;

    @Nullable
    private String name;

    @Past
    @Nullable
    private LocalDate birthday;

    @JsonIgnore
    @Getter
    private HashMap<Integer, Boolean> friends;

    public List<Integer> getFriendsIds(boolean isConfirmFriends){
        var result =  new ArrayList<Integer>();

        for(Integer friendId : friends.keySet()){
            if(isConfirmFriends) {
                if(friends.get(friendId)){
                    result.add(friendId);
                }
            } else {
                if(!friends.get(friendId)){
                    result.add(friendId);
                }
            }
        }

        return result;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("USER_ID", this.id);
        values.put("EMAIL", this.email);
        values.put("LOGIN", this.login);
        values.put("USER_NAME", this.name);
        values.put("BIRTHDAY", this.birthday);
        return values;
    }
}
