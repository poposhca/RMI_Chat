
package topicschat;

import java.io.*;

public class Message implements Serializable
{
    public String name;
    public String text;
    
    public  Message(String name, String text)
    {
        this.name = name;
        this.text = text;
    }
}
