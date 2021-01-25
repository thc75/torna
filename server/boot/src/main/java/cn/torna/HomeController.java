package cn.torna;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final String REDIRECT_INDEX = "forward:index.html";

    @GetMapping("/")
    public String index() {
        return REDIRECT_INDEX;
    }

}