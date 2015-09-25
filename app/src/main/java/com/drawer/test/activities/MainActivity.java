package com.drawer.test.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.drawer.test.R;
import com.drawer.test.dialogs.ColorPicker;
import com.drawer.test.views.DrawingView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private final int SELECT_PIC = 1;
    private final int CAPTURE_PIC = 0;

    @Bind(R.id.drawing)
    DrawingView drawView;
    @Bind(R.id.backgroundColor)
    Button mBackgroundColor;
    @Bind(R.id.paintColor)
    Button mPaintColor;

    float smallBrush;
    float mediumBrush;
    float largeBrush;

    ColorPicker mBackgroundColorPicker;
    ColorPicker mPaintColorPicker;

    Intent mActivityResultData;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);


        drawView.setBrushSize(mediumBrush);
        if (savedInstanceState == null) {
            drawUrlImage(processImage(getIntent()));
            mActivityResultData = null;
        }

        mBackgroundColorPicker =  new ColorPicker(this, drawView.getBackgroundColor());
        mBackgroundColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mBackgroundColor.setBackgroundColor(color);
                drawView.setBackgroundPrint(true);
                drawView.setBackgroundColor(color);
            }
        });
        mBackgroundColor.setBackgroundColor(drawView.getBackgroundColor());

        mPaintColorPicker = new ColorPicker(this, drawView.getPaintColor());
        mPaintColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                mPaintColor.setBackgroundColor(color);
                drawView.setColor(color);
            }
        });
        mPaintColor.setBackgroundColor(drawView.getPaintColor());
    }

    @OnClick({ R.id.brushSize, R.id.savePic, R.id.backgroundColor, R.id.paintColor })
    public void buttonEvent(View v) {
        switch (v.getId()) {
            case R.id.brushSize:
                selectBrushSize();
                break;
            case R.id.savePic:
                savePic();
                break;
            case R.id.backgroundColor:
                mBackgroundColorPicker.show();
                break;
            case R.id.paintColor:
                mPaintColorPicker.show();
                break;
        }
    }

    public void onResume() {
        super.onResume();

        if (mActivityResultData != null) {
            drawUrlImage(processImage(mActivityResultData));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ask4SettingWallpaper();
                }
            }, 1000);
        }
    }

    protected void ask4SettingWallpaper() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // Title
        alertDialogBuilder.setTitle(getString(R.string.drawPicture));

        // Message
        alertDialogBuilder
                .setMessage(getString(R.string.setAsWallpaper))
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        setWallpaper();
                        Toast.makeText(MainActivity.this, getString(R.string.wallpaperSet),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // Open
        alertDialog.show();
    }

    protected void setWallpaper() {
        InputStream inputStream = processImage(mActivityResultData);
        WallpaperManager myWallpaperManager
                = WallpaperManager.getInstance(getApplicationContext());
        try {
            myWallpaperManager.setStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream processImage(Intent data) {
        InputStream imageStream = null;
        try {
            imageUri = data.getParcelableExtra("uri");
            imageStream = getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException fex) {
            fex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imageStream;
    }

    private void drawUrlImage(InputStream imageStream) {
        if (imageStream != null) {
            drawView.setBackgroundPrint(false);
            drawView.animate();
            drawView.setBackground(Drawable.createFromStream(imageStream, "myImg"));
        }
    }

    private void savePic() {
        drawView.setDrawingCacheEnabled(true);
        try {
            Bitmap bitmap = drawView.getDrawingCache();
            String imgSaved = MediaStore.Images.Media.insertImage(this.getBaseContext().getContentResolver(),
                    bitmap, "MyPic" + System.currentTimeMillis() + ".png", "drawing");
            Toast toast;
            if (imgSaved != null) {
                toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.imageSaved), Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.imageSaveError), Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    private void selectBrushSize() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle(getResources().getString(R.string.brushSizeTitle));
        brushDialog.setContentView(R.layout.brush_chooser);

        ImageButton smallButton = (ImageButton) brushDialog.findViewById(R.id.small);
        smallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBrushSize(smallBrush);
                drawView.setLastBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });

        ImageButton mediumButton = (ImageButton) brushDialog.findViewById(R.id.medium);
        mediumButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBrushSize(mediumBrush);
                drawView.setLastBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });

        ImageButton largeButton = (ImageButton) brushDialog.findViewById(R.id.large);
        largeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setBrushSize(largeBrush);
                drawView.setLastBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });

        brushDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myGallery:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PIC);
                return true;
            case R.id.action_myPhotos:
                Intent photosIntent = new Intent(this, GalleryActivity.class);
                startActivityForResult(photosIntent, SELECT_PIC);
                return true;
            case R.id.action_myCamera:
                Intent photoClickerIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoClickerIntent, CAPTURE_PIC);
                return true;
            case R.id.action_exit:{
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.new_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        switch (requestCode) {
            case SELECT_PIC: {
                if (resultCode == RESULT_OK) {
                    try {
                        if (imageReturnedIntent.hasExtra("uri")) {
                            mActivityResultData = imageReturnedIntent;
                        } else {
                            Intent data = new Intent();
                            data.putExtra("uri", imageReturnedIntent.getData());
                            mActivityResultData = data;
                        }
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                } else {
                    mActivityResultData = null;
                }
            }
                break;

            case CAPTURE_PIC: {
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap image1 = (Bitmap) imageReturnedIntent.getExtras().get("data");
                        String imgSaved = MediaStore.Images.Media.insertImage(this.getBaseContext()
                                .getContentResolver(), image1, "Pic" + System.currentTimeMillis()
                                + ".png", "drawing");
                        Intent data = new Intent();
                        data.putExtra("uri", Uri.parse(imgSaved));
                        mActivityResultData = data;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    mActivityResultData = null;
                }
            }
        }
    }
}
