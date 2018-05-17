
package topicschat;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class ChatServer extends UnicastRemoteObject implements IChatServer
{
    //Tabla que guarda a todos los usuarios loggeados al chat
    private Hashtable<String, Hashtable<String, IChatClient>> topics;
    
    //Constructor vacio, sirve para reportar la excepción.
    public ChatServer() throws RemoteException
    {
        topics = new Hashtable<>();
    }
    
    //Implementación de la interfaz, los métodos van a ser asincronos
    
    @Override
    public synchronized void login(String name, String topic, IChatClient newClient) throws RemoteException
    {
        //Busca el topico en el hashtable
        //Si existe asigna un nuevo objeto apuntando a la sub-tabla existente
        //Si no existe crea una nueva sub-tabla
        
        Hashtable<String, IChatClient> chatters;
        if(topics.containsKey(topic))
        {
            chatters = topics.get(topic);
            for(IChatClient client : chatters.values())
                client.receiveEnter(name);
        }
        else
        {
            chatters = new Hashtable<>();
            topics.put(topic, chatters);
            newClient.receiveNewTopic(topic);
        }
        
        //Registra al nuevo usuario
        
        chatters.put(name, newClient);
        System.out.println("Client " + name + " logged in on topic " + topic);
        
    }
    
    @Override
    public synchronized void logout(String name, String topic) throws RemoteException
    {
        //Obtiene sub-tabla del topico
        Hashtable<String, IChatClient> chatters = topics.get(topic);
        
        //Por cada cliente en el hash se invoca el método Exit para notificar que salió un nuevo cliente
        for(IChatClient client : chatters.values())
            client.receiveExit(name);
        
        //Se elimina cliente del hash
        chatters.remove(name);
        
        System.out.println("Client + " + name + " has logged out from tipic " + topic);
    }
    
    @Override
    public synchronized void send(Message message, String topic) throws RemoteException
    {
        //Obtiene sub-tabla del topico
        Hashtable<String, IChatClient> chatters = topics.get(topic);
        
        //Por cada cliente en el hash se invoca el método receiveMessage que recibe el mensaje
        for(IChatClient client : chatters.values())
            client.receiveMessage(message);
        
        System.out.println("Mensaje de " + message.name + ":\n" + message.text);
    }
    
    //Main
    
    public static void main(String[] args)
    {
        //Establece carpeta en la ruta relativa a la ejecución del programa
        String downloadLocationsPath = "file:C://Users/icloud/NetBeansProjects/rmi/codebase/";
        
        //Nombre del objeto que identifica al servidor
        String serverUrl = "///michat";
        
        try
        {
            //Por lo que entiendo, se le asigna de forma muy reflextion la propiedad codebase
            System.setProperty("java.rmi.server.codebase", downloadLocationsPath);
            //System.setProperty("java.rmi.server.hostname", "localhost");
            
            //Instancia de esta clase
            ChatServer server = new ChatServer();
            
            //Registrar esta clase, no un objeto como en el tutorial
            Naming.rebind(serverUrl, server);
            System.out.println("Chat listo");
        }
        catch(Exception e)
        {
            System.err.println("ERROR:\n" + e.getMessage()); 
            e.printStackTrace();
        }
    }
}
