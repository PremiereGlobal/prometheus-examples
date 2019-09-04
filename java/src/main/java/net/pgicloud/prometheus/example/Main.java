package net.pgicloud.prometheus.example;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.jetty.JettyStatisticsCollector;



@SuppressWarnings("unused")
public class Main {
  private final Server jettyServer;

  private final ServerConnector jettyConnector;
  private final Counter requests = Counter.build()
      .name("my_library_requests_total")
      .help("Total requests.")
      .register();


  public Main(int jettyPort) throws Exception {
    DefaultExports.initialize();
    jettyServer = new Server(jettyPort);
    jettyConnector = new ServerConnector(jettyServer);

    HandlerCollection handlers = new HandlerCollection();
    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    handlers.setHandlers(new Handler[]{context});
    
    //Jetty has its own stats handler, and prometheus has a wrapper for it
    //Here we create the stats handler from Jetty
    StatisticsHandler stats = new StatisticsHandler();
    //We pass our default handlers collection to it
    stats.setHandler(handlers);
    //Now we set the stats handler as Jettys main handler
    jettyServer.setHandler(stats);
    //Here we make the prometheus JettyStatis collector and call register 
    JettyStatisticsCollector jsc = new JettyStatisticsCollector(stats);
    jsc.register();

    //Add our basic servlet with our own stat examples
    context.addServlet(new ServletHolder(new BasicServlet()), "/");
    //Add the metrics servlet that can be used by prometheus to pull metrics
    context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
    
    jettyServer.start();
  }

  public static void main(String[] args) throws Exception {
    Main m = new  Main(8844);
    while(true) {
      Thread.sleep(1000);
    }
  }


}