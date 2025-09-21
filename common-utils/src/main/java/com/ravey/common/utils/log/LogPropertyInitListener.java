//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ravey.common.utils.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LogPropertyInitListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {
    private boolean started = false;
    private String host;
    private String ip;

    public LogPropertyInitListener() {
    }

    public void start() {
        if (!this.started) {
            Context context = this.getContext();
            if (this.host == null) {
                this.host = "";
                this.ip = "";
                InetAddress localhost = null;

                try {
                    localhost = InetAddress.getLocalHost();
                } catch (UnknownHostException var4) {
                    System.out.println("=======LogPropertyInitListener UnknownHostException=====");
                }

                if (localhost != null) {
                    this.host = localhost.getHostName();
                    this.ip = localhost.getHostAddress();
                }
            }

            context.putProperty("host", this.host);
            context.putProperty("ip", this.ip);
            this.started = true;
        }
    }

    public void stop() {
    }

    public boolean isStarted() {
        return false;
    }

    public boolean isResetResistant() {
        return false;
    }

    public void onStart(LoggerContext loggerContext) {
    }

    public void onReset(LoggerContext loggerContext) {
    }

    public void onStop(LoggerContext loggerContext) {
    }

    public void onLevelChange(Logger logger, Level level) {
    }
}
