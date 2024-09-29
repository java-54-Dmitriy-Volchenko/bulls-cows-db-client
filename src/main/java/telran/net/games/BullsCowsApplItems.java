package telran.net.games;

import telran.net.games.model.MoveData;
import telran.net.games.service.BullsCowsService;
import telran.view.*;
import java.time.LocalDate;
import java.util.*;

public class BullsCowsApplItems {
    static BullsCowsService bullsCows;
    static String currentUsername;
    static long currentGameId;
    static int currentNDigits; 

    public static List<Item> getItems(BullsCowsService bullsCows) {
        BullsCowsApplItems.bullsCows = bullsCows;
        Item[] items = {
                Item.of("Login", BullsCowsApplItems::login),
                Item.of("Register", BullsCowsApplItems::register),
                Item.ofExit()
        };
        return new ArrayList<>(List.of(items));
    }

   
    static void login(InputOutput io) {
        String username = io.readString("Enter your username:");
        try {
            currentUsername = bullsCows.loginGamer(username);
            io.writeLine("Hello, " + currentUsername + "!");
            GameMenu(io);  
        } catch (Exception e) {
            io.writeLine("Login failed: " + e.getMessage());
        }
    }


    static void register(InputOutput io) {
        String username = io.readString("Enter your username:");
        LocalDate birthDate = io.readIsoDate("Enter your birth date (YYYY-MM-DD):", "Invalid date format");

        try {
            bullsCows.registerGamer(username, birthDate);
            currentUsername = username;
            io.writeLine("Successfully registered as " + username);
            GameMenu(io);  
        } catch (Exception e) {
            io.writeLine("Registration failed: " + e.getMessage());
        }
    }

 
    static void GameMenu(InputOutput io) {
        Menu gameMenu = new Menu("Game Menu", new Item[] {
        		 Item.of("Create game", BullsCowsApplItems::createGameMenu),
                Item.of("Start game", BullsCowsApplItems::startGameMenu),
                Item.of("Continue game", BullsCowsApplItems::continueGameMenu),
                Item.of("Join game", BullsCowsApplItems::joinGameMenu),
                Item.ofExit()
        });
        gameMenu.perform(io);
    }

    static void createGameMenu(InputOutput io) {
        currentGameId = bullsCows.createGame();
        currentNDigits = bullsCows.getNumberOfDigits(currentGameId);  
        io.writeLine("New game created with ID: " + currentGameId + " and " + currentNDigits + " digits.");

        GuessMenu(io);  
    }
    static void startGameMenu(InputOutput io) {
        List<Long> gamesWithGamer = bullsCows.getNotStartedGamesWithGamer(currentUsername);
        if (gamesWithGamer.isEmpty()) {
            io.writeLine("No available games to start.");
            return;
        }

        gamesWithGamer.forEach(gameId -> {
            int nDigits = bullsCows.getNumberOfDigits(gameId);
            io.writeLine("Game ID: " + gameId + " (" + nDigits + " digits)");
        });
        
        currentGameId = io.readLong("Select game ID to start:", "Invalid game ID");
        currentNDigits = bullsCows.getNumberOfDigits(currentGameId); 

        bullsCows.startGame(currentGameId);
        io.writeLine("Game started: " + currentGameId);

        GuessMenu(io);
    }

   
    static void continueGameMenu(InputOutput io) {
        List<Long> startedGames = bullsCows.getStartedGamesWithGamer(currentUsername);
        if (startedGames.isEmpty()) {
            io.writeLine("No games available to continue.");
            return;
        }

        startedGames.forEach(gameId -> {
            int nDigits = bullsCows.getNumberOfDigits(gameId); 
            io.writeLine("Game ID: " + gameId + " (" + nDigits + " digits)");
        });

        currentGameId = io.readLong("Select game ID to continue:", "Invalid game ID");
        currentNDigits = bullsCows.getNumberOfDigits(currentGameId); 
        
        bullsCows.startGame(currentGameId);

        GuessMenu(io);
    }

  
    static void joinGameMenu(InputOutput io) {
        List<Long> gamesWithNoGamer = bullsCows.getNotStartedGamesWithNoGamer(currentUsername);
        if (gamesWithNoGamer.isEmpty()) {
            io.writeLine("No available games to join.");
            return;
        }

        gamesWithNoGamer.forEach(gameId -> {
            int nDigits = bullsCows.getNumberOfDigits(gameId); 
            io.writeLine("Game ID: " + gameId + " (" + nDigits + " digits)");
        });

        currentGameId = io.readLong("Select game ID to join:", "Invalid game ID");
        currentNDigits = bullsCows.getNumberOfDigits(currentGameId); 

        bullsCows.gamerJoinGame(currentGameId, currentUsername);
        io.writeLine("You joined game: " + currentGameId + " with " + currentNDigits + " digits.");

        bullsCows.startGame(currentGameId);
        GuessMenu(io);
    }

  
    static void GuessMenu(InputOutput io) {
        Menu guessMenu = new Menu("Make your move", new Item[] {
                Item.of("Guess sequence", BullsCowsApplItems::guessItem),
                Item.ofExit()
        });
        guessMenu.perform(io);
    }

   
    static void guessItem(InputOutput io) {
        String guess = io.readStringPredicate(
            String.format("Enter %d non-repeated digits", currentNDigits),  
            "Wrong input",
            str -> str.chars().distinct().filter(c -> c >= '0' && c <= '9').count() == currentNDigits
        );

        List<MoveData> moveResults = bullsCows.moveProcessing(guess, currentGameId, currentUsername);
        moveResults.forEach(io::writeLine);
       
        if (bullsCows.gameOver(currentGameId)) {
            io.writeLine("Congratulations! You won the game. Press 5 for exit! ");
            
        }
    }
}
