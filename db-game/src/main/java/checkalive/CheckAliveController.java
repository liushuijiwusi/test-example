package checkalive;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class CheckAliveController {

    @Autowired
    private CheckAliveService checkAliveService;

    @SneakyThrows
    @GetMapping("/check-alive")
    public void checkAlive(HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(checkAliveService.check(request)));
        response.getWriter().close();
    }
}
