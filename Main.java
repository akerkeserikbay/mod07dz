import java.util.*;

//COMMAND

interface ICommand {
    void execute();
    void undo();
}

// Receivers

class Light {
    void on() { System.out.println("Свет включен"); }
    void off() { System.out.println("Свет выключен"); }
}

class Door {
    void open() { System.out.println("Дверь открыта"); }
    void close() { System.out.println("Дверь закрыта"); }
}

class Thermostat {
    private int temperature = 20;

    void increase() {
        temperature++;
        System.out.println("Температура увеличена до " + temperature);
    }

    void decrease() {
        temperature--;
        System.out.println("Температура уменьшена до " + temperature);
    }
}

class TV {
    void on() { System.out.println("Телевизор включен"); }
    void off() { System.out.println("Телевизор выключен"); }
}

//  Commands
class LightOnCommand implements ICommand {
    private Light light;
    public LightOnCommand(Light light) { this.light = light; }

    public void execute() { light.on(); }
    public void undo() { light.off(); }
}

class LightOffCommand implements ICommand {
    private Light light;
    public LightOffCommand(Light light) { this.light = light; }

    public void execute() { light.off(); }
    public void undo() { light.on(); }
}

class DoorOpenCommand implements ICommand {
    private Door door;
    public DoorOpenCommand(Door door) { this.door = door; }

    public void execute() { door.open(); }
    public void undo() { door.close(); }
}

class TempUpCommand implements ICommand {
    private Thermostat thermostat;
    public TempUpCommand(Thermostat thermostat) { this.thermostat = thermostat; }

    public void execute() { thermostat.increase(); }
    public void undo() { thermostat.decrease(); }
}

class TVOnCommand implements ICommand {
    private TV tv;
    public TVOnCommand(TV tv) { this.tv = tv; }

    public void execute() { tv.on(); }
    public void undo() { tv.off(); }
}

// Invoker

class RemoteControl {
    private Stack<ICommand> history = new Stack<>();

    public void executeCommand(ICommand command) {
        command.execute();
        history.push(command);
    }

    public void undoLast() {
        if (history.isEmpty()) {
            System.out.println("Нет команд для отмены!");
            return;
        }
        ICommand command = history.pop();
        command.undo();
    }
}

// TEMPLATE METHOD

abstract class Beverage {

    // шаблонный метод
    public final void prepareRecipe() {
        boilWater();
        brew();
        pourInCup();
        if (customerWantsCondiments()) {
            addCondiments();
        }
    }

    void boilWater() { System.out.println("Кипятим воду"); }
    void pourInCup() { System.out.println("Наливаем в чашку"); }

    abstract void brew();
    abstract void addCondiments();

    // hook
    boolean customerWantsCondiments() {
        return true;
    }
}

class Tea extends Beverage {
    void brew() { System.out.println("Завариваем чай"); }
    void addCondiments() { System.out.println("Добавляем лимон"); }
}

class Coffee extends Beverage {

    private Scanner scanner = new Scanner(System.in);

    void brew() { System.out.println("Завариваем кофе"); }
    void addCondiments() { System.out.println("Добавляем сахар и молоко"); }

    boolean customerWantsCondiments() {
        System.out.print("Добавить сахар и молоко? (yes/no): ");
        String answer = scanner.nextLine();

        if (answer.equalsIgnoreCase("yes")) return true;
        if (answer.equalsIgnoreCase("no")) return false;

        System.out.println("Некорректный ввод, добавки не будут добавлены.");
        return false;
    }
}

class HotChocolate extends Beverage {
    void brew() { System.out.println("Растворяем какао порошок"); }
    void addCondiments() { System.out.println("Добавляем маршмеллоу"); }
}

// MEDIATOR

interface IMediator {
    void sendMessage(String message, User sender);
    void sendPrivateMessage(String message, User sender, String receiverName);
    void addUser(User user);
    void removeUser(User user);
}

class ChatRoom implements IMediator {
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        users.add(user);
        System.out.println(user.getName() + " присоединился к чату");
    }

    public void removeUser(User user) {
        users.remove(user);
        System.out.println(user.getName() + " покинул чат");
    }

    public void sendMessage(String message, User sender) {
        if (!users.contains(sender)) {
            System.out.println("Ошибка: пользователь не в чате!");
            return;
        }

        for (User user : users) {
            if (user != sender) {
                user.receive(message, sender.getName());
            }
        }
    }

    public void sendPrivateMessage(String message, User sender, String receiverName) {
        for (User user : users) {
            if (user.getName().equals(receiverName)) {
                user.receive("(Личное) " + message, sender.getName());
                return;
            }
        }
        System.out.println("Пользователь не найден!");
    }
}

class User {
    private String name;
    private IMediator mediator;

    public User(String name, IMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public String getName() { return name; }

    public void send(String message) {
        mediator.sendMessage(message, this);
    }

    public void sendPrivate(String message, String receiverName) {
        mediator.sendPrivateMessage(message, this, receiverName);
    }

    public void receive(String message, String senderName) {
        System.out.println(name + " получил сообщение от " + senderName + ": " + message);
    }
}

// MAIN

public class Main {
    public static void main(String[] args) {

        System.out.println("=== COMMAND ===");

        Light light = new Light();
        Door door = new Door();
        Thermostat thermostat = new Thermostat();
        TV tv = new TV();

        RemoteControl remote = new RemoteControl();

        remote.executeCommand(new LightOnCommand(light));
        remote.executeCommand(new DoorOpenCommand(door));
        remote.executeCommand(new TempUpCommand(thermostat));
        remote.executeCommand(new TVOnCommand(tv));

        remote.undoLast();
        remote.undoLast();

        System.out.println("\n=== TEMPLATE METHOD ===");

        Beverage tea = new Tea();
        tea.prepareRecipe();

        Beverage coffee = new Coffee();
        coffee.prepareRecipe();

        Beverage chocolate = new HotChocolate();
        chocolate.prepareRecipe();

        System.out.println("\n=== MEDIATOR ===");

        ChatRoom chatRoom = new ChatRoom();

        User akerke = new User("Akerke", chatRoom);
        User nazerke = new User("Nazerke", chatRoom);
        User admin = new User("Admin", chatRoom);

        chatRoom.addUser(akerke);
        chatRoom.addUser(nazerke);
        chatRoom.addUser(admin);

        akerke.send("Привет всем!");
        nazerke.sendPrivate("Привет, как дела?", "Akerke");

        chatRoom.removeUser(admin);
    }
}