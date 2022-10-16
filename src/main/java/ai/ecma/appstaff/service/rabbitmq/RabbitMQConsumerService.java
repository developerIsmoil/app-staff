package ai.ecma.appstaff.service.rabbitmq;

import ai.ecma.appstaff.payload.feign.turniket.TurniketDTOForMessage;
import ai.ecma.appstaff.service.otherService.TurniketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 8/2/2022
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumerService {
    private final TurniketService turniketService;


    @Value("${spring.rabbitmq.staff.queues.turniket-notification-for-staff.name}")
    private String turniketQueueName;


    //TURNIKET NOTIFICATION KELADI FINANCE DAN
    @RabbitListener(queues = "staff.turniket-notification-for-staff")
    public void paymentNotificationListener(TurniketDTOForMessage message) {
        log.info("get turniket notification from turniket service :{}", message);
        turniketService.getUpdates(message);
    }


}
