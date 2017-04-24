package edu.utexas.ee360p_teamproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.ee360p_teamproject.ClientRequestHandler.RequestHandler;

import static java.lang.Thread.sleep;


public class ChatRoomActivity extends AppCompatActivity {

    private String myName;
    private int messagesReceived;
    private Thread notificatonHandler;

    private List<String> chats = new ArrayList<>();
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        Bundle extras = intent.getExtras();
        //receive port number and name
        myName = extras.getString("EXTRA_USERNAME");
        String thisRoom = extras.getString("EXTRA_ROOM");
        messagesReceived = 0;
        context = getApplicationContext();

        TextView chatRoom = (TextView) findViewById(R.id.chatRoomTitle);
        EditText message = (EditText) findViewById(R.id.messageToSend);

        chatRoom.setText(thisRoom);

        //get the messages that are already in the room
        displayNewNotifications();


//        notificatonHandler = new Thread(runnable);
//        notificatonHandler.start();


        // listener for send messages
        final Button sendMessage = (Button) findViewById(R.id.sendMessage);

        sendMessage.setOnClickListener(view -> sendIfValid(message));

        message.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendIfValid(message);
                handled = true;
            }
            return handled;
        });

    }

    private void sendIfValid(EditText message) {
        String msgString = message.getText().toString();
        if (msgString.contains("\n") || msgString.equals(""))
            return;

        displayNewMessage(msgString);
        message.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void displayNewMessage(String msgString) {
        MessageC toSend = new MessageC(myName,
                                       msgString,
                                       System.currentTimeMillis());
        RequestHandler.sendMessage(toSend);
        displayNewNotifications();
    }

    private void displayNewNotifications() {
        //call function to get updates
        List<MessageC> notifications = RequestHandler.notifications(messagesReceived);

        //add new messages in a queue
        if ((notifications == null) || notifications.isEmpty())
            return;

        ListView chatList = (ListView) findViewById(R.id.chatList);
        for (int i = 0; i < notifications.size(); i++) {
            long milliseconds = notifications.get(i).timestamp;
            int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
            int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);


            String totalChat = hours + ":" + minutes + " " + notifications.get(i).author + ": " + notifications.get(i).content;
            chats.add(totalChat);
            messagesReceived++;
        }

        String[] values = chats.toArray(new String[chats.size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1, values);

        chatList.setAdapter(adapter);
    }

//    Runnable runnable = () -> {
//
//        while (true) {
//            //call function to get updates
//            List<MessageC> notifications = RequestHandler.notifications(messagesReceived);
//
//            //no new messages received
//            if ((notifications == null) || notifications.isEmpty())
//                continue;
//
//            //add new messages in a queue
//            ListView chatList = (ListView) findViewById(R.id.chatList);
//            for (int i = 0; i < notifications.size(); i++) {
//                String totalChat = notifications.get(i).timestamp + " " + notifications.get(i).author + ": " + notifications.get(i).content;
//                chats.add(totalChat);
//                messagesReceived++;
//            }
//
//            String[] values = chats.toArray(new String[chats.size()]);
//
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, android.R.id.text1, values);
//
//            chatList.setAdapter(adapter);
//
//            try {
//                Thread.sleep(5000);
//            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//    };

}
