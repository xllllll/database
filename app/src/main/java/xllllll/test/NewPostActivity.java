package xllllll.test;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import xllllll.test.models.Post;
import xllllll.test.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    StorageReference mStorageRef;  //mStorageRef was previously used to transfer data.

    private EditText mTitleField;
    private EditText mBodyField;
    private ImageView imgview;
    private FloatingActionButton mSubmitButton;
    Bitmap bitmap;
    public String encodedImage,price,gsimaj;
public CheckBox cb,cb2;
    private IInAppBillingService mService;
    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("falfiyat","koptu");
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            Log.d("falfiyat","baglandi");
           // cb.setText("KahveFalı VIP Fal");
            //new GetItemList().execute();
/////////////////


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_new_post);


        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        mTitleField = (EditText) findViewById(R.id.field_title);
        mBodyField = (EditText) findViewById(R.id.field_body);
        imgview = (ImageView) findViewById(R.id.imageView);
        cb = (CheckBox) findViewById(R.id.cb);
        cb2 = (CheckBox) findViewById(R.id.cb2);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2076428269239567~1120679339");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("C97AE6AB035695FBC5B1E607507776BF").build();
        mAdView.loadAd(adRequest);

        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent pickPhoto = new Intent(Intent.ACTION_PICK,  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
                Crop.pickImage(NewPostActivity.this);
            }
        });


        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).withAspect(4,3).start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            imgview.setImageURI(Crop.getOutput(result));
            Uri selectedImage = Crop.getOutput(result);
            imgview.setImageURI(selectedImage);


            final Uri imageUri = Crop.getOutput(result);


////////////////
            if (selectedImage != null) {
                //displaying a progress dialog while upload is going on
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("yukleniyor");
                progressDialog.show();
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                mStorageRef = storage.getReferenceFromUrl("gs://test-c0965.appspot.com");

                gsimaj="images/"+getUid()+"-"+ts+".jpg";
                StorageReference riversRef = mStorageRef.child(gsimaj);
                riversRef.putFile(selectedImage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //if the upload is successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying a success toast
                                Toast.makeText(getApplicationContext(), "tamamlandi ", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //if the upload is not successfull
                                //hiding the progress dialog
                                progressDialog.dismiss();

                                //and displaying error message
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //calculating progress percentage
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                //displaying percentage in progress dialog
                                progressDialog.setMessage("tamamlandi " + ((int) progress) + "%...");
                            }
                        });
            }
            //if there is not any file
            else {
                //you can display an error toast
            }


////////////////

            try{
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                //inJustDecodeBounds = true <-- will not load the bitmap into memory
                bmOptions.inJustDecodeBounds = true;

                //BitmapFactory.decodeFile(imagePath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/4, photoH/3);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = 3;
                bmOptions.inPurgeable = true;

                final Bitmap selectedImagex = BitmapFactory.decodeStream(imageStream,null,bmOptions);
                // final Bitmap selectedImagex = BitmapFactory.decodeStream(imageStream);
                final int maxSize = 960;
                int outWidth;
                int outHeight;
                int inWidth = selectedImagex.getWidth();
                int inHeight = selectedImagex.getHeight();
                if(inWidth > inHeight){
                    outWidth = maxSize;
                    outHeight = (inHeight * maxSize) / inWidth;
                } else {
                    outHeight = maxSize;
                    outWidth = (inWidth * maxSize) / inHeight;
                }
                Bitmap scaled = Bitmap.createScaledBitmap(selectedImagex,outWidth, outHeight, false);


                encodedImage = encodeImage(scaled);
                encodedImage="-";
                Log.d("resim", encodedImage);
            }catch(IOException e){}

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(imageReturnedIntent.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, imageReturnedIntent);
        }
