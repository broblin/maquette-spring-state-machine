package fr.ssm.maquette;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.HashMap;

/**
 * pris de l'exemple spring voir : http://docs.spring.io/spring-statemachine/docs/1.2.0.RELEASE/reference/htmlsingle/#sm-persist
 * il faudra voir ensutie pour un vrai context de persistance via une base de données par exemple
 *
 * Created by broblin on 10/01/17.
 */
public class InMemoryStateMachinePersist implements StateMachinePersist<States, Events, String> {

    private final HashMap<String, StateMachineContext<States, Events>> contexts = new HashMap<>();

    @Override
    public void write(StateMachineContext<States, Events> context, String contextObj) throws Exception {
        contexts.put(contextObj, context);
    }

    @Override
    public StateMachineContext<States, Events> read(String contextObj) throws Exception {
        return contexts.get(contextObj);
    }
}
