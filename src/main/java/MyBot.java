import dtos.Course;
import dtos.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import states.States;

import java.util.*;

/**
 * Created by Abdulaziz Pulatjonov
 * Date: 06/29/2023 18:20
 */

public class MyBot extends TelegramLongPollingBot {
    HashMap<String, User> users = new HashMap<>();
    HashMap<String, States> condition = new HashMap<>();
    Set<Course> courses = new HashSet<>(Arrays.asList(
            new Course.CourseBuilder()
                    .setAuthor("John")
                    .setId(1L)
                    .setCourseName("Java in one video!")
                    .setUrl("https://www.youtube.com/watch?v=drQK8ciCAjY")
                    .build(),
            new Course.CourseBuilder()
                    .setId(2L)
                    .setAuthor("Mosh")
                    .setCourseName("Python for beginners!")
                    .setUrl("https://www.youtube.com/watch?v=kqtD5dpn9C8&pp=ygUTcHl0aG9uIGluIG9uZSB2aWRlbw%3D%3D")
                    .build(),
            new Course.CourseBuilder()
                    .setId(3L)
                    .setAuthor("Sheriyan")
                    .setCourseName("JavaScript basic!")
                    .setUrl("https://www.youtube.com/watch?v=htznIeWKgg8&pp=ygUXamF2YXNjcmlwdCBpbiBvbmUgdmlkZW8%3D")
                    .build()
    ));
    HashMap<User, Set<Course>> authorCourses = new HashMap<>();
    HashMap<String, Set<Course>> shelf = new HashMap<>();
    @Override
    public String getBotUsername() {
        return TelegramBotUtils.USERNAME;
    }

    @Override
    public String getBotToken() {
        return TelegramBotUtils.TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage());
        String chatId = update.getMessage().getChatId().toString();
        System.out.println(condition.get(chatId));
        System.out.println(shelf);
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                if (update.getMessage().getText().toLowerCase().equals("back")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Welcome to the board " + update.getMessage().getFrom().getFirstName() + "!");
                    sendMessage.setReplyMarkup(mainMenu());
                    try {
                        users.put(chatId, new User(update));
                        condition.put(chatId, States.MAIN_MENU);
                        execute(sendMessage);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else if (update.getMessage().getText().equals("/start")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Welcome to the board " + update.getMessage().getFrom().getFirstName() + "!");
                    sendMessage.setReplyMarkup(mainMenu());
                    try {
                        users.put(chatId, new User(update));
                        condition.put(chatId, States.MAIN_MENU);
                        execute(sendMessage);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                } else if (condition.get(chatId).equals(States.MAIN_MENU)) {
                    String response = update.getMessage().getText();
                    if (response.toLowerCase().equals("enroll to course!")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("Choose the course from below:");
                        sendMessage.setChatId(chatId);
                        System.out.println("Enrolling to the course!");
                        sendMessage.setReplyMarkup(getAllCourses());
                        try {
                            condition.put(chatId, States.ADD_COURSE);
                            execute(sendMessage);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    } else if (response.toLowerCase().equals("my courses!")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        System.out.println("My courses!");
                        Set<Course> cs = shelf.get(chatId);
                        if (cs == null || cs.size() == 0) {
                            System.out.println("No courses found!");
                            condition.put(chatId, States.MAIN_MENU);
                            String answer = "You have not enrolled any course yet!";
                            sendMessage.setText(answer);
                        } else {
                            System.out.println("I have courses!");
                            condition.put(chatId, States.MY_COURSES);
                            String answer = "Here is your courses!";
                            sendMessage.setText(answer);
                            sendMessage.setReplyMarkup(getMyCoursesMenu(update));
                        }
                        try {
                            execute(sendMessage);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    } else {
                        try {
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(chatId);
                            condition.put(chatId, States.MAIN_MENU);
                            sendMessage.setText("Invalid input, please choose from the menu below:");
                            execute(sendMessage);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                } else if (condition.get(chatId).equals(States.MY_COURSES)) {
                    getMyCourse(update);
                } else if (condition.get(chatId).equals(States.ADD_COURSE)) {
                    String courseName = update.getMessage().getText();
                    Course course = null;
                    for (Course c : courses) {
                        if (c.getCourseName().equals(courseName)) {
                            course = c;
                            break;
                        }
                    }
                    if (course == null) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("Invalid input! Please choose from menu:");
                        sendMessage.setChatId(chatId);
                        try {
                            execute(sendMessage);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    } else {
                        User user = users.get(chatId);
                        Set<Course> userCourses = shelf.get(chatId);
                        boolean exist = false;
                        if (userCourses == null || userCourses.size() == 0) {
                            userCourses = new HashSet<>();
                        } else {
                            for (Course c : userCourses) {
                                if (c.getCourseName().equals(course.getCourseName())) {
                                    exist = true;
                                    break;
                                }
                            }
                        }
                        if (exist) {
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setText("You had already enrolled into this course!");
                            sendMessage.setChatId(chatId);
                            try {
                                condition.put(chatId, States.MAIN_MENU);
                                execute(sendMessage);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        } else {
                            userCourses.add(course);
                            shelf.put(chatId, userCourses);
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setText("You successfully enrolled to the course!");
                            sendMessage.setChatId(chatId);
                            try {
                                execute(sendMessage);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    }
                }
            }
        }
    }

    private ReplyKeyboard getMyCoursesMenu(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        User user = users.get(chatId);
        Set<Course> courses = shelf.get(chatId);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (Course c : courses) {
            KeyboardRow row = new KeyboardRow();
            row.add(c.getCourseName());
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private void getMyCourse(Update update){
        String courseName = update.getMessage().getText();
        Course course = null;
        for (Course c: shelf.get(update.getMessage().getChatId().toString())) {
            if (c.getCourseName().equals(courseName)){
                course = c;
                break;
            }
        }
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        if (course == null){
            sendMessage.setText("Invalid input, choose from below!");
        }else{
            sendMessage.setText(course.toString());
            condition.put(update.getMessage().getChatId().toString(), States.MAIN_MENU);
        }
        try{
            execute(sendMessage);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private ReplyKeyboard mainMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("Enroll to course!");
        keyboard.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add("My courses!");
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private ReplyKeyboard getAllCourses() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (Course c : courses) {
            KeyboardRow row = new KeyboardRow();
            row.add(c.getCourseName());
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }
}
