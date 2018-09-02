package au.com.pixelforcesystems.yeahoh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Subscription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.pixelforcesystems.yeahoh.model.Geoname;
import au.com.pixelforcesystems.yeahoh.rxjava.Api;
import au.com.pixelforcesystems.yeahoh.rxjava.ApiHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Button ok;
    TextView textView;

    String chatText;

    /**
     * We will query geonames with this service
     */
    @NonNull
    private Api api;

    /**
     * Collects all subscriptions to unsubscribe later
     */
    @NonNull
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ok = findViewById(R.id.button);
        textView = findViewById(R.id.textView2);
        textView.setText(chatText);

        createConnection();

        // api
        api = ApiHelper.getInstance().getApi();
        requestGeonames();
    }

    @Override
    protected void onDestroy() {
        // DO NOT CALL .dispose()
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    private void requestGeonames() {
        mCompositeDisposable.add(api.queryGeonames(44.1, -9.9, -22.4, 55.2, "de")
                .subscribeOn(Schedulers.io()) // "work" on io thread
                .observeOn(AndroidSchedulers.mainThread()) // "listen" on UIThread
                .map(cityResponse -> {
                    // we want to have the geonames and not the wrapper object
                    return cityResponse.geonames;
                })
                .subscribe(geonames -> displayGeonames(geonames))
        );
    }

    private void displayGeonames(@NonNull final List<Geoname> geonames) {
        // Cheap way to display a list of Strings - I was too lazy to implement a RecyclerView
        final StringBuilder output = new StringBuilder();
        for (final Geoname geoname : geonames) {
            output.append(geoname.getName()).append("\n");
        }

        textView.setText(output.toString());
    }

    private void createConnection() {
        URI uri = null;
        try {
            uri = new URI("ws://10.88.1.163:28080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        // Consumer consumer = ActionCable.createConsumer(uri);
        com.hosopy.actioncable.Consumer.Options options = new com.hosopy.actioncable.Consumer.Options();

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

        com.hosopy.actioncable.Consumer consumer = ActionCable.createConsumer(uri, options);

        // 2. Create subscription
        Channel appearanceChannel = new Channel("CommentsChannel");
        // Channel appearanceChannel = new Channel("Channel");
        appearanceChannel.addParam("data", "");
        // appearanceChannel.addParam("data-channel", "comments");
        // appearanceChannel.addParam("data-message-id", "1");
        final ChatSubscription subscription = consumer.getSubscriptions().create(appearanceChannel, ChatSubscription.class);

        subscription
                .onConnected(() -> {
                    // Called when the subscription has been successfully completed
                    System.out.println("============");
                    subscription.follow(2, "asdfasdf", true);
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
                chatText += data.toString();
                final String text = textView.getText() + "\n" + data;
                synchronized (this) {
                    // wait(5000);
                    runOnUiThread(() -> textView.setText(text));
                }
            }
        }).onDisconnected(() -> {
            // Called when the subscription has been closed
            System.out.println("asdfasdfasdf");
        }).onFailed(e -> {
            // Called when the subscription encounters any error
            System.out.println(e.getMessage());
        });

        // subscription.perform("away");

        // 3. Establish connection
        consumer.connect();
    }
}
