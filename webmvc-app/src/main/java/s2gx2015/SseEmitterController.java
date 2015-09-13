package s2gx2015;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@CrossOrigin("http://localhost:9000")
@Controller
public class SseEmitterController {

	private static Logger logger = LoggerFactory.getLogger(SseEmitterController.class);

	private final Set<SseEmitter> emitters = new CopyOnWriteArraySet<>();


	@Autowired
	public SseEmitterController(TaskScheduler scheduler) {
		scheduleTask(scheduler, this.emitters);
	}

	private static void scheduleTask(TaskScheduler scheduler, Set<SseEmitter> emitters) {
		AtomicInteger integer = new AtomicInteger();
		scheduler.scheduleWithFixedDelay(() -> {
			int intValue = integer.getAndIncrement();
			for (SseEmitter emitter : emitters) {
				try {
					logger.debug("Sending value to: " + emitter);
					emitter.send(intValue);
				}
				catch (IOException ex) {
					logger.debug("IOException for: " + emitter);
					emitters.remove(emitter);
				}
			}
		}, 1000);
	}


	@RequestMapping(path = "/sse-interval", method = RequestMethod.GET)
	public SseEmitter handle() {
		return initEmitter();
	}

	@RequestMapping(path = "/sse-interval-with-status", method = RequestMethod.GET)
	public ResponseEntity<SseEmitter> handleWithResponseEntity(@RequestParam(required=false) String q) {
		return (StringUtils.hasText(q) ?
			ResponseEntity.status(HttpStatus.NO_CONTENT).body(null) :
			ResponseEntity.ok().body(initEmitter()));
	}

	private SseEmitter initEmitter() {
		SseEmitter emitter = new SseEmitter();
		emitter.onCompletion(() -> {
			logger.debug("Interval stream completed: " + emitter);
			emitters.remove(emitter);
		});
		this.emitters.add(emitter);
		return emitter;
	}

}