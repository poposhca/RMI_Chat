
package topicschat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatClient extends Remote
{
    void receiveEnter(String name) throws RemoteException;
    void receiveExit(String name)throws RemoteException;
    void receiveMessage(Message message) throws RemoteException;
    
    //Nuevo mñetodo para avisar de la creación de un nuevo tópico
    void receiveNewTopic(String topic) throws RemoteException;

    
}
