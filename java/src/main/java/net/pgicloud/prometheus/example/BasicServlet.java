package net.pgicloud.prometheus.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public class BasicServlet extends HttpServlet {

  private static final long serialVersionUID = -6186472456660737799L;
  
  //This Creates a new Histogram
  //It is best practice to use the app name for the start
  //And to keep the metric in seconds
  private static final Histogram httpRequestLatency = Histogram.build()
      .name("prometheus_example_http_requests_latency_seconds")
      .help("HTTP Request latency in seconds.")
      .register();
  
  private static final Counter getRequests = Counter.build()
      .name("prometheus_example_get_requests_total")
      .help("Total get requests for spesific paths")
      .labelNames("path")
      .register();
  
  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    //Here we use a histogram runnable/lambda to process the get request
    //The histogram will time how long the runnable takes to run. 
    httpRequestLatency.time(()->{
//      req.getContextPath()
      String path = req.getRequestURI();
      System.out.println("PATH:  "+path);
      if(path != null) {
        path = path.toLowerCase();
      } else {
        path = "";
      }

      switch(path) {
      case "/":{
        getRequests.labels(path).inc();
        resp.setStatus(200);
        break;
      }
      case "/bad":{
        getRequests.labels(path).inc();
        resp.setStatus(400);
        break;
      }
      default: {
        getRequests.labels("unhandled").inc();
        resp.setStatus(404);
      }
      }
    });
  }
  
  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    
  }
  
  @Override
  protected void doHead(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    
  }
  
  @Override
  protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    
  }

}
