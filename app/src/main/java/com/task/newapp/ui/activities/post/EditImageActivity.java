package com.task.newapp.ui.activities.post;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.task.newapp.BuildConfig;
import com.task.newapp.R;
import com.task.newapp.utils.Constants;
import com.task.newapp.utils.photoeditor.EmojiBSFragment;
import com.task.newapp.utils.photoeditor.PropertiesBSFragment;
import com.task.newapp.utils.photoeditor.StickerBSFragment;
import com.task.newapp.utils.photoeditor.TextEditorDialogFragment;
import com.task.newapp.utils.photoeditor.base.BaseActivity;
import com.task.newapp.utils.photoeditor.edit.OnPhotoEditorListener;
import com.task.newapp.utils.photoeditor.edit.PhotoEditor;
import com.task.newapp.utils.photoeditor.edit.PhotoEditorView;
import com.task.newapp.utils.photoeditor.edit.PhotoFilter;
import com.task.newapp.utils.photoeditor.edit.SaveSettings;
import com.task.newapp.utils.photoeditor.edit.TextStyleBuilder;
import com.task.newapp.utils.photoeditor.edit.ViewType;
import com.task.newapp.utils.photoeditor.filters.FilterListener;
import com.task.newapp.utils.photoeditor.filters.FilterViewAdapter;
import com.task.newapp.utils.photoeditor.tools.EditingToolsAdapter;
import com.task.newapp.utils.photoeditor.tools.ToolType;
import com.task.newapp.utils.simplecropview.BasicActivity;

import java.io.File;
import java.io.IOException;

