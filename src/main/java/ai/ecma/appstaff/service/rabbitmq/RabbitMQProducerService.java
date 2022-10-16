package ai.ecma.appstaff.service.rabbitmq;

import ai.ecma.appstaff.payload.ExceptionMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This class not documented :(
 *
 * @author Muhammad Mo'minov
 * @since 8/2/2022
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQProducerService {
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exception-handler.default-exchange}")
    private String exceptionHandlerDefaultDirectExchange;

    @Value("${spring.rabbitmq.exception-handler.queues.bot-queues.routing-key}")
    private String exceptionHandlerRoutingKey;


    public void sendExceptions(ExceptionMessageDTO exceptionMessageDTO) {
        this.send(exceptionHandlerDefaultDirectExchange, exceptionHandlerRoutingKey, exceptionMessageDTO);
    }


    private void send(String exchange, String routingKey, Object message) {
        log.info("Start queueing: Exchange = {}, RoutingKey = {}, Message = {}", exchange, routingKey, message.toString());

        rabbitTemplate.convertAndSend(exchange, routingKey, message);

        log.info("End queueing: Exchange = {}, RoutingKey = {}, Message = {}", exchange, routingKey, message);
    }

//
//    //===============EDUCATION UCHUN ============================================
//    /**
//     * STUDENT NI GROUP GA BIRIKTIRISH UCHUN QUEUGA YUBORADI
//     */
//    public void attachStudentToEducationForGroup(StudentAttachDTO message) {
//        this.send(educationDefaultDirectExchange, educationAttachStudentGroupQueueRoutingKey, message);
//    }
}
