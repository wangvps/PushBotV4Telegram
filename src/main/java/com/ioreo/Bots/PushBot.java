package com.ioreo.Bots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.shiro.session.Session;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.session.TelegramLongPollingSessionBot;

import com.ioreo.Utils.CommandParser;
import com.ioreo.Utils.DataBaseX;

public class PushBot extends TelegramLongPollingSessionBot {

    private static Session session;
    private static String[] adminIDs;
    private static String[] channelIDs;


    @Override
    public void onUpdateReceived(Update update, Optional<Session> botSession) {
        String adminIDsString = System.getenv("ADMIN_IDs");
        String channelIDsString = System.getenv("CHANNEL_IDs");

        if(adminIDsString != null && adminIDsString.contains(","))
        {
            adminIDs = adminIDsString.split(",");
        }
        else
        {
            adminIDs = new String[] {adminIDsString};
        }
        if(channelIDsString != null && channelIDsString.contains(","))
        {
            channelIDs = channelIDsString.split(",");
        }
        else
        {
            channelIDs = new String[] {channelIDsString};
        }
        session = botSession.get();
        if(session.getAttribute("Progress")==null)
        {
            session.setAttribute("Progress", 0);
        }
        if(update.hasMessage() && (update.getMessage().hasText() || update.getMessage().hasPhoto()) && update.getMessage().isUserMessage())
        {
            Message message = update.getMessage();
            if(message.isCommand()&&CommandParser.parse(message, getBotUsername()).size()>0)
            {
                List<String> commandList = CommandParser.parse(message, getBotUsername());
                switch(commandList.get(0))
                {
                    case "/create":
                        if(getProgress()==0)
                        {
                            cleanSession();
                            setProgress(1);
                            sendSimpleMessage(message.getFrom().getId().toString(), "?????????????????????");
                            sendSimpleMessage(message.getFrom().getId().toString(), "???????????????????????????");
                        }
                        else
                        {
                            sendSimpleMessage(message.getFrom().getId().toString(), "????????????????????????????????????????????????????????? /cancel");
                        }
                        break;
                    case "/cancel":
                        if(getProgress()==0)
                        {
                            sendSimpleMessage(message.getFrom().getId().toString(), "???????????????????????????????????????????????? /create");
                        }
                        else
                        {
                            cleanSession();
                            setProgress(0);
                            sendSimpleMessage(message.getFrom().getId().toString(), "?????????????????????");
                        }
                        break;
                    case "/start":
                        sendSimpleMessage(message.getFrom().getId().toString(), "??????????????????????????????????????? /create ????????????\n?????????????????????????????????????????? /cancel \n"+
                                "???????????????????????????????????? /help ?????????????????? \n"+
                                "?????????????????????????????????????????????????????? @wangran10 \n UniPushBot Version V Made by UniOreoX");
                        break;
                    case "/pass":
                        String chatID = message.getFrom().getId().toString();
                        if(adminIDsString.contains(chatID))
                        {
                            try {
                                if(message.getReplyToMessage()!=null)
                                {
                                    String id = message.getReplyToMessage().getMediaGroupId();
                                    pushToChannel(message.getReplyToMessage().getMessageId(),id);
                                    sendSimpleMessage(chatID, "??????????????????ID"+message.getReplyToMessage().getMessageId());
                                    DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatID).messageId(message.getReplyToMessage().getMessageId()).build();
                                    try {
                                        execute(deleteMessage);
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    sendSimpleMessage(chatID, "????????????????????????????????????");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            
                        }
                        else
                        {
                            sendSimpleMessage(chatID, "??????????????????????????????");
                        }
                        break;
                    case "/reject":
                        String chatID2 = message.getFrom().getId().toString();
                        if(adminIDsString.contains(chatID2))
                        {
                            if(message.getReplyToMessage()!=null)
                            {
                                sendSimpleMessage(chatID2, "??????????????????ID"+message.getReplyToMessage().getMessageId());
                                DeleteMessage deleteMessage = DeleteMessage.builder().chatId(chatID2).messageId(message.getReplyToMessage().getMessageId()).build();
                                DataBaseX delData = null;
                                try {
                                    execute(deleteMessage);
                                    delData = new DataBaseX();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String contentString = delData.get(message.getReplyToMessage().getMessageId(),message.getMediaGroupId()).get(0);
                                String autherChatID = delData.get(message.getReplyToMessage().getMessageId(),message.getMediaGroupId()).get(2);
                                String reason = null;
                                if(commandList.size()==2)
                                {
                                    reason = commandList.get(1);
                                }
                                else
                                {
                                    reason = "?????????";
                                }
                                
                                sendSimpleMessage(autherChatID, "?????????????????????????????????\n"+contentString+"\n\n?????????????????????\n\n?????????"+reason);
                                delData.delete(message.getReplyToMessage().getMessageId(),message.getReplyToMessage().getMediaGroupId());
                                delData.close();
                            }
                            else
                            {
                                sendSimpleMessage(chatID2, "????????????????????????????????????");
                            }
                        }
                        else
                        {
                            sendSimpleMessage(chatID2, "??????????????????????????????");
                        }
                        break;
                    case "/clean":
                        String userChatID = message.getFrom().getId().toString();
                        if(userChatID.equals(adminIDs[0]))
                        {
                            DataBaseX dataBaseX = null;
                            try {
                                dataBaseX = new DataBaseX();
                                sendSimpleMessage(userChatID, "????????????????????????");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dataBaseX.removeTable();
                            dataBaseX.close();
                        }
                        break;
                    case "/help":
                        sendSimpleMessage(message.getFrom().getId().toString(),
                        "<b>????????????</b>\n"
                        +"????????????\n/start ????????????\n/create ???????????????\n/cancel ????????????????????????\n"
                        +"???????????????\n/pass ????????????\n/reject ????????????\n/clean ?????????????????????????????????????????????<s>????????????</s>?????????\n"
                        +"???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????\n"
                        +"???????????????????????????????????????????????????????????????????????????????????????\n");
                        break;
                }
            }
            else
            {
                switch (getProgress())
                {
                    case 0:
                        sendSimpleMessage(message.getFrom().getId().toString(), "???????????????????????????????????????????????? /create");
                        break;
                    case 1:
                        if(message.hasText())
                        {
                            String title = message.getText();
                            session.setAttribute("Title", title);
                            sendSimpleMessage(message.getFrom().getId().toString(), "??????????????????????????????????????????<b>??????</b>??????(<b>??????HTML</b>)");
                            setProgress(2);
                        }
                        break;
                    case 2:
                        if(message.hasText())
                        {
                            String content = message.getText();
                            session.setAttribute("Content", content);
                            sendSimpleMessage(message.getFrom().getId().toString(), "??????????????????????????????????????????????????????\n<b>????????????</b>???#????????????????????????");
                            setProgress(3);
                        }
                        break;
                    case 3:
                        if(message.hasText())
                        {
                            String tag = message.getText();
                            session.setAttribute("Tag", tag);
                            sendSimpleMessage(message.getFrom().getId().toString(), "???????????????????????????????????????????????????<b>?????????????????????????????????</b>\n?????????????????????<b>????????????????????????</b>??????");
                            setProgress(4);
                        }
                        break;
                    case 4:
                        if(session.getAttribute("Photos")==null)
                        {
                            session.setAttribute("Photos", new ArrayList<String>());
                        }
                        if(message.hasPhoto())
                        {
                            List<PhotoSize> photoList = message.getPhoto();
                            List<String> photos = (List<String>) session.getAttribute("Photos");
                            photos.add(photoList.get(2).getFileId());
                            session.setAttribute("Photos", photos);
                        }
                        else if(message.hasText())
                        {
                            String chatID = message.getFrom().getId().toString();
                            String fullName ;
                            if(message.getFrom().getFirstName()==null)
                            {
                                fullName = message.getFrom().getLastName();
                            }
                            else if(message.getFrom().getLastName()==null)
                            {
                                fullName = message.getFrom().getFirstName();
                            }
                            else
                            {
                                fullName = message.getFrom().getFirstName()+" "+message.getFrom().getLastName();
                            }
                            session.setAttribute("FullName", fullName);
                            session.setAttribute("ChatID", chatID);
                            setProgress(5);
                            sendPreview(chatID).getMessageId();
                            KeyboardButton keyboardConfirmButton = KeyboardButton.builder()
                            .text("#??????")
                            .build();
                            KeyboardButton keyboardCancelButton = KeyboardButton.builder()
                            .text("#??????")
                            .build();
                            KeyboardRow keyboardRow = new KeyboardRow();
                            keyboardRow.add(keyboardConfirmButton);
                            keyboardRow.add(keyboardCancelButton);
                            ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                            .clearKeyboard()
                            .resizeKeyboard(true)
                            .oneTimeKeyboard(true)
                            .keyboardRow(keyboardRow)
                            .build();
                            SendMessage sendMessage = SendMessage.builder()
                            .chatId(message.getFrom().getId())
                            .disableNotification(false)
                            .parseMode(ParseMode.HTML)
                            .replyMarkup(replyKeyboardMarkup)
                            .text("?????????????????????????????????????????????????????????????????????")
                            .build();
                            try {
                                execute(sendMessage);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case 5:
                        if(message.getText().equals("#??????"))
                        {
                            pushToAdmin();
                            sendSimpleMessage(message.getFrom().getId().toString(), "?????????????????????????????????");
                            cleanSession();
                        }
                        else
                        {
                            sendSimpleMessage(message.getFrom().getId().toString(), "?????????????????????");
                        }
                        break;
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    private int getProgress() {
        return (int) session.getAttribute("Progress");
    }

    private void setProgress(int progress) {
        session.setAttribute("Progress", progress);
    }

    private Message sendSimpleMessage(String chatID, String text) {
        SendMessage sendMessage = SendMessage.builder()
        .chatId(chatID)
        .text(text)
        .parseMode(ParseMode.HTML)
        .build();
        try {
            Message message = execute(sendMessage);
            return message;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void cleanSession()
    {
        session.setAttribute("Progress", 0);
        session.removeAttribute("Title");
        session.removeAttribute("Content");
        session.removeAttribute("Tag");
        session.removeAttribute("Photos");
        session.removeAttribute("FullName");
        session.removeAttribute("ChatID");
    }

    private String getPreview() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>???</b>"+session.getAttribute("Title")+"<b>???</b>");
        stringBuilder.append("\n\n");
        stringBuilder.append(session.getAttribute("Content"));
        stringBuilder.append("\n\n");
        stringBuilder.append("<b>?????? </b>"+session.getAttribute("Tag"));
        stringBuilder.append("\n\n");
        stringBuilder.append("<b>????????? </b> <a href=\"tg://user?id="+session.getAttribute("ChatID")+"\">"+session.getAttribute("FullName")+"</a>");

        return stringBuilder.toString();
    }

    private Message sendPreview(String chatID)
    {
        List<String> photos = (List<String>) session.getAttribute("Photos");
        if(photos.size()==0)
        {   
            SendMessage sendMessage = SendMessage.builder()
            .chatId(chatID)
            .parseMode(ParseMode.HTML)
            .text(getPreview())
            .disableNotification(false)
            .protectContent(true)
            .build();

            try {
                Message message = execute(sendMessage);
                return message;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else if(photos.size()==1)
        {
            SendPhoto sendPhoto = SendPhoto.builder()
            .chatId(chatID)
            .parseMode(ParseMode.HTML)
            .photo(new InputFile(photos.get(0)))
            .caption(getPreview())
            .disableNotification(false)
            .build();

            try {
                Message message = execute(sendPhoto);
                return message;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        else
        {
            List<InputMedia> inputMedias = new ArrayList<>();
            for(int i = 0; i < photos.size(); i++)
            {
                InputMediaPhoto inputMediaPhoto = InputMediaPhoto.builder()
                .parseMode(ParseMode.HTML)
                .media(photos.get(i))
                .build();
                if(i==0)
                {
                    inputMediaPhoto.setCaption(getPreview());

                }
                inputMedias.add(inputMediaPhoto);

            }
            SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
            .chatId(chatID)
            .protectContent(true)
            .disableNotification(false)
            .medias(inputMedias)
            .build();
            try {
                Message message = execute(sendMediaGroup).get(0);
                return message;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void pushToAdmin()
    {
        List<String> photos = (List<String>) session.getAttribute("Photos");
        if(photos.size()==0)
        {
            for(int i = 0; i<adminIDs.length; i++)
            {
                try {
                    Thread.sleep(2*1000);
                    SendMessage sendMessage = SendMessage.builder()
                    .chatId(adminIDs[i])
                    .parseMode(ParseMode.HTML)
                    .text(getPreview())
                    .disableNotification(false)
                    .protectContent(true)
                    .build();
                    Message message = execute(sendMessage);
                    pushToDB(message);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            
        }
        else if(photos.size()==1)
        {
            String photo = photos.get(0);
            for(int i = 0; i<adminIDs.length; i++)
            {
                try {
                    Thread.sleep(2*1000);
                    SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(adminIDs[i])
                    .parseMode(ParseMode.HTML)
                    .photo(new InputFile(photo))
                    .disableNotification(false)
                    .protectContent(true)
                    .caption(getPreview())
                    .build();
                    Message message = execute(sendPhoto);
                    pushToDB(message);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                
            }
        }
        else
        {
            List<InputMedia> inputMedias = new ArrayList<>();
            for(int i = 0; i < photos.size(); i++)
            {
                InputMediaPhoto inputMediaPhoto = InputMediaPhoto.builder()
                .parseMode(ParseMode.HTML)
                .media(photos.get(i))
                .build();
                if(i==0)
                {
                    inputMediaPhoto.setCaption(getPreview());
                }
                inputMedias.add(inputMediaPhoto);

            }
            for (int i = 0; i<adminIDs.length; i++)
            {
                try {
                    Thread.sleep(2*1000);
                    SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                    .chatId(adminIDs[i])
                    .protectContent(true)
                    .disableNotification(false)
                    .medias(inputMedias)
                    .build();
                    Message message = execute(sendMediaGroup).get(0);
                    pushToDB(message);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    private void pushToDB(Message message)
    {
        DataBaseX dataBaseX = null;
        try {
            dataBaseX = new DataBaseX();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> photos = (List<String>) session.getAttribute("Photos");
        StringBuilder stringBuilder = new StringBuilder();
        photos.forEach(photo -> {
            stringBuilder.append(",");
            stringBuilder.append(photo);
        });
        String imgs = stringBuilder.toString().replaceFirst(",", "");
        String id = null;
        System.out.print(message.getMediaGroupId());
        if(message.getMediaGroupId()==null)
        {
            id = message.getMessageId().toString();
        }
        else
        {
            id = message.getMediaGroupId().toString();
        }
        dataBaseX.insert(getPreview(), imgs, session.getAttribute("ChatID").toString(), message.getMessageId(),id);
        dataBaseX.close();
    }

    private void pushToChannel(int messageid,String mediagroupid)
    {
        DataBaseX dataBaseX = null;
        try {
            dataBaseX = new DataBaseX();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> data = dataBaseX.get(messageid,mediagroupid);
        String content = data.get(0);
        String photos = data.get(1);
        String chatid = data.get(2);
        sendSimpleMessage(chatid, "??????????????????????????????\n"+content+"\n\n????????????????????????");
        List<String> photoList = new ArrayList<>();
        dataBaseX.delete(messageid,mediagroupid);
        dataBaseX.close();
        if(photos.contains(","))
        {
            photoList = Arrays.asList(photos.split(","));
        }
        else
        {
            photoList = new ArrayList<>();
            photoList.add(photos);
        }

        if(photoList.size()==0||photoList.get(0).isEmpty())
        {
            for(int i = 0; i<channelIDs.length; i++)
            {
                try {
                    Thread.sleep(2*1000);
                    SendMessage sendMessage = SendMessage.builder()
                    .chatId(channelIDs[i])
                    .parseMode(ParseMode.HTML)
                    .text(content)
                    .disableNotification(false)
                    .protectContent(false)
                    .build();
                    execute(sendMessage);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            
        }
        else if(photoList.size()==1)
        {
            String photo = photoList.get(0);
            for(int i = 0; i<channelIDs.length; i++)
            {
                try {
                    Thread.sleep(2*1000);
                    SendPhoto sendPhoto = SendPhoto.builder()
                    .chatId(channelIDs[i])
                    .parseMode(ParseMode.HTML)
                    .photo(new InputFile(photo))
                    .disableNotification(false)
                    .protectContent(false)
                    .caption(content)
                    .build();
                    execute(sendPhoto);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                
            }
        }
        else
        {
            List<InputMedia> inputMedias = new ArrayList<>();
            for(int i = 0; i < photoList.size(); i++)
            {
                InputMediaPhoto inputMediaPhoto = InputMediaPhoto.builder()
                .parseMode(ParseMode.HTML)
                .media(photoList.get(i))
                .build();
                if(i==0)
                {
                    inputMediaPhoto.setCaption(content);

                }
                inputMedias.add(inputMediaPhoto);

            }
            for (int i = 0; i<channelIDs.length; i++)
            {
                try {
                    Thread.sleep(2*1000);
                    SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                    .chatId(channelIDs[i])
                    .protectContent(false)
                    .disableNotification(false)
                    .medias(inputMedias)
                    .build();
                    execute(sendMediaGroup);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                
            }
        }
    }
    
}
