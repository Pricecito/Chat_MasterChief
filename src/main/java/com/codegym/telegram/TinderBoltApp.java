package com.codegym.telegram;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends SimpleTelegramBot {

    public static final String TELEGRAM_BOT_TOKEN = "7498293621:AAEvghZVltxVOJaWDWgnj3TnDuaLdHzsS54"; //TODO: añadir el token del bot entre comillas
    public static final String OPEN_AI_TOKEN = "gpt:yeSMIRCC7ePMWDmNqAy9JFkblB3T8MQ6g521gef0dCT5qsls"; //TODO: añadir el token de ChatGPT entre comillas

    private ChatGPTService chatGPT=new ChatGPTService(OPEN_AI_TOKEN);
    private DialogMode mode; //variable para realizar los cambios de modalidad de GPT
    ArrayList <String> list=new ArrayList<>();

    public TinderBoltApp() {
        super(TELEGRAM_BOT_TOKEN);
    }

    public void startCommand(){

        mode=DialogMode.MAIN;

        //cargar mensaje de archivo de texto
        String text = loadMessage("main");
        sendPhotoMessage("main");
        sendTextMessage(text);

        showMainMenu(
                "start","Menu Principal",
                "profile","generación de perfil de Tinder \uD83D\uDE0E",
                "opener","mensaje para iniciar conversación \uD83E\uDD70",
                "message","correspondencia en su nombre \uD83D\uDE08",
                "date","correspondencia con celebridades \uD83D\uDD25",
                "gpt","hacer una pregunta a chat GPT \uD83E\uDDE0"
        );
    }
    public void gptCommand(){
        mode=DialogMode.GPT;
        //dar la instruccion al usuario
        String text= loadMessage("gpt");
        sendPhotoMessage("gpt");
        sendTextMessage(text);
    }

    public void gptDialog(){
        //Extrayendo consulta
        String text=getMessageText();
        //Almacenando prompt o petición de la carpeta prompt
        String prompt=loadPrompt("gpt");
        var myMessage= sendTextMessage("user is typing...");
        String answer=chatGPT.sendMessage(prompt,text);

        updateTextMessage(myMessage,answer);
    }

    public void dateCommand(){
        mode=DialogMode.DATE;
        //dar la instruccion al usuario
        String text= loadMessage("date");
        sendPhotoMessage("date");
        sendTextButtonsMessage(text,
            "date_grande","Ariana Grande",
                "date_robbie","Margot Robbie",
                "date_zendaya","Zendaya",
                "date_gosling","Ryan Gosling",
                "date_hardy","Tom Hardy");
    }

    public void dateButton(){
        String key=getButtonKey();
        //Metodo para poder enviar mensaje con simbolos reservados *texto* / _texto_
        sendPhotoMessage(key);
        sendHtmlMessage(key);
        String prompt=loadPrompt(key);
        chatGPT.setPrompt(prompt);
    }

    public void dateDialog(){
        String text=getMessageText();
        var myMessage= sendTextMessage("user is typing...");
        //para mantenernernos en el mismo chat
        String answer=chatGPT.addMessage(text);
//        sendTextMessage(answer);
        updateTextMessage(myMessage,answer);
    }

    public void messageCommand(){
        mode=DialogMode.MESSAGE;
        String text=loadMessage("message");
        sendPhotoMessage("message");
        sendTextButtonsMessage(text,
                "message_next", "Escrbir el siguiente mensaje.",
                "message_date", "Invitar a la persona.");
        list.clear();
    }

    public void messageButton(){
        String key=getButtonKey();
        String prompt= loadPrompt(key);
        String history=String.join("\n\n",list);
        var myMessage=sendTextMessage("Chat GPT is typing ...");
        String answer=chatGPT.sendMessage(prompt,history);
        updateTextMessage(myMessage,answer);
    }

    public void messageDialog(){
        String text=getMessageText();
        list.add(text);
    }

    private UserInfo user= new UserInfo();
    private int questionCount=0;

    public void profileCommand(){
        mode=DialogMode.PROFILE;
        String text=loadMessage("profile");
        sendPhotoMessage("profile");
        sendTextMessage(text);

        sendTextMessage("Ingresa tu nombre: ");
        user = new UserInfo();
        questionCount=0;
    }
    public void profileDialog(){
        String text= getMessageText();
        questionCount++;
        if(questionCount==1){
            user.name=text;
            sendTextMessage("Ingresa tu edad:");
        }else if(questionCount==2){
            user.age=text;
            sendTextMessage("Ingresa tu actividad de tiempo libre:");
        }else if(questionCount==3){
            user.hobby=text;
            sendTextMessage("Ingresa tu objetivo para interactuar con esta persona:");
        }else if(questionCount==4) {
            user.goals = text;
            String prompt = loadPrompt("profile");
            String userInfo = user.toString();
            var myMessage = sendTextMessage("Chat GPT is typing ...");
            String answer = chatGPT.sendMessage(prompt, userInfo);
            updateTextMessage(myMessage, answer);
        }
    }

    public void openerCommand(){
        mode=DialogMode.OPENER;
        String text=loadMessage("opener");
        sendPhotoMessage("opener");
        sendTextMessage(text);
        sendTextMessage("Ingresa su nombre: ");
        user = new UserInfo();
        questionCount=0;
    }

    public void openerDialog(){
        String text= getMessageText();
        questionCount++;
        if(questionCount==1){
            user.name=text;
            sendTextMessage("Ingresa su edad: ");
        }else if(questionCount==2){
            user.age=text;
            sendTextMessage("En que trabaja: ");
        }else if(questionCount==3){
            user.hobby=text;
            sendTextMessage("En la escala de 1 al 10 que tan atractiva es la persona: ");
        }else if(questionCount==4) {
            user.goals = text;

            String prompt = loadPrompt("opener");
            String userInfo = user.toString();

            var myMessage = sendTextMessage("Chat GPT is typing ...");
            String answer = chatGPT.sendMessage(prompt, userInfo);
            updateTextMessage(myMessage, answer);
        }
    }

    public void hello() {
        if (mode == DialogMode.GPT) {
            gptDialog();
        } else if (mode == DialogMode.DATE) {
            dateDialog();
        } else if (mode == DialogMode.MESSAGE) {
            messageDialog();
        } else if (mode==DialogMode.PROFILE){
            profileDialog();
        }else if(mode==DialogMode.OPENER) {
            openerDialog();
        }else{
            String text = getMessageText();
            sendTextMessage("*Hello*");
            sendTextMessage("_How ara you?_");
            sendTextMessage("Yoy wrote: " + text);
            //Mandar imagenes
            sendPhotoMessage("avatar_main");
            //Agregar botones metodo(Texto contexto, teto_identificador, texto_contenido)
            sendTextButtonsMessage("Launch process",
                    "Start", "Start",
                    "Stop", "Stop");
        }
    }
    public void helloButton(){
        String key= getButtonKey();
        switch(key){
            case "Start": sendTextMessage("Acabas de iniciar el proceso");break;
            case "Stop":sendTextMessage("Acabas de parar el proceso");break;
            case "Saludar": sendPhotoMessage("avatar_main"); sendTextMessage("Holaaaaaa");break;
                default:sendTextMessage("Adios  :(");
        }
    }

    @Override
    public void onInitialize() {
        //agregar manejo de comandos
        addCommandHandler("start",this::startCommand);
        addCommandHandler("gpt",this::gptCommand);
        addCommandHandler("date",this::dateCommand);
        addCommandHandler("message",this::messageCommand);
        addCommandHandler("profile",this::profileCommand);
        addCommandHandler("opener",this::openerCommand);
        //agregar manejo de comandos
        addMessageHandler(this::hello);
        //agregar manejo de botones
        //^.* identifica cualquier boton
//        addButtonHandler("^.*",this::helloButton);
        addButtonHandler("^date_.*",this::dateButton);
        addButtonHandler("^message_.*",this::messageButton);

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
