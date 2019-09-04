package net.pgicloud.prometheus.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.client.Counter;

public class CounterServlet  extends HttpServlet {

  private static final long serialVersionUID = -1940838796628684732L;
  
  //Creates a counter that counts the number of requests coming into this servlet.
  private final Counter requestCounter = Counter.build()
      .name("prometheus_example_http_requests_total")
      .help("Prometheus example http request counter")
      .labelNames("resp")
      .register();

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    System.out.println("OLD!");
    String get_resp = req.getParameter("resp");
    try {
      int resp_num = Integer.parseInt(get_resp);
      if(resp_num >=200 && resp_num<=505) {
        requestCounter.labels(get_resp).inc();
        resp.setStatus(resp_num);
        resp.getWriter().write("count:"+requestCounter.labels(get_resp).get()+"\n");
        return;
      }
    } catch(Exception e) {
      
    }
    resp.setStatus(404);
    resp.getWriter().write("count:"+requestCounter.labels("404").get()+"\n");
    
  }
}
