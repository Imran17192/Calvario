package de.uniks.stp24.ws;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

@jakarta.websocket.ClientEndpoint
public class ClientEndpoint {
    private final URI endpointURI;
    private final List<Consumer<String>> messageHandlers = Collections.synchronizedList(new ArrayList<>());

    Session userSession;

    final Timer timer = new Timer();

    public ClientEndpoint(URI endpointURI) {
        this.endpointURI = endpointURI;
    }

    public boolean isOpen() {
        return this.userSession != null && this.userSession.isOpen();
    }

    public void open() {
        if (isOpen()) {
            return;
        }

        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        forceKeepAwake();
    }

    private void forceKeepAwake() {
        final int INTERVAL  = 30000;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
               sendMessage("keep-alive");
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, INTERVAL);
    }

    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    /*
    Array was modified and run through at the same time. Synchronisation of hole iteration
    solves this problem.
     */
    @OnMessage
    public void onMessage(String message) {
        synchronized (this.messageHandlers) {
            for (final Consumer<String> handler : this.messageHandlers) {
                handler.accept(message);
            }
        }
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    public void addMessageHandler(Consumer<String> msgHandler) {
        this.messageHandlers.add(msgHandler);
    }

    public void removeMessageHandler(Consumer<String> msgHandler) {
        this.messageHandlers.remove(msgHandler);
    }

    public void sendMessage(String message) {
        if (this.userSession == null) {
            return;
        }
        this.userSession.getAsyncRemote().sendText(message);
    }

    public void close() {
        if (this.userSession == null) {
            return;
        }

        try {
            this.userSession.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMessageHandlers() {
        return !this.messageHandlers.isEmpty();
    }
}
