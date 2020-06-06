package org.outsiders.arena.configuration;

import org.outsiders.arena.handler.BattleSocketHandler;
import org.outsiders.arena.handler.GlobalSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig
  implements WebSocketConfigurer
{
  @Autowired
  private BattleSocketHandler battleSocketHandler;
  @Autowired
  private GlobalSocketHandler globalSocketHandler;
  
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry)
  {
    registry.addHandler(battleSocketHandler, "/arena/{arenaId}").setAllowedOrigins("*");
    registry.addHandler(globalSocketHandler, "/chat").setAllowedOrigins("*");
  }
 
}
