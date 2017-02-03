package xllllll.test.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

import xllllll.test.PostDetailActivity;
import xllllll.test.R;
import xllllll.test.models.Comment;
import xllllll.test.models.Post;
import xllllll.test.models.User;
import xllllll.test.viewholder.PostViewHolder;

public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    public Post post;

    private String mPostKey;
    private EditText mCommentField,input;
    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
public ProgressBar vProgres;
    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);
        mCommentField = (EditText) rootView.findViewById(R.id.field_comment_text);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
vProgres = (ProgressBar) rootView.findViewById(R.id.vProgress);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);
        //mRecycler.setItemViewCacheSize(20);
        //mRecycler.setDrawingCacheEnabled(true);
       // mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        return rootView;
    }
    private void setupProgress() {
        vProgres.setVisibility(View.VISIBLE);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                vProgres.setVisibility(View.GONE);
                mAdapter.unregisterAdapterDataObserver(this);
            }
        });

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                PostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                Log.d("postkey",String.valueOf(position));

               // final String postKeytoken = postRef.child("posts");

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });


                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        // Launch PostDetailActivity
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Bu fali sil")
                                .setPositiveButton("iptal", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // FIRE ZE MISSILES!
                                        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).equals(postKey);
                                        FirebaseDatabase.getInstance().getReference().child("user-posts").child(getUid()).child(postKey)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                              //  System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                                               try{

                                                   if(snapshot.getValue().toString()!=null){
                                                      // FirebaseDatabase.getInstance().
                                                          //     getReference().child("user-posts").child(getUid()).child(postKey)
                                                        //       .removeValue();
                                                       Log.d("possss",snapshot.getValue().toString());}
                                               }catch (NullPointerException e){}



                                            }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                        });
                                            ;




                                            AlertDialog.Builder dialogAlert = new AlertDialog.Builder(getContext());
                                        dialogAlert.setMessage("talebiniz alinmistir, tesekkurler");
                                       // dialogAlert.show();

                                    }
                                })
                                .setNegativeButton("sil", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog\\


                                        FirebaseDatabase.getInstance().getReference().child("users").child(getUid()).equals(postKey);
                                        FirebaseDatabase.getInstance().getReference().child("posts").child(postKey)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                         System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                                                        try{

                                                            if(snapshot.getValue().toString()!=null){
                                                            Post pozt=    snapshot.getValue(Post.class);
                                                                Log.d("possss",pozt.uid+postKey);

                                                                if(pozt.uid.toString().equals(getUid().toString())){
                                                                    FirebaseDatabase.getInstance().
                                                                            getReference().child("posts").child(postKey)
                                                                            .removeValue();
                                                                    FirebaseDatabase.getInstance().
                                                                            getReference().child("user-posts").child(getUid()).child(postKey)
                                                                            .removeValue();
                                                                    Log.d("possss",snapshot.getValue().toString());
                                                                    AlertDialog.Builder dialogAlert = new AlertDialog.Builder(getContext());
                                                                    dialogAlert.setMessage("talebiniz alinmistir, tesekkurler");
                                                                    dialogAlert.show();
                                                                }else{

                                                                    AlertDialog.Builder dialogAlert = new AlertDialog.Builder(getContext());
                                                                    dialogAlert.setMessage("sizin olmayan bir fali silemessiniz").setPositiveButton(
                                                                            "bu fali bildir", new DialogInterface.OnClickListener() {
                                                                                public void onClick(DialogInterface dialog, int id) {
                                                                                    // User cancelled the dialog\\
                                                                                    AlertDialog.Builder dialogAlert = new AlertDialog.Builder(getContext());
                                                                                    dialogAlert.setMessage("talebiniz alinmistir, tesekkurler");
                                                                                    dialogAlert.show();
                                                                                }}

                                                                    );
                                                                    dialogAlert.show();

                                                                }
                                                                }
                                                        }catch (NullPointerException e){}



                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                        ;






/****


                                         input = new EditText(getContext());
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT);
                                        input.setLayoutParams(lp);

                                        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(getContext());
                                        dialogAlert.setTitle("yorumunuz:");
                                        dialogAlert.setView(input);
                                        dialogAlert    .setPositiveButton("GONDER", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // FIRE ZE MISSILES!
                                                // Get post key from intent
                                                mPostKey = postKey;
                                                if (mPostKey == null) {
                                                    throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
                                                }

                                                // Initialize Database
                                                mPostReference = FirebaseDatabase.getInstance().getReference()
                                                        .child("posts").child(mPostKey);
                                                mCommentsReference = FirebaseDatabase.getInstance().getReference()
                                                        .child("post-comments").child(mPostKey);
                                                postComment();
                                            }
                                        });


                                        dialogAlert.show();

 *////
                                    }
                                });
                        // Create the AlertDialog object and return it
                        builder.create();
                        builder.show();
                        return false;

                    }
                });


                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
        setupProgress();

    }


    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new comment object
                        String commentText = input.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);


                        String url = "http://barronpw.alwaysdata.net/kahvefali.php";
                        HashMap postData = new HashMap();

                        postData.put("token", post.token);
                        postData.put("mesaj", authorName +": "+commentText);
                        PostResponseAsyncTask readTask = new PostResponseAsyncTask(getContext(), postData, false, new AsyncResponse() {
                            @Override
                            public void processFinish(String s) {
                                // Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        });
                        if(post.token!=null && commentText.length()>2){

                            readTask.execute(url);


//post.token;
                            // Push the comment, it will appear in the list
                            mCommentsReference.push().setValue(comment);

                            // Clear the field
                            mCommentField.setText(null);}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
