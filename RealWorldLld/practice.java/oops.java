import java.util.*;


abstract class Notification { //abstraction

    private final String reciever; //encapsulation
    private final String message;

    protected Notification(String reciever, String message){

        if(reciever == null || reciever.isBlank()){
            throw new IllegalArgumentException("reciever not found");
        }

        this.reciever = reciever;
        this.message = message;
    }

    public String getReciever(){
        return reciever;
    }

    public String getMessage(){
        return message;
    }

    public abstract void send();
}

//inhertance

class smsNotification extends Notification{

    public smsNotification(String phoneNumber, String message){
        super(phoneNumber, message);
}

    @Override
    public void send(){
        System.out.println("[SMS] Sending text to " + getReciever() + ": " + getMessage());
    }
}

class EmailNotification extends Notification{



    public EmailNotification(String emailAddress, String message){
        super(emailAddress, message);

    }

    @Override
    public void send(){
        System.out.println("[EMAIL] Sending email to " + getReciever() + ": " + getMessage());
    }
}

class NotificationDispatcher{

   private final List<Notification> ls = new ArrayList<>();

   public void addNotification(Notification notification){
        ls.add(notification);
    }

    public void dispatchAll(){
        for(Notification notification : ls){
            notification.send();
        }

        ls.clear();
    }


}

public class oops{
    public static void main(String[] args){

        NotificationDispatcher dispatcher = new NotificationDispatcher();
        
        dispatcher.addNotification(new smsNotification("123-456-7890", "Hello, this is a test SMS!"));
        dispatcher.addNotification(new EmailNotification("user@example.com", "Hello, this is a test email!"));
        dispatcher.dispatchAll();
    }
}
