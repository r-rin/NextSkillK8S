package ukma.springboot.nextskill.common.messaging;

public final class JmsDestinations {
    private JmsDestinations() {}

    public static final String SEND_EMAIL_QUEUE = "SendEmailQueue";
    public static final String USER_CREATED_TOPIC = "VirtualTopic.UserCreatedTopic";
    public static final String USER_CREATED_EMAIL_SERVICE_QUEUE = "Consumer.email-service.VirtualTopic.UserCreatedTopic";
}
