package ai.ecma.appstaff.config.rabbitmq;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Main RabbitMQ configurations.
 *
 * @author Muhammad Mo'minov
 * @since 8/2/2022
 */
@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.staff.default-exchange}")
    private String defaultDirectExchange;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public DirectExchange defaultDirectExchange() {
        return new DirectExchange(defaultDirectExchange);
    }
}
