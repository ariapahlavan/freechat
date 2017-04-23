package edu.utexas.ee360p_teamproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.utexas.ee360p_teamproject.ClientRequestHandler.RequestHandler;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private TextView t;

    public static final String EXTRA_MESSAGE = "com.example.chatroomclient.MESSAGE";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ///comment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner roomSelector = (Spinner)findViewById(R.id.chatRoomSelector);

        //ask coordinator for available rooms in strings and replace rooms below with those strings

        List<String> chatrooms = RequestHandler.listOfAllRooms();
        chatrooms.add("Other");
        String[] items = chatrooms.toArray(new String[chatrooms.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        roomSelector.setAdapter(adapter);

        //from Aria
//        testConnectionToServer();
    }



    public void enterRoom(View view){
        Intent intent = new Intent(this, ChatRoomActivity.class);
        EditText username = (EditText) findViewById(R.id.editUsername);
        String name = username.getText().toString();

        Spinner roomSelector = (Spinner)findViewById(R.id.chatRoomSelector);
        String room = roomSelector.getSelectedItem().toString();

        if(room == "Other"){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("New Chat Room");
            alert.setMessage("Enter Chat Room Name");

            // Set an EditText view to get user input
            final EditText input = new EditText(this);
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {


                    String newRoom = input.getText().toString();
                    RequestHandler.enterChatroom(newRoom);

                    Bundle extras = new Bundle();
                    extras.putString("EXTRA_USERNAME", name);
                    extras.putString("EXTRA_ROOM", newRoom);
                    intent.putExtras(extras);


                    startActivity(intent);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        }
        else{
            // setRoom(String)  send chosen chatroom to middle man who will comment client to chatroom
            RequestHandler.enterChatroom(room);

            Bundle extras = new Bundle();
            extras.putString("EXTRA_USERNAME", name);
            extras.putString("EXTRA_ROOM", room);
            intent.putExtras(extras);


            startActivity(intent);
        }


    }

    /**
     * This is just to demo the usage of RequestHandler class
     */
    private void testConnectionToServer() {
        //getting the list of all available chatrooms
        List<String> chatrooms = RequestHandler.listOfAllRooms();

        if (chatrooms == null){
            //server connection failed
            Log.d(TAG, "connections failed :(");
        }
        else if(chatrooms.isEmpty()){
            //no rooms available (this is an error)
            Log.d(TAG, "No rooms received from server :(");
        }else {
            chatrooms.forEach(chatroom -> Log.d(TAG, "chatroom: " + chatroom));
        }

        //choosing a chatroom to enter
        RequestHandler.enterChatroom("Default");

        //sending a new message to current chatroom
        RequestHandler.sendMessage(new MessageC("Aria", "Hello bananas!", System.currentTimeMillis()));

        //getting new notifications
        List<MessageC> notifications = RequestHandler.notifications(0);
        if (notifications == null){
            //server connection failed
            Log.d(TAG, "connections failed :(");
        }
        else if (notifications.isEmpty()){
            //no new notifications
            Log.d(TAG, "no new notification available :(");
        }
        else {
            //you have new notifications
            notifications.forEach(notification -> Log.d(TAG, "notification: " + notification));
        }
    }


}
