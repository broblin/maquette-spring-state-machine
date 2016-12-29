package fr.ssm.maquette;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.data.RepositoryTransition;
import org.springframework.statemachine.data.TransitionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by broblin on 29/12/16.
 */
@Controller
public class StateMachineController {
    @Autowired
    private StateMachineFactory<String, String> stateMachineFactory;

    @Autowired
    private TransitionRepository<? extends RepositoryTransition> transitionRepository;

    @RequestMapping("/")
    public String home() {
        return "redirect:/state";
    }

    @RequestMapping("/state")
    public String feedAndGetStates(@RequestParam(value = "events", required = false) List<String> events, Model model) throws Exception {

        StateMachine<String, String> stateMachine = stateMachineFactory.getStateMachine();
        StateMachineLogListener listener = new StateMachineLogListener();
        stateMachine.addStateListener(listener);
        stateMachine.start();
        if (events != null) {
            for (String event : events) {
                stateMachine.sendEvent(event);
            }
        }
        stateMachine.stop();
        model.addAttribute("allEvents", getEvents());
        model.addAttribute("messages", createMessages(listener.getMessages()));
        return "states";
    }

    private String[] getEvents() {
        List<String> events = new ArrayList<>();
        for (RepositoryTransition t : transitionRepository.findAll()) {
            events.add(t.getEvent());
        }
        return events.toArray(new String[0]);
    }

    private String createMessages(List<String> messages) {
        StringBuilder buf = new StringBuilder();
        for (String message : messages) {
            buf.append(message);
            buf.append("\n");
        }
        return buf.toString();
    }

}


