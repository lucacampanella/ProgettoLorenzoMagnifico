package it.polimi.ingsw.client;
import it.polimi.ingsw.utils.Debug;

import java.util.* ;

/**
 * Created by higla on 11/05/2017.
 */
public class LauncherClientFake {
    ClientMain clientMain;
    /**
     * This method welcomes the user and asks him if he wants CLI or GUI.
     * @return the correct object choosen by the player
     */
    public AbstractUIType welcome()
    {
        System.out.println("Hello! WELCOME to Lorenzo il Magnifico's temporary launcher");
        return this.inputCliOrGui();

    }
    public LauncherClientFake(ClientMain client)
    {
        this.clientMain = client;
    }

    /**
     * This method chooses if UI is CL or G.
     * @return the correct object choosen by the player
     */
    private AbstractUIType inputCliOrGui()
    {
        String input;
        Scanner inputScanner = new Scanner(System.in);
        while(true){
            System.out.println("Do you want to use CLI or GUI?");
            input = inputScanner.nextLine();

            //inputScanner.close();
            if(input.equalsIgnoreCase("cli")){
                return new CommandLineUI(this.clientMain);
            }
            if(input.equalsIgnoreCase("gui")){
                return new GraphicalUI(this.clientMain);

            }
        }

    }
}

