package au.com.pixelforcesystems.yeahoh;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.ActionCableException;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Consumer;
import com.hosopy.actioncable.Subscription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Text {

    public static void main(String[] arg) {
        // 1. Setup
        URI uri = null;
        try {
            uri = new URI("ws://10.88.1.163:28080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // Consumer consumer = ActionCable.createConsumer(uri);
        Consumer.Options options = new Consumer.Options();

        // header
        Map<String, String> headers = new HashMap();
        headers.put("X-USER-ID", "3");
        // headers.put("user_id", "1");
        options.headers = headers;

        // parameters
        Map<String, String> query = new HashMap();
        query.put("access_token", "xxxxxxxxxx");
        options.query = query;

        options.reconnection = true;
        options.reconnectionMaxAttempts = 5;

        Consumer consumer = ActionCable.createConsumer(uri, options);

        // 2. Create subscription
        Channel appearanceChannel = new Channel("CommentsChannel");
        // Channel appearanceChannel = new Channel("Channel");
        appearanceChannel.addParam("data", "");
        // appearanceChannel.addParam("data-channel", "comments");
        // appearanceChannel.addParam("data-message-id", "1");
        final ChatSubscription subscription = consumer.getSubscriptions().create(appearanceChannel, ChatSubscription.class);

        subscription
                .onConnected(new Subscription.ConnectedCallback() {
                    @Override
                    public void call() {
                        // Called when the subscription has been successfully completed
                        System.out.println("============");
                        subscription.follow(2, "asdfasdf", true);
                    }
                }).onRejected(new Subscription.RejectedCallback() {
            @Override
            public void call() {
                System.out.println("noooooo");
            }
        }).onReceived(new Subscription.ReceivedCallback() {
            @Override
            public void call(JsonElement data) {
                // Called when the subscription receives data from the server
                System.out.println(data);
            }
        }).onDisconnected(new Subscription.DisconnectedCallback() {
            @Override
            public void call() {
                // Called when the subscription has been closed
                System.out.println("asdfasdfasdf");
            }
        }).onFailed(new Subscription.FailedCallback() {
            @Override
            public void call(ActionCableException e) {
                // Called when the subscription encounters any error
                System.out.println(e.getMessage());
            }
        });

        // subscription.perform("away");

        // 3. Establish connection
        consumer.connect();

        // subscription.perform("away");

        // subscription.follow(2, "asdfasdf", true);

        // subscription.unfollow();

        final JsonObject data = new JsonObject();
        data.addProperty("message_id", 2);
        // subscription.perform("away", data);

        final JsonObject expected = new JsonObject();
        expected.addProperty("message_id", 2);
        expected.addProperty("message-id", 2);
        expected.addProperty("action", "follow");
        // expected.addProperty("identifier", subscription.getIdentifier());
        // expected.addProperty("data", data.toString());
        // subscription.perform("follow", expected);
        // send message
        // 4. Perform any action
        // subscription.perform("follow");

        // 5. Perform any action using JsonObject(GSON)
        // JsonObject params = new JsonObject();
        // params.addProperty("message_id", "1");
        // params.addProperty("comment", "asdfafsdfasdfasdf");
        // subscription.perform("follow", params);
    }
}
