package telran.net.games;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;


import telran.net.Request;
import telran.net.TcpClient;
import telran.net.games.model.MoveData;
import telran.net.games.service.BullsCowsService;

public class BullsCowsProxy implements BullsCowsService{
TcpClient tcpClient;


public BullsCowsProxy(TcpClient tcpClient) {
	this.tcpClient = tcpClient;
}


@Override
public long createGame() {
	
	    String response = tcpClient.sendAndReceive(new Request("createGame", ""));
	    
	    if (response.startsWith("Exception:")) {
	        throw new RuntimeException(response.substring("Exception:".length()).trim()); 
	    }
	    
	    return Long.parseLong(response); 
	
}

@Override
public List<String> startGame(long gameId) {
	  String response = tcpClient.sendAndReceive(new Request("startGame", String.valueOf(gameId)));
	    
	    if (response.startsWith("Exception:")) {
	        throw new RuntimeException(response.substring("Exception:".length()).trim()); 
	    }
	    
	    return Arrays.asList(response.split(";")); 
}

@Override
public void registerGamer(String username, LocalDate birthDate) {
	  JSONObject registered = new JSONObject();
	  registered.put("username", username);
	  registered.put("birthDate", birthDate.toString());
	    
	    String response = tcpClient.sendAndReceive(new Request("registerGamer", registered.toString()));
	    
	    if (response.startsWith("Exception:")) {
	        throw new RuntimeException(response.substring("Exception:".length()).trim());
	    }
}

@Override
public void gamerJoinGame(long gameId, String username) {
	JSONObject joined = new JSONObject();
	joined.put("gameId", gameId);
	joined.put("username", username);
    
    String response = tcpClient.sendAndReceive(new Request("gamerJoinGame", joined.toString()));
    
    if (response.startsWith("Exception:")) {
        throw new RuntimeException(response.substring("Exception:".length()).trim());
    }
}

@Override
public List<Long> getNotStartedGames() {
String response = tcpClient.sendAndReceive(new Request("getNotStartedGames", ""));
    
    if (response.startsWith("Exception:")) {
        throw new RuntimeException(response.substring("Exception:".length()).trim());
    }
    
    return Arrays.stream(response.split(";"))
            .map(Long::parseLong) 
            .toList();
}

@Override
public List<MoveData> moveProcessing(String sequence, long gameId, String username) {
	  JSONObject json = new JSONObject();
	    json.put("sequence", sequence);
	    json.put("gameId", gameId);
	    json.put("username", username);
	    
	    String response = tcpClient.sendAndReceive(new Request("moveProcessing", json.toString()));
	    
	    if (response.startsWith("Exception:")) {
	        throw new RuntimeException(response.substring("Exception:".length()).trim());
	    }
	    
	    return Arrays.stream(response.split(";"))
	            .map(s -> new MoveData(new JSONObject(s))) 
	            .toList();
}

@Override
public boolean gameOver(long gameId) {
String response = tcpClient.sendAndReceive(new Request("gameOver", String.valueOf(gameId)));
    
    if (response.startsWith("Exception:")) {
        throw new RuntimeException(response.substring("Exception:".length()).trim());
    }
    
    return Boolean.parseBoolean(response); 
}

@Override
public List<String> getGameGamers(long gameId) {
	  String response = tcpClient.sendAndReceive(new Request("getGameGamers", String.valueOf(gameId)));
	    
	    if (response.startsWith("Exception:")) {
	        throw new RuntimeException(response.substring("Exception:".length()).trim());
	    }
	    
	    return Arrays.asList(response.split(";"));
}

@Override
public String loginGamer(String username) {
	 String response = tcpClient.sendAndReceive(new Request("loginGamer", username));
	    
	    if (response.startsWith("Exception:")) {
	        throw new RuntimeException(response.substring("Exception:".length()).trim());
	    }
	    
	    return response; 
}
@Override
public List<Long> getNotStartedGamesWithGamer(String username) {
    String response = tcpClient.sendAndReceive(new Request("getNotStartedGamesWithGamer", username));
    if (response.startsWith("Exception:")) {
        throw new RuntimeException(response.substring("Exception:".length()).trim());
    }
    return Arrays.stream(response.split(";")).map(Long::parseLong).toList();
}

@Override
public List<Long> getNotStartedGamesWithNoGamer(String username) {
    String response = tcpClient.sendAndReceive(new Request("getNotStartedGamesWithNoGamer", username));
    if (response.startsWith("Exception:")) {
        throw new RuntimeException(response.substring("Exception:".length()).trim());
    }
    return Arrays.stream(response.split(";")).map(Long::parseLong).toList();
}

@Override
public List<Long> getStartedGamesWithGamer(String username) {
    String response = tcpClient.sendAndReceive(new Request("getStartedGamesWithGamer", username));
    if (response.startsWith("Exception:")) {
        throw new RuntimeException(response.substring("Exception:".length()).trim());
    }
    return Arrays.stream(response.split(";")).map(Long::parseLong).toList();
}

@Override
public int getNumberOfDigits(long gameId) {
    String response = tcpClient.sendAndReceive(new Request("getNumberOfDigits", String.valueOf(gameId)));
    if (response.startsWith("Exception:")) {
        throw new RuntimeException(response.substring("Exception:".length()).trim());
    }
    return Integer.parseInt(response);
}
}