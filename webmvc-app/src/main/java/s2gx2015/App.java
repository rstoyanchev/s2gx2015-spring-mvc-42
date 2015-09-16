package s2gx2015;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;


@SpringBootApplication
public class App {


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }


    @Bean
    @Primary
    public TaskScheduler scheduler() {
        return new ThreadPoolTaskScheduler();
    }


    /**
     * Spring MVC config.
     */
    @Configuration
    static class WebConfig extends WebMvcConfigurerAdapter {

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setDefaultTimeout(-1);
        }
    }

    /**
     * WebSocket config.
     */
    @Configuration
    @EnableWebSocketMessageBroker
    static class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

        @Autowired
        private App app;

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/messaging")
                    .setAllowedOrigins(
                            "http://localhost:8080", // https://jira.spring.io/browse/SPR-13464
                            "http://localhost:9000")
                    .withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.enableSimpleBroker()
                    .setTaskScheduler(this.app.scheduler());  // for heartbeats
        }
    }

}
