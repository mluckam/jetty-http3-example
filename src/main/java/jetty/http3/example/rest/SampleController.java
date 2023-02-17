package jetty.http3.example.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SamplePath.SAMPLE)
public class SampleController {

    @GetMapping(SamplePath.HELLO_W0RLD)
    public String getHelloWorld() {
        return "HELLO WORLD!";
    }
}
