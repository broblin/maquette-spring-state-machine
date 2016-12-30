package fr.ssm.maquette;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;

/**
 * Created by broblin on 16/12/16.
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"fr.ssm.maquette"})
public class Application implements CommandLineRunner {

    public static final String ORDER_ID_LABEL = "orderId";
    @Autowired
    private StateMachine<States, Events> stateMachine;

    Long orderIdTest = 10L;
    Long orderIdTestWithError = -10L;

    @Override
    public void run(String... args) throws Exception {
        stateMachine.sendEvent(Events.E1);
        stateMachine.sendEvent(Events.E2);
        stateMachine.sendEvent(Events.E3);

        reinitForS1();

        sendMessageWithE2(orderIdTest);

        simulateUnexpectedError();

        System.out.println(String.format("state machine final state  %s",stateMachine.getState().getId()));
    }

    public void reinitForS1(){
        stateMachine.stop();
        stateMachine.start();
        //put to S1 as initial value
        stateMachine.sendEvent(Events.INIT_TO_S1);
    }

    public void sendMessageWithE2(Long id){
        //send a message from S1 to S2
        Message<Events> message = MessageBuilder
                .withPayload(Events.E2)
                .setHeader(ORDER_ID_LABEL,id)
                .build();

        stateMachine.sendEvent(message);
    }

    public void simulateUnexpectedError(){
        try{
            reinitForS1();
            sendMessageWithE2(orderIdTestWithError);

        }catch(Exception e){
            System.out.println(String.format("exception occured : %s state machine state  %s",e.getMessage(),stateMachine.getState()));
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
