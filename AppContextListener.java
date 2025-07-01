package com.servlets.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Stop background threads when the application stops
        FaceCaptureServlet.stopThread();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing to initialize here
    }
}