/***
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imgview.setImageURI(selectedImage);


                    final Uri imageUri = imageReturnedIntent.getData();


////////////////
                    if (selectedImage != null) {
                        //displaying a progress dialog while upload is going on
                        final ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle("yukleniyor");
                        progressDialog.show();
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        mStorageRef = storage.getReferenceFromUrl("gs://test-c0965.appspot.com");

                        gsimaj="images/"+getUid()+"-"+ts+".jpg";
                        StorageReference riversRef = mStorageRef.child(gsimaj);
                        riversRef.putFile(selectedImage)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //if the upload is successfull
                                        //hiding the progress dialog
                                        progressDialog.dismiss();

                                        //and displaying a success toast
                                        Toast.makeText(getApplicationContext(), "tamamlandi ", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        //if the upload is not successfull
                                        //hiding the progress dialog
                                        progressDialog.dismiss();

                                        //and displaying error message
                                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        //calculating progress percentage
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                        //displaying percentage in progress dialog
                                        progressDialog.setMessage("tamamlandi " + ((int) progress) + "%...");
                                    }
                                });
                    }
                    //if there is not any file
                    else {
                        //you can display an error toast
                    }


////////////////

                    try{
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        //inJustDecodeBounds = true <-- will not load the bitmap into memory
                        bmOptions.inJustDecodeBounds = true;

                        //BitmapFactory.decodeFile(imagePath, bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW/4, photoH/3);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = 3;
                        bmOptions.inPurgeable = true;

                        final Bitmap selectedImagex = BitmapFactory.decodeStream(imageStream,null,bmOptions);
                       // final Bitmap selectedImagex = BitmapFactory.decodeStream(imageStream);
                        final int maxSize = 960;
                        int outWidth;
                        int outHeight;
                        int inWidth = selectedImagex.getWidth();
                        int inHeight = selectedImagex.getHeight();
                        if(inWidth > inHeight){
                            outWidth = maxSize;
                            outHeight = (inHeight * maxSize) / inWidth;
                        } else {
                            outHeight = maxSize;
                            outWidth = (inWidth * maxSize) / inHeight;
                        }
                        Bitmap scaled = Bitmap.createScaledBitmap(selectedImagex,outWidth, outHeight, false);


                        encodedImage = encodeImage(scaled);
                        encodedImage="-";
                        Log.d("resim", encodedImage);
                    }catch(IOException e){}
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();



                    imgview.setImageURI(selectedImage);


                    final Uri imageUri = imageReturnedIntent.getData();




 ////////////////
                    if (selectedImage != null) {
                        //displaying a progress dialog while upload is going on
                        final ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle("yukleniyor");
                        progressDialog.show();
                        Long tsLong = System.currentTimeMillis()/1000;
                        String ts = tsLong.toString();

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                         mStorageRef = storage.getReferenceFromUrl("gs://test-c0965.appspot.com");

                        gsimaj="images/"+getUid()+"-"+ts+".jpg";
                        StorageReference riversRef = mStorageRef.child(gsimaj);
                        riversRef.putFile(selectedImage)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //if the upload is successfull
                                        //hiding the progress dialog
                                        progressDialog.dismiss();

                                        //and displaying a success toast
                                        Toast.makeText(getApplicationContext(), "tamamlandi ", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        //if the upload is not successfull
                                        //hiding the progress dialog
                                        progressDialog.dismiss();

                                        //and displaying error message
                                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        //calculating progress percentage
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                                        //displaying percentage in progress dialog
                                        progressDialog.setMessage("tamamlandi " + ((int) progress) + "%...");
                                    }
                                });
                    }
                    //if there is not any file
                    else {
                        //you can display an error toast
                    }


////////////////









                    try{
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        // Get the dimensions of the bitmap
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        //inJustDecodeBounds = true <-- will not load the bitmap into memory
                        bmOptions.inJustDecodeBounds = true;

                        //BitmapFactory.decodeFile(imagePath, bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW/4, photoH/3);

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = 3;
                        bmOptions.inPurgeable = true;

                        final Bitmap selectedImagex = BitmapFactory.decodeStream(imageStream,null,bmOptions);
                        final int maxSize = 960;
                        int outWidth;
                        int outHeight;
                        int inWidth = selectedImagex.getWidth();
                        int inHeight = selectedImagex.getHeight();
                        if(inWidth > inHeight){
                            outWidth = maxSize;
                            outHeight = (inHeight * maxSize) / inWidth;
                        } else {
                            outHeight = maxSize;
                            outWidth = (inWidth * maxSize) / inHeight;
                        }
                        Bitmap scaled = Bitmap.createScaledBitmap(selectedImagex,outWidth, outHeight, false);


                        encodedImage = encodeImage(scaled);
                        encodedImage="-";
                        Log.d("resim", encodedImage);
                    }catch(IOException e){}


                }
                break;


        }
 *//////
    }

    private String encodeImage(Bitmap bm)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }


    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "gonderiliyor...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "hata olustu.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            if (encodedImage==null){
                                Toast.makeText(NewPostActivity.this,
                                        "lutfen resim seciniz...",
                                        Toast.LENGTH_SHORT).show();

                            }else{
if (cb.isChecked()){

    new GetItemList().execute();
if(cb2.isChecked()){
    writeNewPost(userId, "anonim", title, body,encodedImage,gsimaj);


}else{
    writeNewPost(userId, user.username, title, body,encodedImage,gsimaj);

}

}else{

    if(cb2.isChecked()){
        writeNewPost(userId, "anonim", title, body,encodedImage,gsimaj);


    }else{
        writeNewPost(userId, user.username, title, body,encodedImage,gsimaj);

    }

}

                            }
                        }
                        if (encodedImage==null){
                            Toast.makeText(NewPostActivity.this,
                                    "lutfen resim seciniz...",
                                    Toast.LENGTH_SHORT).show();
                            setEditingEnabled(true);

                        }else{

                            // Finish this Activity, back to the stream
                            setEditingEnabled(true);
                            finish();
                            // [END_EXCLUDE]
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body,String imaj,String gsimaj) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously




        String key = mDatabase.child("posts").push().getKey();
        String token = FirebaseInstanceId.getInstance().getToken();

        Post post = new Post(userId, username, title, body,imaj,token,gsimaj);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]







    class GetItemList extends AsyncTask<Integer, Integer, Long> {

        @Override
        protected Long doInBackground(Integer... params) {
            ArrayList<String> skuList = new ArrayList<String>();
            skuList.add("falyorum");
            //skuList.add("i002");
            Bundle querySkus = new Bundle();
            querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
            Bundle skuDetails = null;
            try {
                skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList
                            = skuDetails.getStringArrayList("DETAILS_LIST");
                    for (String thisResponse : responseList) {
                        JSONObject object;
                        object = new JSONObject(thisResponse);
                        String sku = object.getString("productId");
                         price = object.getString("price");
                        String mFirstIntermediate;
                        String mSecondIntermediate;
                        if (sku.equals("falyorum"))

                        {mFirstIntermediate = price;
                        // else if (sku.equals("i002")) mSecondIntermediate = price;
                        //pView.setText(sku + ": " + price);
                        Log.d("falfiyat", price);



                           String userId = getUid();
                           // Log.d("falfiyat", userId);
                          //  if (response != 0) {}

                           // response = mService.consumePurchase(3, getPackageName(), object.getString("purchaseToken"));

                            //response = mService.consumePurchase(3, getPackageName(), "inapp:xllllll.test:falyorum");
                           // if (response != 0) {}



                            Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
                            int responsex = ownedItems.getInt("RESPONSE_CODE");
                            if (responsex == 0)
                            {
                                ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                                ArrayList<String> purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                                //ArrayList<String> signatureList = ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
                                //String continuationToken = ownedItems.getString("INAPP_CONTINUATION_TOKEN");
                                for (int i = 0; i < purchaseDataList.size(); ++i) {
                                    try {
                                        String purchaseData = purchaseDataList.get(i);
                                        JSONObject jo = new JSONObject(purchaseData);
                                        final String token = jo.getString("purchaseToken");
                                        String skux = null;
                                        if (ownedSkus != null)
                                            skux = ownedSkus.get(i);
                                        mService.consumePurchase(3,getPackageName(), token);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), sku, "inapp", userId);


                            response = buyIntentBundle.getInt("RESPONSE_CODE");

                          // if (response != 0) continue;
Log.d("respons",String.valueOf(response));
                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                            startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);


                        }
                    }
                }
            } catch (NullPointerException ne) {
                Log.d("Synch Billing", "Error Null Pointer: " + ne.getMessage());
                ne.printStackTrace();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                Log.d("Synch Billing", "Error Remote: " + e.getMessage());
                e.printStackTrace();
            } catch (JSONException
                    | IntentSender.SendIntentException
                    je) {
                // TODO Auto-generated catch block
                Log.d("Synch Billing", "Error JSON: " + je.getMessage());
                je.printStackTrace();
            }
            return null;
        }
    }
}
