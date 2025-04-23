package com.codeit.duckhu;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
  @RequestMapping(value = {
      "/",
      "/{path:^(?!api|static|assets|favicon\\.ico|index\\.html).*}/**"
  })
  public String forward() {
    return "forward:/index.html";
  }

}
