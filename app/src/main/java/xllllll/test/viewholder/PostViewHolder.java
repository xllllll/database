package xllllll.test.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import xllllll.test.R;
import xllllll.test.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public ImageView imajs;
    Context context;
    StorageReference mStorageRef;  //mStorageRef was previously used to transfer data.

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        imajs = (ImageView) itemView.findViewById(R.id.imajx);
        context =itemView.getContext();
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);

String downloadlink= post.gsimaj;
        String base64json=post.imaj;

        if (downloadlink==null){
            byte[] decodedString = Base64.decode(post.imaj, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imajs.setImageBitmap(decodedByte);
        } else{
        FirebaseStorage storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl("gs://test-c0965.appspot.com");

        mStorageRef.child(downloadlink).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use the bytes to display the image
                //Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //imajs.setImageBitmap(bmp);
               // Picasso.with().load(bmp).into(imajs);
                Picasso.with(context).load(uri.toString()).centerCrop().fit().into(imajs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        }


       // imaj.setText(post.imaj);

        starView.setOnClickListener(starClickListener);
    }
}
