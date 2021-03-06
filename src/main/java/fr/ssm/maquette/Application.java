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
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

/**
 * Created by broblin on 16/12/16.
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan({"fr.ssm.maquette"})
public class Application implements CommandLineRunner {

    public static final String ORDER_ID_LABEL = "orderId";

    @Autowired
    private StateMachineFactory<States, Events> stateMachineFactory;

    Long orderIdTest = 10L;
    Long orderIdTestWithError = -10L;

    InMemoryStateMachinePersist stateMachinePersist = new InMemoryStateMachinePersist();
    StateMachinePersister<States, Events, String> persister = new DefaultStateMachinePersister<>(stateMachinePersist);

    @Override
    public void run(String... args) throws Exception {
        StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.sendEvent(Events.E1);
        stateMachine.sendEvent(Events.E2);
        stateMachine.sendEvent(Events.E3);

        sendMessageWithE2(orderIdTest);

        simulateUnexpectedError();

        System.out.println(String.format("state machine final state  %s",stateMachine.getState().getId()));

        System.out.println(String.format("state machine final with id : %s state  %s",orderIdTest,restoreStateMachine(stateMachineFactory.getStateMachine(orderIdTest.toString())).getState().getId()));

        System.out.println(String.format("state machine final with id : %s state  %s",orderIdTestWithError,restoreStateMachine(stateMachineFactory.getStateMachine(orderIdTestWithError.toString())).getState().getId()));
    }

    /**
     *
     * @param stateMachine
     */
    public void saveStateMachine(StateMachine<States, Events> stateMachine) throws Exception {
        persister.persist(stateMachine,stateMachine.getId());
    }

    /**
     *
     * @param stateMachine
     * @return
     * @throws Exception
     */
    public StateMachine<States, Events> restoreStateMachine(StateMachine<States, Events> stateMachine) throws Exception {
        return persister.restore(stateMachine,stateMachine.getId());
    }

    public void sendMessageWithE2(Long id) throws Exception {
        StateMachine<States, Events> stateMachine = stateMachineFactory.getStateMachine(id.toString());
        stateMachine.sendEvent(Events.INIT_TO_S1);
        Message<Events> message = MessageBuilder
                .withPayload(Events.E2)
                .setHeader(ORDER_ID_LABEL,id)
                .build();

        stateMachine.sendEvent(message);
        saveStateMachine(stateMachine);
    }

    public void simulateUnexpectedError(){
        try{
            sendMessageWithE2(orderIdTestWithError);
        }catch(Exception e){
            System.out.println(String.format("exception occured : %s",e.getMessage()));
        }

    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
