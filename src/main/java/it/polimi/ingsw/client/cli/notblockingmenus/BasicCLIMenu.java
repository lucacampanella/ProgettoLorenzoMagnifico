package it.polimi.ingsw.client.cli.notblockingmenus;

import it.polimi.ingsw.client.cli.CallbackFunction;
import it.polimi.ingsw.client.controller.ControllerCallbackInterface;
import it.polimi.ingsw.utils.Debug;

import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by campus on 11/06/2017.
 */
public abstract class BasicCLIMenu implements Runnable {

    private HashMap<String, DescrCallbackContainer> optionsMap;

    private String initialMenu;

    private ControllerCallbackInterface controller;

    public BasicCLIMenu(String initialMenu, ControllerCallbackInterface controller) {
        this.initialMenu = initialMenu;
        this.controller = controller;
        optionsMap = new HashMap<String, DescrCallbackContainer>();
        Debug.printDebug("BasicCLIMenu constructor");
    }

    public void addOption(String abbrev, String descr, CallbackFunction callbackFunction) {
        optionsMap.put(abbrev, new DescrCallbackContainer(callbackFunction, descr));
    }

    /**
     *
     */
    @Override
    public void run() {
        Debug.printVerbose("Process Started");
        //TODO make a singleton for the input stream, bad practice to open multiple scanner on the same stream
        Scanner cin = new Scanner(System.in);
        printMenu();

        DescrCallbackContainer callbackContainer = optionsMap.get(cin.nextLine());
        while(callbackContainer == null) {
            System.out.println("Not a recognised option, please choose a correct one");
            callbackContainer = optionsMap.get(cin.nextLine());
        }
        callbackContainer.getFunction().callback();
    }

    private void printMenu() {
        System.out.println(initialMenu);
        optionsMap.forEach((abbrev, descrCallback) -> System.out.println(abbrev + " - " + descrCallback.getDescription()));
    }

    private class DescrCallbackContainer {
        CallbackFunction function;
        String description;

        public DescrCallbackContainer(CallbackFunction function, String description) {
            this.function = function;
            this.description = description;
        }

        public CallbackFunction getFunction() {
            return function;
        }

        public String getDescription() {
            return description;
        }
    }

    protected ControllerCallbackInterface getController() {
        return controller;
    }
}
