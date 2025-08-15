package study.springai.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.LocalDateTime

@Controller
class IndexController {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("currentTime", LocalDateTime.now())
        model.addAttribute("serverStatus", "Running")
        return "index"
    }
}
