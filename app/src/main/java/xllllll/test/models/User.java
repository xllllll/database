package xllllll.test.models;

import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String fid;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email,String fid) {
        this.username = username;
        this.email = email;
        this.fid=fid;
    }

}
// [END blog_user_class]
