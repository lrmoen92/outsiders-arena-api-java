package org.outsiders.arena.handler;

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
    protected WebSocketSession session;
    
    @Autowired
    protected BattleMessageService battleMessageService;

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        this.session = session;
        this.processMessage(message);
    }

    public WebSocketSession getSession() {
        return this.session;
    }

    public void processMessage(TextMessage message) throws Exception {
    	LOG.info("Literally Anything");
    	LOG.info(message.toString());
    	LOG.info(message.getPayload());
    	// map representing json sent in
    	Map m = new Gson().fromJson(message.getPayload(), Map.class);
    	
    	//create response
    	WebSocketMessage msg = this.createTextMessage(m);
    	
    	boolean twoPlayersMessage = false;
    	
        for (WebSocketSession s : sessions) {
        	if (session.getUri().equals(s.getUri()) && !session.equals(s)) {
        		if (m.get("type").toString().equals("MATCH_MAKING") || m.get("type").toString().equals("TURN_END") || m.get("type").toString().equals("CHAT") || m.get("type").toString().equals("GAME_END")) {
        			LOG.info(m.get("type").toString() + " MESSAGE RECIEVED FROM " + session.getRemoteAddress().toString() + " MATCHED AND SENT TO " + s.getRemoteAddress().toString() + " ON ARENA : " + s.getUri().toString());
    		    	session.sendMessage(msg);
            		s.sendMessage(msg);
            		twoPlayersMessage = true;
        		}
        	} 
        }
        if (!twoPlayersMessage) {
        	LOG.info(m.get("type").toString() + " MESSAGE RECIEVED FROM " + session.getRemoteAddress().toString() + " AND ARENA : " + session.getUri().toString());
        	session.sendMessage(msg);
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