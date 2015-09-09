package s2gx2015;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class WebSocketIntervalService {

	private final AtomicInteger counter = new AtomicInteger();


	@Autowired
	public WebSocketIntervalService(TaskScheduler scheduler, SimpMessagingTemplate template) {
		scheduleTask(scheduler, template, this.counter);
	}

	private void scheduleTask(TaskScheduler scheduler, SimpMessagingTemplate template, AtomicInteger integer) {
		scheduler.scheduleWithFixedDelay(() -> {
			template.convertAndSend("/topic/interval", integer.incrementAndGet());
		}, 1000);
	}

}