import static android.provider.MediaStore.Files.getContentUri;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    public static final String FILE_PROVIDER_AUTHORITY = "photoeditor.fileprovider";
    public static final String IS_IMAGE_EDITED = "is_image_edited";
    private static final String TAG = EditImageActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    private static final int REQUEST_CODE_CROP_IMAGE = 202;
    public PhotoEditor mPhotoEditor;
    @Nullable
    @VisibleForTesting
    Uri mSaveImageUri;
    String imagepath;
    ImageView imgUndo;
    ImageView imgRedo;
    Uri cropuri;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private final EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this, EditImageActivity.this);
    private FilterViewAdapter mFilterViewAdapter;
    private RelativeLayout mRootView;

    public static void hideframe() {

        for (int i = 0; i < PhotoEditor.addedViews.size(); i++) {
            try {
                PhotoEditor.addedViews.get(i).findViewById(R.id.frmBorder).setBackgroundResource(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                PhotoEditor.addedViews.get(i).findViewById(R.id.imgPhotoEditorClose).setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                PhotoEditor.addedViews.get(i).findViewById(R.id.frmBorder).setTag(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                PhotoEditor.addedViews.get(i).findViewById(R.id.imgPhotoEditoredit).setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);

        initViews();
        mRvFilters.setVisibility(View.GONE);

        imagepath = getIntent().getStringExtra("filepath");
        mFilterViewAdapter = new FilterViewAdapter(this, EditImageActivity.this, imagepath);
//        Commons.logEvent(EditImageActivity.this, "EditImageActivity", "", "EditImageActivity", "onCreate");

        File imgFile = new File(imagepath);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            mPhotoEditorView.getSource().setImageBitmap(myBitmap);
            mPhotoEditorView.getSource().setImageURI(Uri.parse(imagepath));
        }

        handleIntentImage(mPhotoEditorView.getSource());

        mWonderFont = Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);


        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        Log.println(Log.ASSERT, "addedViews---", PhotoEditor.addedViews.size() + "---" + mPhotoEditor.redoViews.size());

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);
    }

    private void handleIntentImage(ImageView source) {
        Intent intent = getIntent();
        if (intent != null) {
            String intentType = intent.getType();
            if (intentType != null && intentType.startsWith("image/")) {
                Uri imageUri = intent.getData();
                if (imageUri != null) {
                    source.setImageURI(imageUri);
                }
            }
        }
    }

    private void initViews() {


        ImageView imgSave;
        ImageView imgClose;
        ImageView imgShare;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvTools.setHasFixedSize(true);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);


        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgShare = findViewById(R.id.imgShare);
        imgShare.setOnClickListener(this);

        mPhotoEditorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideframe();
                return true;
            }
        });

    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode, Typeface typeface) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode, typeface);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode, Typeface font) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);
                styleBuilder.withTextFont(font);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");

        if (PhotoEditor.addedViews.size() == 0) {
//            imgUndo.setVisibility(View.INVISIBLE);
            imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
//            imgUndo.setVisibility(View.VISIBLE);
            imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (mPhotoEditor.redoViews.size() == 0) {
//            imgRedo.setVisibility(View.INVISIBLE);
            imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
//            imgRedo.setVisibility(View.VISIBLE);
            imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
        if (PhotoEditor.addedViews.size() == 0) {
//            imgUndo.setVisibility(View.INVISIBLE);
            imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
//            imgUndo.setVisibility(View.VISIBLE);
            imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (mPhotoEditor.redoViews.size() == 0) {
//            imgRedo.setVisibility(View.INVISIBLE);
            imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
//            imgRedo.setVisibility(View.VISIBLE);
            imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
//                Commons.logEvent(EditImageActivity.this, "Undo", "", "EditImageActivity", "UndoClick");

                if (PhotoEditor.addedViews.size() == 0) {
//            imgUndo.setVisibility(View.INVISIBLE);
                    imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
//            imgUndo.setVisibility(View.VISIBLE);
                    imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                }

                if (mPhotoEditor.redoViews.size() == 0) {
//            imgRedo.setVisibility(View.INVISIBLE);
                    imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
//            imgRedo.setVisibility(View.VISIBLE);
                    imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                }

                break;

            case R.id.imgRedo:
//                Commons.logEvent(EditImageActivity.this, "Redo", "", "EditImageActivity", "RedoClick");
                mPhotoEditor.redo();

                if (PhotoEditor.addedViews.size() == 0) {
//            imgUndo.setVisibility(View.INVISIBLE);
                    imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
//            imgUndo.setVisibility(View.VISIBLE);
                    imgUndo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                }

                if (mPhotoEditor.redoViews.size() == 0) {
//            imgRedo.setVisibility(View.INVISIBLE);
                    imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.gray_edit),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                } else {
//            imgRedo.setVisibility(View.VISIBLE);
                    imgRedo.setColorFilter(ContextCompat.getColor(this, R.color.white),
                            android.graphics.PorterDuff.Mode.SRC_IN);
                }

                break;

            case R.id.imgSave:
//                Commons.logEvent(EditImageActivity.this, "SaveImage", "", "EditImageActivity", "SaveClick");
                saveImage();
                break;

            case R.id.imgClose:
//                Commons.logEvent(EditImageActivity.this, "Cancel", "", "EditImageActivity", "CloseClick");
                onBackPressed();
                break;

            case R.id.imgShare:
//                Commons.logEvent(EditImageActivity.this, "Share", "", "EditImageActivity", "ShareClick");
                shareImage();
                break;
        }
    }

    private void shareImage() {
        if (mSaveImageUri == null) {
            showSnackbar(getString(R.string.msg_save_image_to_share));
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(mSaveImageUri));
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_image)));
    }

    private Uri buildFileProviderUri(@NonNull Uri uri) {
        return FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY,
                new File(uri.getPath()));
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            showLoading("Saving...");
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
//                        hideLoading();
//                        showSnackbar("Image Saved Successfully");
                        mSaveImageUri = Uri.fromFile(new File(imagePath));
                        mPhotoEditorView.getSource().setImageURI(mSaveImageUri);
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("SOURCE_PATH", imagepath);
                        returnIntent.putExtra("OUTPUT_PATH", imagePath);
                        returnIntent.putExtra("IS_IMAGE_EDITED", true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
                            Uri uri = getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                            ContentResolver resolver = getContentResolver();
                            resolver.insert(uri, values);
                        } else {

                            MediaScannerConnection.scanFile(EditImageActivity.this, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                        }

                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        hideLoading();
//                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
//                `FirebaseCrashlytics`.getInstance().setCustomKey("EditImageSave", e.getMessage());
//                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }

    private void saveImageforcrop() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            showLoading("Saving...");
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");
            try {
                file.createNewFile();

                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
//                        hideLoading();
//                        showSnackbar("Image Saved Successfully");
                        cropuri = Uri.parse(imagePath);
                        mPhotoEditorView.getSource().setImageURI(cropuri);
                        startActivityForResult(BasicActivity.createIntent(EditImageActivity.this, cropuri), REQUEST_CODE_CROP_IMAGE);
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
//                        hideLoading();
//                        showSnackbar("Failed to save Image");
                    }
                });
            } catch (IOException e) {
//                FirebaseCrashlytics.getInstance().setCustomKey("EditImageSave", e.getMessage());
//                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
                hideLoading();
                showSnackbar(e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
//                    Commons.logEvent(EditImageActivity.this, "CameraResult", "", "EditImageActivity", "onActivityResult");
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get(Constants.data);
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
//                    Commons.logEvent(EditImageActivity.this, "GalleryResult", "", "EditImageActivity", "onActivityResult");
                    try {
                        mPhotoEditor.clearAllViews();
//                        Uri uri = data.getData();
                        String path = data.getStringExtra("path");
                        Uri uri = FileProvider.getUriForFile(EditImageActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(path));
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
//                        FirebaseCrashlytics.getInstance().setCustomKey("PickRequest", e.getMessage());
//                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_CODE_CROP_IMAGE:
                    String image = data.getStringExtra("uri");
                    mPhotoEditorView.getSource().setImageURI(Uri.parse(image));
                    break;

            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize, boolean iserase) {
        if (iserase) {
            mPhotoEditor.setBrushEraserSize(brushSize);
            mPhotoEditor.setBrushEraserColor(Color.TRANSPARENT);
            mTxtCurrentTool.setText(R.string.label_eraser);
            mPhotoEditor.brushEraser();
        } else {
            mPhotoEditor.setBrushSize(brushSize);
            mTxtCurrentTool.setText(R.string.label_brush);
        }
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                hideframe();
                showFilter(false);
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                PropertiesBSFragment.iserase = false;
                mEditingToolsAdapter.notifyDataSetChanged();
                break;
            case TEXT:
                hideframe();
                showFilter(false);
                mPhotoEditor.setBrushDrawingMode(false);
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode, Typeface font) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);
                        styleBuilder.withTextFont(font);

                        mPhotoEditor.addText(inputText, styleBuilder);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                mEditingToolsAdapter.notifyDataSetChanged();
                break;
            case ERASER:
                hideframe();
                showFilter(false);
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser_mode);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                PropertiesBSFragment.iserase = true;
                mEditingToolsAdapter.notifyDataSetChanged();
                break;
            case FILTER:
                hideframe();
                mPhotoEditor.setBrushDrawingMode(false);
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                mEditingToolsAdapter.notifyDataSetChanged();
                break;
            case CROP:
                hideframe();
                showFilter(false);
                mPhotoEditor.setBrushDrawingMode(false);
                saveImageforcrop();
