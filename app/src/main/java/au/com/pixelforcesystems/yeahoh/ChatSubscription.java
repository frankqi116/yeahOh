package au.com.pixelforcesystems.yeahoh;

import com.hosopy.actioncable.Subscription;
import com.hosopy.actioncable.annotation.Data;
import com.hosopy.actioncable.annotation.Perform;

public interface ChatSubscription extends Subscription {
    /*
     * Equivalent:
     *   perform("join")
     */
    @Perform("unfollow")
    void unfollow();

    /*
     * Equivalent:
     *   perform("send_message", JsonObjectFactory.fromJson("{body: \"...\", private: true}"))
     */
    @Perform("follow")
    void follow(@Data("message_id") int message_id, @Data("comment") String comment, @Data("private") boolean isPrivate);
}
