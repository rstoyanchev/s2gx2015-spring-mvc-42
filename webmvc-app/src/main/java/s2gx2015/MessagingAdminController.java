package s2gx2015;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagingAdminController {

	private final TaskScheduler scheduler;

	private final SimpMessagingTemplate template;

	private final AtomicInteger counter = new AtomicInteger();

	private ScheduledFuture<?> task;


	@Autowired
	public MessagingAdminController(TaskScheduler scheduler, SimpMessagingTemplate template) {
		this.scheduler = scheduler;
		this.template = template;
	}


	@RequestMapping(path = "/interval", method = RequestMethod.POST)
	private void start() {
		synchronized (this) {
			if (this.task == null) {
				this.task = this.scheduler.scheduleWithFixedDelay(() -> {
					IntervalValue value = new IntervalValue(counter.incrementAndGet());
					String parity = (value.getValue() % 2 == 0 ? "even" : "odd");
					Map<String, Object> headers = Collections.singletonMap("parity", parity);
					template.convertAndSend("/topic/interval", value, headers);
				}, 1000);
			}
		}
	}

	@RequestMapping(path = "/interval", method = RequestMethod.DELETE)
	private void stop() {
		synchronized (this) {
			if (this.task != null) {
				this.task.cancel(true);
				this.task = null;
			}
		}
	}


	private static class IntervalValue {

		private final int value;

		public IntervalValue(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		@Override
		public String toString() {
			return "value=" + this.value;
		}
	}

}
