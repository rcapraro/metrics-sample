package hello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/hello-world")
public class HelloWorldController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    CounterService counterService;

    @Autowired
    GaugeService gaugeService;

    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    Greeting sayHello(@RequestParam(value = "name", required = false, defaultValue = "Stranger") String name) throws InterruptedException {

        counterService.increment("counter.hello-service.invoked");

        long debut = System.currentTimeMillis();

        Thread.sleep(500 + Math.round(Math.random() * 500));

        gaugeService.submit("gauge.hello-service.duration", System.currentTimeMillis() - debut);
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

}
