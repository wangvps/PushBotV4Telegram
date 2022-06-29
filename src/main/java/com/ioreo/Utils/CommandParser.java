package com.ioreo.Utils;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.Message;

public class CommandParser {

    public static List<String> parse(Message message, String userName)
    {
        String command = message.getText();
        List<String> parsedList = new ArrayList<String>();
        if(command.replace(" ", "").startsWith("/"))
        {
            String[] commandSplit = command.split(" ");
            for(int i = 0; i<commandSplit.length; i++)
            {
                if(!commandSplit[i].isEmpty()&&!commandSplit[i].isBlank())
                {
                    parsedList.add(commandSplit[i]);
                }
            }
            
        }
        
        return parsedList;
    }
}
