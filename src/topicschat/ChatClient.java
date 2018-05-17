
package topicschat;

import java.util.Scanner;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;

public class ChatClient extends UnicastRemoteObject implements IChatClient
{
    
    //Propiedades
    public String name;
    public String topic;
    IChatServer server;
    String serverURL;
    
    public ChatClient(String name, String topic, String url) throws RemoteException
    {
        this.name = name;
        this.topic = topic;
        this.serverURL = url;
        connect();
    }
    
    //Métodos
    
    //Se conecta con el servidor y se regostra en la tabla hash
    private void connect()
    {
        try
        {
            //Obtiene conección con el server usando el método Naming en lugar del registry
            this.server = (IChatServer) Naming.lookup("rmi://"+this.serverURL+"/michat");
            //Se registra en la tabla hash
            server.login(name, topic, this);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    private void disconnect()
    {
        try
        {
            //Se elimina de la tabla hash
            server.logout(name, topic);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
    
    //Manda un string al servidor para ser progpagado por los demás
    private void sendTextToChat(String text) 
    {
        try 
        {
            server.send(new Message(name,text), topic);
        }
        catch( RemoteException e ) 
        {
            System.err.println(e.getMessage());
        }
    }

    //Implementación de la interfaz
    
    @Override
    public void receiveEnter(String name)
    {
        System.out.println("Log in " + name);
        System.out.println(name + "-- Cadena a enviar: ");
    }
    
    @Override
    public void receiveExit(String name)
    {
        System.out.println("Log out " + name);
        
        //Si el nombre del cliente es el de la actual proceso, entonces se termina el programa
        if(name.equals(this.name))
            System.exit(0);
        
        System.out.println(name + "-- Cadena a enviar: ");
    }
    
    @Override
    public void receiveMessage(Message message)
    {
        System.out.println(message.name + ":\n" + message.text);
        System.out.println(name + "-- Cadena a enviar: ");
    }
    
    @Override
    public void receiveNewTopic(String topic)
    {
        System.out.println("Nuevo topico creado: " + topic);
    }
    
    //Métodos estáticos
    
    //Este mñetodo se encarga de pedir un input al usuario desde stdin
    public static String pideCadena(String letrero)
    {
        StringBuffer strDato = new StringBuffer();
        String strCadena = "";	
        try {
                System.out.print(letrero);
                //Lee el mensaje a enviar
                BufferedInputStream bin = new BufferedInputStream(System.in);
                //Covierte el input en un arreglo de bytes
                byte bArray[] = new byte[256];
                int numCaracteres = bin.read(bArray);
                while (numCaracteres==1 && bArray[0]<32)
                        numCaracteres = bin.read(bArray);
                for(int i=0;bArray[i]!=13 && bArray[i]!=10 && bArray[i]!=0; i++) 
                        strDato.append((char) bArray[i]);
                strCadena = new String( strDato );
        }
        catch( IOException ioe ) {
                System.out.println(ioe.toString());
        }
        return strCadena;
    }
    
    public static void main (String[] args)
    {
        //Si hay argumentos, usar el argumento 0 como nombre del cliente
        String chatName;
        if(args.length > 0)
            chatName = args[0];
        else
            chatName = "prueba";
        
        //Direccion ip del servidor
        String ipDir;
        if(args.length == 2)
            ipDir = args[1];
        else
            ipDir = "127.0.0.1";
        
        //Scanner para leer de la consola
        Scanner scanner = new Scanner(System.in);
        
        //String para los mensajes del chat
        String strCad;
        
        //Ciclo del chat
        try 
        {
            //¿A qué topico prtense el cliente?
            System.out.print("Escribe el topico del chat: ");
            String topic = scanner.next();
            //Crea un nuevo cliente
            ChatClient clte = new ChatClient(chatName, topic, ipDir);
            //Leer cadena que contiene el mensaje
            strCad = "";  
            //ciclo que mantiene conectado al cliente
            while(strCad.equals("quit") == false) 
            {          
                    clte.sendTextToChat(strCad);
                    strCad = pideCadena(chatName + "-- Cadena a enviar: ");
            }    
            System.out.println("Local console "+clte.name+", going down");
            clte.disconnect();
        }
        catch( RemoteException e ) 
        {
                System.err.println(e.getMessage());
        }

    }
}
