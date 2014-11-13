package hello;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    MetricRegistry registry;


    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    Greeting sayHello(@RequestParam(value = "name", required = false, defaultValue = "Stranger") String name) throws InterruptedException {

        //counter metrics
        registry.counter("counter.testrc").inc();

        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @PostConstruct
    private void init() {
        //Export dans graphite
        final Graphite graphite = new Graphite(new InetSocketAddress("10.1.1.80", 2003));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        reporter.start(5, TimeUnit.SECONDS);
    }

}
