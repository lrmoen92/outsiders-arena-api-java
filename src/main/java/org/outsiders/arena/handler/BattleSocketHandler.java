
package org.outsiders.arena.handler;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.Gson;

@Service
public class BattleSocketHandler
extends SocketHandler {
    public static Logger LOG = LoggerFactory.getLogger(BattleSocketHandler.class);
    
    @Autowired
    protected BattleMessageService battleMessageService;

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        this.processMessage(session, message);
    }
    
    public boolean isTwoPlayersMessage(String type) {
    	return (type.equals("MATCH_MAKING") || type.equals("TURN_END") || 
    			type.equals("CHAT") || type.equals("GAME_END"));
    }
    
    public boolean sessionsShareUri(WebSocketSession session, WebSocketSession s) {
    	return (session.getUri().equals(s.getUri()) && !session.equals(s));
    }

    public void processMessage(WebSocketSession session, TextMessage message) throws Exception {
		LOG.info("Literally Anything");
		LOG.info(message.toString());
		LOG.info(message.getPayload());
		
		// map representing json sent in
		Map m = new Gson().fromJson(message.getPayload(), Map.class);
		String type = m.get("type").toString();
		boolean twoPlayersMessage = isTwoPlayersMessage(type);
		WebSocketMessage msg = this.createTextMessage(m);
		
	    for (WebSocketSession s : sessions) {
	    	if (sessionsShareUri(session, s) && twoPlayersMessage) {
	    			LOG.info(m.get("type").toString() + " MESSAGE RECIEVED FROM " + session.getRemoteAddress().toString() + " MATCHED AND SENT TO " + s.getRemoteAddress().toString() + " ON ARENA : " + s.getUri().toString());
	    			trySend(s, msg);
	    			trySend(session, msg);
	    	} 
	    }
	    if (!twoPlayersMessage) {
	    	LOG.info(m.get("type").toString() + " MESSAGE RECIEVED FROM " + session.getRemoteAddress().toString() + " AND ARENA : " + session.getUri().toString());
	    	trySend(session, msg);
	    }
	}
    
    public synchronized void trySend(WebSocketSession s, WebSocketMessage msg) throws IOException, InterruptedException {
		try {
			synchronized(s) {
	    		s.sendMessage(msg);
			}
		} catch (Exception e) {
			LOG.info(e.getMessage());
		}
    }

    public String processMapEntry(Map valueMap) throws Exception {
    	String type = valueMap.get("type").toString();
    	LOG.info(type);
		switch (type) {
		    case "MATCH_MAKING": 
		      LOG.info("Match Making...");
		      return this.battleMessageService.handleMatchmakingMessage(valueMap);
		    case "COST_CHECK": 
		      LOG.info("Cost Check");
		      return this.battleMessageService.handleCostCheckMessage(valueMap);
		    case "TARGET_CHECK": 
		      LOG.info("Target Check");
		      return this.battleMessageService.handleTargetCheckMessage(valueMap);
		    case "TURN_END": 
		      LOG.info("Turn End");
		      return this.battleMessageService.handleTurnEndMessage(valueMap);
    	  	case "ENERGY_TRADE": 
		      LOG.info("Energy Trade");
		      return this.battleMessageService.handleEnergyTradeMessage(valueMap);
    	  	case "GAME_END": 
		      LOG.info("Game End");
		      return this.battleMessageService.handleGameEndMessage(valueMap);
		    default :
		      LOG.info("Unrecognized Message");
		      return "";
	    } 
    }

    public TextMessage createTextMessage(Map valueMap) throws Exception {
    	String res = this.processMapEntry(valueMap);
    	if (!res.isEmpty()) {
    		return new TextMessage(res);
    	} else {
    		return new TextMessage("{}");
    	}
    }
}