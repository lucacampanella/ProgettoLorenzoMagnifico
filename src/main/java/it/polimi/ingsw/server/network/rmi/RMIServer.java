package it.polimi.ingsw.server.network.rmi;

import it.polimi.ingsw.server.AbstractServerType;
import it.polimi.ingsw.server.ServerMain;
import it.polimi.ingsw.client.network.rmi.RMIClientInterface;
import it.polimi.ingsw.client.exceptions.*;
import it.polimi.ingsw.utils.Debug;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

/**
 * RMIServer is the implementation of the server via rmi. It’s interface is used to be published and called by the client side. In fact the RMIServer publishes himself (his stub) on the rmi registry server and waits to be called by the client thru his function loginPlayer.
 */
public class RMIServer extends AbstractServerType implements RMIServerInterface {

    private Registry registry;

	/**
	 * This is the public constructor of the class that also prepares and start the server
	 * @param serverMainInst the server object
	 * @param port
	 * @throws ServerException if the creation of the server goes wrong
	 */
	public RMIServer(ServerMain serverMainInst, int port) throws ServerException {
		super(serverMainInst, port);
		startServer();
	}

	/**
	 * performs rmi actions to create registry and publishes the object (itself)
	 * @throws ServerException if the creation of the server goes wrong
	 */
	@Override
	protected void startServer() throws ServerException
	{
		Debug.printDebug("I'm starting the rmi server on port " + getPort());
        this.createOrLoadRegistry();
        this.publishObj();
        Debug.printDebug("rmi server started");
	}

    /**
     * Creates or loads the rmi registry at the selected port
     * @throws ServerException if it cannot load or create the rmi registry
     */
	private void createOrLoadRegistry() throws ServerException
    {
        try {
            registry = LocateRegistry.createRegistry(getPort());
        } catch(RemoteException e) {
            Debug.printDebug("rmi registry already exists", e);
        }
        try {
            registry = LocateRegistry.getRegistry(getPort());
        } catch(RemoteException e) {
            Debug.printDebug("rmi registry not found", e);
            throw new ServerException("Cannot load or create the rmi registry");
        }
    }

    /**
     * publishes this class to the registry
     * @throws ServerException
     */
    private void publishObj() throws ServerException
    {
        try {
            UnicastRemoteObject.exportObject(this, getPort());
            registry.rebind("RMIServerInterface", this);
        } catch(RemoteException e) {
            Debug.printError("Unable to publish object", e);
            throw new ServerException("Cannot publish server object \"RMIServerInterface\"");
        }
    }

    /**
     * Used to close the server when it's needed no more
     * @throws ServerException
     */
    @Override
	public void closeServer() throws ServerException
	{
		//TODO implement the real closure of the server
		Debug.printDebug("I'm stopping the rmi server");
	}

    /**
     * this method is used when the user has never played and wants to create an account
     * @param nickname to register in the server DB
     * @param password to register in the server DB
     * @return
     * @throws RemoteException if something goes wrong during the connection
     */
    @Override
	public RMIPlayerInterface loginPlayer(String nickname, String password, RMIClientInterface RMIClientInterfaceInst) throws RemoteException, LoginException
	{
	    RMIPlayer newPlayer = null;

        Debug.printVerbose("Client tried to log in, usr: " + nickname + "password: " + password);

        getServerMainInst().loginPlayer(nickname, password); //if some problem is encountered LoginException is thrown and passed to the client

        //if the exception is not thrown then we can continue creating the player object and making him join a room
        newPlayer = new RMIPlayer(nickname, RMIClientInterfaceInst);
        UnicastRemoteObject.exportObject(newPlayer, getPort());

        getServerMainInst().makeJoinRoomLogin(newPlayer);

        return newPlayer;
	}

    /**
     * this method is used when an user already exists and decides to login with his username and password, real implementation of the abstract method
     *
     * @param nickname
     * @param password
     * @throws RemoteException
     * @throws UsernameAlreadyInUseException if the username is already in use
     */
    @Override
    public RMIPlayerInterface registerPlayer(String nickname, String password, RMIClientInterface RMIClientInterfaceInst) throws RemoteException, UsernameAlreadyInUseException
    {
        RMIPlayer newPlayer = null;

        Debug.printDebug("CLient tried to register, usr: " + nickname + "password: " + password);
        getServerMainInst().registerPlayer(nickname, password);

        //if no exception is not thrown then we can continue creating the player object and making him join a room
        newPlayer = new RMIPlayer(nickname, RMIClientInterfaceInst);
        UnicastRemoteObject.exportObject(newPlayer, getPort());

        getServerMainInst().makeJoinRoomRegister(newPlayer);

        return newPlayer;
    }

    private void connectClientToPlayer()
    {
        String randomRMIName = UUID.randomUUID().toString();
    }
}	
