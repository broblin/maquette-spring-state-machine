package fr.ssm.maquette;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * Created by broblin on 16/12/16.
 */
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig
        extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config)
            throws Exception {
        config
                .withConfiguration()
                .autoStartup(true)
                .listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states
                .withStates()
                .initial(States.SI)
                .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(States.SI).target(States.S1).event(Events.E1)
                .and()
                .withExternal()
                .source(States.SI).target(States.S1).event(Events.INIT_TO_S1)
                .and()
                .withExternal()
                .source(States.SI).target(States.S2).event(Events.INIT_TO_S2)
                .and()
                .withExternal()
                .source(States.SI).target(States.S3).event(Events.INIT_TO_S3)
                .and()
                .withExternal()
                .source(States.S1).target(States.S2).event(Events.E2).action(action(),errorAction())
                .and()
                .withExternal()
                .source(States.S2).target(States.S3).event(Events.E3);
    }

    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                System.out.println("State change to " + to.getId());
            }

            @Override
            public void stateMachineError(StateMachine<States, Events> stateMachine, Exception exception) {
                System.out.println(String.format("an exception an occured on the stateMachineListener : %s", exception.getMessage()));
            }
        };
    }

    @Bean
    public Action<States, Events> action() {
        return  context -> {
                // do something
                System.out.println(String.format("action occured for this event : %s from this source : %s to this target : %s",context.getEvent(),context.getSource().getId(),context.getTarget().getId()));

                if(Long.valueOf(-10L).equals(context.getMessage().getHeaders().get(Application.ORDER_ID_LABEL))){
                   throw new RuntimeException("unexpected exception simulation");
                }else{
                    System.out.println(String.format("order id : %s",context.getMessage().getHeaders().get(Application.ORDER_ID_LABEL)));
                }
            };
    }

    @Bean
    public Action<States, Events> errorAction() {
        return  context -> {
                // RuntimeException("MyError") added to context
                Exception exception = context.getException();
                StateMachine stateMachine = context.getStateMachine();
            System.out.println(String.format("an exception an occured %s on the order : %s for this stateMachine state : %s", exception.getMessage(),context.getMessage().getHeaders().get(Application.ORDER_ID_LABEL),stateMachine.getState()));
        };
    }
}
