package fr.ssm.maquette;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by broblin on 29/12/16.
 */
public class StateMachineLogListener extends StateMachineListenerAdapter<String, String> {

    private final LinkedList<String> messages = new LinkedList<String>();

    public List<String> getMessages() {
        return messages;
    }

    public void resetMessages() {
        messages.clear();
    }

    @Override
    public void stateContext(StateContext<String, String> stateContext) {
        if (stateContext.getStage() == StateContext.Stage.STATE_ENTRY) {
            messages.addFirst("Enter " + stateContext.getTarget().getId());
        } else if (stateContext.getStage() == StateContext.Stage.STATE_EXIT) {
            messages.addFirst("Exit " + stateContext.getSource().getId());
        } else if (stateContext.getStage() == StateContext.Stage.STATEMACHINE_START) {
            messages.addLast("Machine started");
        } else if (stateContext.getStage() == StateContext.Stage.STATEMACHINE_STOP) {
            messages.addFirst("Machine stopped");
        }
    }

}
