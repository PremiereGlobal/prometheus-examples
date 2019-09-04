package net.pgicloud.prometheus.example;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.threadly.util.StringUtils;

import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;

public class HistogramServlet extends HttpServlet {

  private static final long serialVersionUID = 4490425922319621023L;

  //This Creates a new Histogram
  //It is best practice to use the app name for the start
  //And to keep the metric in seconds
  private final Histogram httpRequestLatency = Histogram.build()
      .name("prometheus_example_http_requests_latency_seconds")
      .help("HTTP Request latency in seconds.")
      .register();

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    String type = req.getParameter("type");
    if(type != null) {
      if(req.getParameter("type").equals("normal")) {
        normalLatency(req,resp);
      } else if(req.getParameter("type").equals("selftracking")) {
        selfTrackingLatency(req,resp);
      }else if(req.getParameter("type").equals("runnable")) {
        runnableLatency(req,resp);
      }
    } else {
      normalLatency(req,resp);
    }

  }

  private void normalLatency(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    //Create a timer from the Histogram.  Timers are thread safe so you can create one from anywhere.
    Timer t = httpRequestLatency.startTimer();
    //Return a 200, why not
    resp.setStatus(200);

    //Here we do some random hashing to get different times
    String sha = doStuff();
    //Write the hash to the body
    resp.getWriter().write(sha+": ");
    //This will record the time it took since startTimer() was called.
    //t.close() will do the same thing.
    t.observeDuration();
  }

  private void selfTrackingLatency(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    //Here we create our own start time, we can get this how ever we want
    //As long as we can tell how much time elapsed between now and when we are done.
    long startTime = System.currentTimeMillis();
    //Return a 200, why not
    resp.setStatus(200);

    //Here we do some random hashing to get different times
    String sha = doStuff();
    //Write the hash to the body
    resp.getWriter().write(sha+": ");
    //Here we get the elapsed time, we have to make sure its a double and in seconds.
    double elapsed = (double)(System.currentTimeMillis()-startTime)/1000.0;
    httpRequestLatency.observe(elapsed);
  }

  private void runnableLatency(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
    //Here we send a runnable to the histogram and it tracks the duration.
    //This runs in the current thread.
    httpRequestLatency.time(()->{
      //Return a 200, why not
      resp.setStatus(200);
      //Here we do some random hashing to get different times
      String sha = doStuff();
      //Write the hash to the body
      try {
        resp.getWriter().write(sha+": ");
      } catch (IOException e) {
        //Exceptions are kind of harder to handle this way but it all depends on what you are doing.
        e.printStackTrace();
      }

    });
  }

  private String doStuff() {
    int iter = ThreadLocalRandom.current().nextInt(500, 10000);
    StringBuilder sb = new StringBuilder();
    for(int i=0; i<iter ; i++) {
      String s= StringUtils.makeRandomString(ThreadLocalRandom.current().nextInt(500, 10000));
      sb.append(s);
    }
    String sha = Utils.SHAString(sb.toString());
    return sha;
  }

}