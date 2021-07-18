package com.ajmalyousufza.alltojpg;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private AdView adView;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;
    private LinearLayout adsBanners;
    private Button adsInterestitials;
    private Button adsReward;
	private Button pick_image,view_converted_images,btn_cinvrt;
	private ImageView image_picked;
	Context context;
	Bitmap bmp;
	int SELECT_PICTURE = 200; 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		context = getApplicationContext();
	Toolbar toolbar= (Toolbar)findViewById(R.id.toolbar);
	setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			
            @Override public void onClick(View pr) {
                onBackPressed();
            }
			
        });
        adsBanners = (LinearLayout)findViewById(R.id.adsBanners);
        adsInterestitials = (Button)findViewById(R.id.adsInterestitials);
        adsReward = (Button)findViewById(R.id.adsReward);
		pick_image = (Button) findViewById(R.id.pickimage);
		view_converted_images =(Button) findViewById(R.id.convertedimages);
		image_picked = (ImageView) findViewById(R.id.pickedimage);
		btn_cinvrt = (Button) findViewById(R.id.buttonconvert);
		

        MobileAds.initialize(this, "ca-app-pub-1341458745033418~7094080014"); 

        setBanners();
        setInterestitials();
        setReward();
		
		btn_cinvrt.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1) 
				{
					try{
					BitmapDrawable bitmapdrawable = (BitmapDrawable) image_picked.getDrawable();
					Bitmap bitmap = bitmapdrawable.getBitmap();
					saveImageToGallery(bitmap);
						if (mInterstitialAd.isLoaded()) { 
							mInterstitialAd.show();
						}
					}
					catch(Exception e){
						e.printStackTrace();
						toast("Select Image First...");
					}
				}

				
			});
		
		pick_image.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1) {
					imageChooser(Intent.ACTION_GET_CONTENT);
				}
				
			
		});
		
		view_converted_images.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1) {
					
					
				}


			});

        adsInterestitials.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if (mInterstitialAd.isLoaded()) { 
                        mInterstitialAd.show();
                    }
                }
            });
        adsReward.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    if (mRewardedVideoAd.isLoaded()) {  
                        mRewardedVideoAd.show();  
                    } else {
                        loadRewardedVideoAd();
                    }
                }
            });
    }

    //Banners
    public void setBanners(){
        adView = new AdView(this); 
        adView.setLayoutParams(new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT)); 
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1341458745033418/9400393740");
        AdRequest adRequest = new AdRequest.Builder().build(); 
        adView.loadAd(adRequest); 
        adView.setAdListener(new AdListener() {
                @Override public void onAdLoaded() {}
                @Override public void onAdFailedToLoad(int errorCode) {}
                @Override public void onAdOpened() {}
                @Override public void onAdLeftApplication() {}
                @Override public void onAdClosed() {} 
            });
        adsBanners.addView(adView);
    }

    //interestitial
    public void setInterestitials(){
        mInterstitialAd = new InterstitialAd(this);  
        mInterstitialAd.setAdUnitId("ca-app-pub-1341458745033418/5624800265"); 
        mInterstitialAd.loadAd(new AdRequest.Builder().build()); 
        mInterstitialAd.setAdListener(new AdListener() {
                @Override public void onAdLoaded() {}
                @Override public void onAdFailedToLoad(int errorCode) {}
                @Override public void onAdOpened() {}
                @Override public void onAdLeftApplication() {}
                @Override public void onAdClosed() {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }  
            });
    }

    //Video
    public void setReward(){
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this); 
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener(){
                @Override public void onRewarded(RewardItem reward) {}
                @Override public void onRewardedVideoAdLeftApplication() {}
                @Override public void onRewardedVideoAdClosed() {
                    loadRewardedVideoAd();
                } 
                @Override public void onRewardedVideoAdFailedToLoad(int errorCode) {} 
                @Override public void onRewardedVideoAdLoaded(){}
                @Override public void onRewardedVideoAdOpened() {} 
                @Override public void onRewardedVideoStarted() {} 
                public void onRewardedVideoCompleted(){}
            });
        loadRewardedVideoAd();
    }
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-1341458745033418/7109751258", new AdRequest.Builder().build());
    }
	
	void imageChooser(String Con) { 



        // create an instance of the  

		// intent of the type image 

        Intent i = new Intent(); 

        i.setType("image/*"); 
		
        i.setAction(Con); 
		



        // pass the constant to compare it  

		// with the returned requestCode 

        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE); 

    } 
	
	/*public void openFolder(){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse(Environment.getExternalStorageDirectory() +"/"+ Environment.DIRECTORY_PICTURES+"/Converted jpg images");
		intent.setDataAndType(uri, "image/*");
		startActivity(intent);
	}*/



    // this function is triggered when user 

	// selects the image from the imageChooser 

    public void onActivityResult(int requestCode, int resultCode, Intent data) { 

        super.onActivityResult(requestCode, resultCode, data); 



        if (resultCode == RESULT_OK) { 



            // compare the resultCode with the 

			// SELECT_PICTURE constant 

            if (requestCode == SELECT_PICTURE) { 

                // Get the url of the image from data 

                Uri selectedImageUri = data.getData(); 
				try {
					bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
				} catch (IOException e) {}

				//toast(selectedImageUri.toString());

                if (null != selectedImageUri) { 

                    // update the preview image in the layout 

                    image_picked.setImageURI(selectedImageUri); 
			

                } 
				}
				}
				}

            

	void toast(String txt){
		
		Toast.makeText(this,txt,Toast.LENGTH_LONG).show();
	}
	
	OutputStream fos;
	private void saveImageToGallery(Bitmap bitmap) {
		
		
		
		try{
			
			if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
				
				ContentResolver resolver = getContentResolver();
				ContentValues contentvalues = new ContentValues();
				contentvalues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_"+System.currentTimeMillis()/1000012345+".jpg");
				contentvalues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg");
				contentvalues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+"Converted jpg images");
				Uri image_uru = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentvalues);
				fos =resolver.openOutputStream(Objects.requireNonNull(image_uru));
				bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
				Objects.requireNonNull(fos);
				Uri selectedUri = Uri.parse("Phone Storage /"+Environment.DIRECTORY_PICTURES+"/Converted jpg images");

				String s = selectedUri.toString();
				view_converted_images.setText("Completed ✅\nSaved Location : "+s);
				toast("✅ Completed ✅\n\nImage Saved to : \n\n"+Environment.DIRECTORY_PICTURES+File.separator+"Converted jpg images : \n\n"+"As jpg");
			}
			else{
				File filepath = Environment.getExternalStorageDirectory();
				File dir = new File(filepath.getAbsolutePath()+"/Converted jpg images/");
				dir.mkdir();
				File file = new File(dir,System.currentTimeMillis()+".jpg");
				try{
					fos = new FileOutputStream(file);
				}
				catch(FileNotFoundException e){
					e.printStackTrace();
					
				}
				bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
				toast("Image Saved to \n\n"+file.toString()+"\n\n As jpg");
				Uri selectedUri = Uri.parse("Phone Storage /"+Environment.DIRECTORY_PICTURES+"/Converted jpg images");

				String s = selectedUri.toString();
				view_converted_images.setText("Completed ✅\nSaved Location : "+s);
				try{
					fos.flush();
					fos.close();

				}
				catch(Exception e){
					e.printStackTrace();

				}
			}
			
		}
		catch(Exception e){
			
			toast("Image not saved \n"+e.getMessage());
			
		}
	}
	
	boolean doubleBackToExitPressedOnce = false;

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			
			
				//Handler.removeCallbacks(handler);
				
			if (mRewardedVideoAd.isLoaded()) {  
				mRewardedVideoAd.show();  
			} else {
				loadRewardedVideoAd();
			}
				
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

				@Override
				public void run() {
					doubleBackToExitPressedOnce=false;                       
				}
			}, 2000);
	} 

    @Override
    protected void onResume() {
        super.onResume();
        mRewardedVideoAd.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRewardedVideoAd.pause();
    }
	
	

    @Override
    protected void onDestroy() {
        //super.onDestroy();
		
		super.onDestroy();
		
		//mRewardedVideoAd.destroy();
    }
	
    
}
/*don't forget to subscribe my YouTube channel for more Tutorial and mod*/
/*
https://youtube.com/channel/UC_lCMHEhEOFYgJL6fg1ZzQA */
