package ukma.springboot.nextskill.common.messaging;

public final class JmsDestinations {
    private JmsDestinations() {}

    public static final String SEND_EMAIL_QUEUE = "Send.Email.Queue";
    public static final String USER_CREATED_TOPIC = "User.Created.Topic";
}
