package ai.ecma.appstaff.config.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 7/5/2022
 */
@Configuration
public class StaffMQConfig {

    /*=================================TURNIKET NOTIFICATION QUEUE START=================================*/

    @Value("${spring.rabbitmq.staff.queues.turniket-notification-for-staff.name}")
    private String turniketQueueName;

    @Value("${spring.rabbitmq.staff.queues.turniket-notification-for-staff.durable}")
    private boolean turniketQueueDurable;

    @Value("${spring.rabbitmq.staff.queues.turniket-notification-for-staff.routing-key}")
    private String turniketQueueRoutingKey;

    @Bean
    public Queue paymentNotificationForSalesQueue() {
        return new Queue(turniketQueueName, turniketQueueDurable);
    }

    @Bean
    public Binding paymentNotificationForSalesQueueBinding(DirectExchange defaultDirectExchange) {
        return BindingBuilder.bind(paymentNotificationForSalesQueue()).to(defaultDirectExchange).with(turniketQueueRoutingKey);
    }

}
