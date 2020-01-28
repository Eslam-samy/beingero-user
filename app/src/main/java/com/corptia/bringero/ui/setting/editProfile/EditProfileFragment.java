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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.Gender;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.ui.home.HomeActivity;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


public class EditProfileFragment extends Fragment implements ImageContract.View, DatePickerDialog.OnDateSetListener {

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
    @BindView(R.id.edt_date)
    EditText edt_date;
    @BindView(R.id.radioGroupGender)
    RadioGroup radioGroupGender;
    @BindView(R.id.radioMail)
    RadioButton radioMail;
    @BindView(R.id.radioFemail)
    RadioButton radioFemail;

    @BindView(R.id.btn_save)
    Button btn_save;

    AlertDialog dialog ;

    Handler handler = new Handler();

    //For BirthDate
    DatePickerDialog datePickerDialog;
    int Year, Month, Day;
    Calendar calendar;
    Date birthDate = null;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        calendar = Calendar.getInstance();


        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        ButterKnife.bind(this , view);

        dialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();


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
                    updateInfo(firstName ,lastName,email,Common.CURRENT_USER.getAvatarImageId());

                }



            }
        });

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show(getFragmentManager(), "Datepickerdialog");

            }
        });


        //Here Fetch Local Data (Ok)
        fetchLocalData();

        try {
            initDatePickerDialog();
        } catch (ParseException e) {
            e.printStackTrace();
        }




        return view;
    }

    private void fetchLocalData() {

        UserModel userData = Common.CURRENT_USER;
        if (userData.getEmail()!=null)
            input_email.getEditText().setText(userData.getEmail());

        input_firstName.getEditText().setText(userData.getFirstName());
        input_lastName.getEditText().setText(userData.getLastName());

        if (userData.getAvatarName() !=null )
            Picasso.get().load(Common.BASE_URL_IMAGE + userData.getAvatarName())
                    .placeholder(R.drawable.ic_placeholder_profile)
                    .into(img_avatar);

        if (userData.getGender()!=null){
            Gender gender =userData.getGender();
            if (gender.rawValue().equalsIgnoreCase(Gender.MALE.rawValue()))
                radioMail.setChecked(true);
            else if (gender.rawValue().equalsIgnoreCase(Gender.FEMALE.rawValue()))
                radioFemail.setChecked(true);

        }

        if (userData.getBirthDate()!=null )
        {
            edt_date.setText(userData.getBirthDate().toString());


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            Calendar oldDate = Calendar.getInstance();
            try {
                oldDate.setTime(sdf.parse(userData.getBirthDate().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Year = oldDate.get(Calendar.YEAR);
            Log.d("HAZEM" , "HHH : " + Year);
            Month = oldDate.get(Calendar.MONTH);
            Log.d("HAZEM" , "HHH : " + Month);

            Day = oldDate.get(Calendar.DAY_OF_MONTH);
            Log.d("HAZEM" , "HHH : " + Day);

        }

    }

    private void updateInfo(String firstName, String lastName, String email, String avatarImageId)  {



        Gender gender ;

        if (radioMail.isChecked())
            gender = Gender.MALE;
        else  if (radioFemail.isChecked())
            gender = Gender.FEMALE;
        else
            gender = Gender.$UNKNOWN;


        if (!email.isEmpty()){

            if (!emailValidator(email)){
                dialog.dismiss();
                Toasty.info(getActivity(), getString(R.string.invalid_email_address)).show();
                return;
            }
        }


        UserInfo userInfo;

        if (!email.isEmpty()){

            if (gender==null){
                userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarImageId(avatarImageId)
                        .birthDate(birthDate)
                        .email(email)
                        .build();
            }
            else
            {
                userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarImageId(avatarImageId)
                        .birthDate(birthDate)
                        .gender(gender)
                        .email(email)
                        .build();
            }

        }
        else
        {

            if (gender==null){
                userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarImageId(avatarImageId)
                        .birthDate(birthDate)
                        .build();
            }
            else
            {
                userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarImageId(avatarImageId)
                        .birthDate(birthDate)
                        .gender(gender)
                        .build();
            }


        }

        MyApolloClient.getApollowClientAuthorization().mutate(UpdateUserInfoMutation.builder().data(userInfo).build())
                .enqueue(new ApolloCall.Callback<UpdateUserInfoMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<UpdateUserInfoMutation.Data> response) {

                        //TODO Still Make Update Here

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                dialog.dismiss();

                                if (response.data().UserMutation().updateInfo().status() == 200)
                                {
                                    Common.CURRENT_USER.setFirstName(firstName);
                                    Common.CURRENT_USER.setLastName(lastName);
                                    Common.CURRENT_USER.setEmail(email);
                                    Common.CURRENT_USER.setAvatarImageId(avatarImageId);
                                    if (response.data().UserMutation().updateInfo().data().AvatarResponse().status() == 200)
                                    Common.CURRENT_USER.setAvatarName(response.data().UserMutation().updateInfo().data().AvatarResponse().data().name());

                                    if (gender!=null)
                                        Common.CURRENT_USER.setGender(response.data().UserMutation().updateInfo().data().gender());
                                    else
                                        Common.CURRENT_USER.setGender(Gender.$UNKNOWN);

                                    Common.CURRENT_USER.setBirthDate(edt_date.getText().toString());

                                    Toasty.success(getActivity() , getString(R.string.successful_update)  ).show();

                                    //Start Again
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);


                                }

                                else {


                                }
                            }
                        });


                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                dialog.dismiss();
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
        MultipartBody.Part part = MultipartBody.Part.createFormData("files", "File1", fileReqBody);
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


    private void initDatePickerDialog() throws ParseException {

        datePickerDialog = DatePickerDialog.newInstance(this, Year, Month, Day);
        datePickerDialog.setThemeDark(false);
        datePickerDialog.showYearPickerFirst(false);
        datePickerDialog.setTitle(getString(R.string.date_of_birth));
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setCancelText(getString(R.string.canceled));
        datePickerDialog.setOkText(getString(R.string.ok));


        // Setting Min Date to today date
//        Calendar min_date_c = Calendar.getInstance();
//        datePickerDialog.setMinDate(min_date_c);


        // Setting Max Date to next 3 years
//        Calendar max_date_c = Calendar.getInstance();
//        max_date_c.set(Calendar.YEAR, Year );
//        datePickerDialog.setMaxDate(max_date_c);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

//
//        String myFormat = "yyyy-MM-dd";
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        txt_date.setText(sdf.format(myCalendar.getTime()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en"));

        try {
            birthDate = new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
            input_birthDate.getEditText().setText(dateFormat.format(birthDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
