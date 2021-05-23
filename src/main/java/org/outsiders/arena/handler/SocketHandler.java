package org.outsiders.arena.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class SocketHandler
  extends TextWebSocketHandler
{
  List<WebSocketSession> sessions = new CopyOnWriteArrayList<WebSocketSession>();
  
  public void afterConnectionEstablished(WebSocketSession session)
    throws Exception
  {
    this.sessions.add(session);
  }
  
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    throws Exception
  {
    this.sessions.remove(session);
  }
}