//                cropImage(Uri.parse(imagepath));
//                // create explicit intent
//                Intent intent = new Intent(this, CropImage.class);
//
//                // tell CropImage activity to look for image to crop
//                intent.putExtra(CropImage.IMAGE_PATH, imagepath);
//
//                // allow CropImage activity to rescale image
//                intent.putExtra(CropImage.SCALE, true);
//
//                // if the aspect ratio is fixed to ratio 3/2
//                intent.putExtra(CropImage.ASPECT_X, 3);
//                intent.putExtra(CropImage.ASPECT_Y, 2);
//
//                // start activity CropImage with certain request code and listen
//                // for result
//                startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
                break;
            case STICKER:
                hideframe();
                showFilter(false);
                mPhotoEditor.setBrushDrawingMode(false);
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                mEditingToolsAdapter.notifyDataSetChanged();
                break;
            case REDO:
                hideframe();
                showFilter(false);
                mPhotoEditor.redo();
                mEditingToolsAdapter.notifyDataSetChanged();
                break;
            case UNDO:
                hideframe();
                showFilter(false);
                mPhotoEditor.undo();
                mEditingToolsAdapter.notifyDataSetChanged();
                break;
            case CLOSE:
                onBackPressed();
                break;

        }
    }

//    private void cropImage(Uri sourceUri) {
//        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), sourceUri.toString()));
//        Log.e("path",sourceUri.toString()+"::"+destinationUri.toString());
//        UCrop.Options options = new UCrop.Options();
//        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary));
//
//        UCrop.of(sourceUri, destinationUri)
//                .start(this);

//        cropper.CropImage.activity()
//                .start(this);


//    }


    void showFilter(boolean isVisible) {
//        if (mRvFilters.getVisibility() == View.GONE) {
//            mRvFilters.setVisibility(View.VISIBLE);
//
//        } else {
//            mRvFilters.setVisibility(View.GONE);
//        }
        if (isVisible) {
            mRvFilters.setVisibility(View.VISIBLE);

        } else {
            mRvFilters.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (mRvFilters.getVisibility() == View.VISIBLE) {
            mRvFilters.setVisibility(View.GONE);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.exit_without_save)
                    .setCancelable(false).setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveImage();
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("SOURCE_PATH", imagepath);
                    returnIntent.putExtra("OUTPUT_PATH", imagepath);
                    returnIntent.putExtra("IS_IMAGE_EDITED", false);

                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("SOURCE_PATH", imagepath);
            returnIntent.putExtra("OUTPUT_PATH", imagepath);
            returnIntent.putExtra("IS_IMAGE_EDITED", false);

            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }
}
