package com.corptia.bringero.ui.setting.editProfile;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.BuildConfig;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.Interface.CallbackListener;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.Remote.Retrofit.Image.UploadImageResponse;
import com.corptia.bringero.Remote.Retrofit.NetworkClient;
import com.corptia.bringero.Remote.Retrofit.WebServicesAPI;
import com.corptia.bringero.graphql.MeQuery;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.utils.ImageUpload.ImageContract;
import com.corptia.bringero.utils.ImageUpload.ImagePresenter;
import com.corptia.bringero.utils.PicassoUtils;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment implements ImageContract.View {

    static final int REQUEST_TAKE_PHOTO = 101;
    static final int REQUEST_GALLERY_PHOTO = 102;
    static String[] permissions = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ImagePresenter mPresenter = new ImagePresenter(this);
    Uri photoURI;
    String path;


    @BindView(R.id.input_email)
    TextInputLayout input_email;
    @BindView(R.id.input_firstName)
    TextInputLayout input_firstName;
    @BindView(R.id.input_lastName)
    TextInputLayout input_lastName;
    @BindView(R.id.input_birthDate)
    TextInputLayout input_birthDate;
    @BindView(R.id.img_avatar)
    CircleImageView img_avatar;

    @BindView(R.id.btn_save)
    Button btn_save;

    AlertDialog dialog ;

    Handler handler = new Handler();
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        ButterKnife.bind(this , view);
        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();


        //Here Fetch Local Data (Ok)
        MeQuery.UserData userData = Common.CURRENT_USER;
        if (userData.email()!=null)
            input_email.getEditText().setText(userData.email());

        input_firstName.getEditText().setText(userData.firstName());
        input_lastName.getEditText().setText(userData.lastName());

        if (userData.AvatarResponse().status() == 200)
        PicassoUtils.setImage(Common.BASE_URL_IMAGE + userData.AvatarResponse().data().name() , img_avatar);

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Here Set Image
                selectImage();
            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Here Save Data To Server

                String firstName = input_firstName.getEditText().getText().toString().trim();
                String lastName = input_lastName.getEditText().getText().toString().trim();
                String email = input_email.getEditText().getText().toString().trim();

                if (firstName.isEmpty())
                {
                    Toasty.info(getActivity(),getString(R.string.first_name_field_is_required)).show();
                    return;
                }

                if (lastName.isEmpty())
                {
                    Toasty.info(getActivity(),getString(R.string.last_name_field_is_required)).show();
                    return;
                }


                //TODO Chk Email Patirn
                dialog.show();

                if (path != null) {

                    sendImage(firstName ,lastName,email,path);

                }
                else
                {
                    updateInfo(firstName ,lastName,email,userData.avatarImageId());

                }



            }
        });


        return view;
    }

    private void updateInfo(String firstName, String lastName, String email, String avatarImageId) {


        UserInfo userInfo = UserInfo.builder()
                .firstName(firstName)
                .lastName(lastName)
                .avatarImageId(avatarImageId)
                .build();

        MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(userInfo).build())
                .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {


                        handler.post(new Runnable() {
                            @Override
                            public void run() {



                                if (response.data().UserMutation().updateInfo().status() == 200)
                                {


                                    Common.getMe(response.data().UserMutation().updateInfo().token(), new CallbackListener() {
                                        @Override
                                        public void OnSuccessCallback() {

                                            handler.post(() -> {
                                                dialog.hide();
                                                Toasty.success(getActivity() , getString(R.string.successful_update)  ).show();


                                            });
                                        }

                                        @Override
                                        public void OnFailedCallback() {

                                            handler.post(() -> {
                                                dialog.hide();
                                                Toasty.error(getActivity() , getString(R.string.save_failed)).show();
                                            });
                                        }
                                    });



                                }

                                else {

                                    dialog.hide();

                                }
                            }
                        });


                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                dialog.hide();
                            }
                        });
                    }
                });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                mPresenter.showPreview(photoURI);

                path = getFilePathFromURI(getActivity(), photoURI);

            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                photoURI = selectedImage;
                mPresenter.showPreview(selectedImage);
                path = getRealPathFromUri(selectedImage);

            }
        }
    }


    public boolean checkPermission() {

        for (String mPermission : permissions) {
            int result = ActivityCompat.checkSelfPermission(getActivity(), mPermission);
            if (result == PackageManager.PERMISSION_DENIED) return false;
        }
        return true;
    }

    @Override
    public void showPermissionDialog() {

        Dexter.withActivity(getActivity()).withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> showErrorDialog())
                .onSameThread()
                .check();

    }

    @Override
    public File getFilePath() {
        return getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    @Override
    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void startCamera(File file) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            if (file != null) {
                photoURI = FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider", file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    @Override
    public void chooseGallery() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);

    }

    @Override
    public void showNoSpaceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.error_message_no_more_space));
        builder.setMessage(getString(R.string.error_message_insufficient_space));
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @Override
    public int availableDisk() {
        File mFilePath = getFilePath();
        long freeSpace = mFilePath.getFreeSpace();
        return Math.round(freeSpace / 1048576);
    }

    @Override
    public File newFile() {
        Calendar cal = Calendar.getInstance();
        long timeInMillis = cal.getTimeInMillis();
        String mFileName = String.valueOf(timeInMillis) + ".jpeg";
        File mFilePath = getFilePath();
        try {
            File newFile = new File(mFilePath.getAbsolutePath(), mFileName);
            newFile.createNewFile();
            return newFile;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void showErrorDialog() {
        Toast.makeText(getActivity(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayImagePreview(String mFilePath) {
        //Glide.with(MainActivity.this).load(mFilePath).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.profile_pic)).into(userProfilePhoto);
        Picasso.get()
                .load(mFilePath)
                .placeholder(R.drawable.ic_logo_app)
                .into(img_avatar);

    }

    @Override
    public void displayImagePreview(Uri mFileUri) {
        Picasso.get()
                .load(mFileUri)
                .placeholder(R.drawable.ic_logo_app)
                .into(img_avatar);
    }

    @Override
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //---------------------


    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.message_need_permission));
        builder.setMessage(getString(R.string.message_grant_permission));
        builder.setPositiveButton(getString(R.string.label_setting), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo),
                getString(R.string.choose_gallery),
                getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals(getString(R.string.take_photo))) {
                mPresenter.cameraClick();
            } else if (items[item].equals(getString(R.string.choose_gallery))) {
                mPresenter.ChooseGalleryClick();
            } else if (items[item].equals(getString(R.string.cancel))) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    public String getOriginalImagePath() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        int column_index_data = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();

        return cursor.getString(column_index_data);
    }


    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void sendImage(String firstName ,String lastName,String email ,String filePath) {

        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        WebServicesAPI uploadAPIs = retrofit.create(WebServicesAPI.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("files", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Call<UploadImageResponse> call = uploadAPIs.postImage(part, description);

        call.enqueue(new Callback<UploadImageResponse>() {
            @Override
            public void onResponse(Call<UploadImageResponse> call, retrofit2.Response<UploadImageResponse> response) {

                if (response.isSuccessful()) {

                    if (response.body().getStatusCode() == 200) {

                        String idImage = response.body().getData().get(0).getData().getId();

                        //String FULL_PATH = Common.BASE_URL_WEB + path;

                        updateInfo(firstName , lastName,email ,idImage);
                    } else {

                        Toasty.error(getActivity(), response.body().getMessage()).show();
                    }


                } else
                    Toasty.error(getActivity(), response.message()).show();
            }

            @Override
            public void onFailure(Call<UploadImageResponse> call, Throwable t) {

                //               Log.i("dddd", "No Image eeee " + t.getMessage());

            }
        });
    }

}
