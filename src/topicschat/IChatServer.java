
package topicschat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChatServer extends Remote
{
    void login(String name, String topic, IChatClient newClient) throws RemoteException;
    void logout(String name, String topic) throws RemoteException;
    void send(Message message, String topic) throws RemoteException;
}
