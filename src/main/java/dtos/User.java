package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String chatId;
    private String username;
    private String phoneNumber;

    public User(Update update) {
        this.firstName = update.getMessage().getFrom().getFirstName();
        this.lastName = update.getMessage().getFrom().getLastName();
        this.username = update.getMessage().getFrom().getUserName();
        this.chatId = update.getMessage().getChatId().toString();
    }

}
