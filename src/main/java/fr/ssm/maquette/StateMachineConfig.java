package fr.ssm.maquette;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.data.*;
import org.springframework.statemachine.data.support.StateMachineJackson2RepositoryPopulatorFactoryBean;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * Created by broblin on 16/12/16.
 */
@Configuration
public class StateMachineConfig {

    @Bean
    public StateMachineJackson2RepositoryPopulatorFactoryBean jackson2RepositoryPopulatorFactoryBean() {
        StateMachineJackson2RepositoryPopulatorFactoryBean factoryBean = new StateMachineJackson2RepositoryPopulatorFactoryBean();
        factoryBean.setResources(new Resource[]{new ClassPathResource("data.json")});
        return factoryBean;
    }

    @Configuration
    @EnableStateMachineFactory
    public static class Config extends StateMachineConfigurerAdapter<String, String> {

        @Autowired
        private StateRepository<? extends RepositoryState> stateRepository;

        @Autowired
        private TransitionRepository<? extends RepositoryTransition> transitionRepository;

        @Override
        public void configure(StateMachineModelConfigurer<String, String> model) throws Exception {
            model
                    .withModel()
                    .factory(modelFactory());
        }

        @Bean
        public StateMachineModelFactory<String, String> modelFactory() {
            return new RepositoryStateMachineModelFactory(stateRepository, transitionRepository);
        }
    }
}
