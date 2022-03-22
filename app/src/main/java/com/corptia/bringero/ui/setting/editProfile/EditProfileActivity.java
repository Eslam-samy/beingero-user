package com.corptia.bringero.ui.home.setting.editProfile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.corptia.bringero.BuildConfig;
import com.corptia.bringero.Common.Common;
import com.corptia.bringero.R;
import com.corptia.bringero.Remote.MyApolloClient;
import com.corptia.bringero.Remote.Retrofit.Image.UploadImageResponse;
import com.corptia.bringero.Remote.Retrofit.NetworkClient;
import com.corptia.bringero.Remote.Retrofit.WebServicesAPI;
import com.corptia.bringero.base.BaseActivity;
import com.corptia.bringero.databinding.ActivityEditProfileBinding;
import com.corptia.bringero.graphql.UpdateUserInfoMutation;
import com.corptia.bringero.model.UserModel;
import com.corptia.bringero.type.Gender;
import com.corptia.bringero.type.UserInfo;
import com.corptia.bringero.ui.home.HomeActivity;
import com.corptia.bringero.utils.ImageUpload.ImageContract;
import com.corptia.bringero.utils.ImageUpload.ImagePresenter;
import com.google.android.gms.common.util.IOUtils;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class EditProfileActivity extends BaseActivity implements ImageContract.View, DatePickerDialog.OnDateSetListener {
    ActivityEditProfileBinding binding;
    static final int REQUEST_TAKE_PHOTO = 101;
    static final int REQUEST_GALLERY_PHOTO = 102;
    static String[] permissions = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ImagePresenter mPresenter = new ImagePresenter(this);
    Uri photoURI;
    String path;
    AlertDialog dialog;

    Handler handler = new Handler();

    //For BirthDate
    DatePickerDialog datePickerDialog;
    int Year, Month, Day;
    Calendar calendar;
    Date birthDate = null;

    //for gender adapter
    ArrayAdapter<Object> adapterStatus;
    List<String> genders;
    String gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);


        calendar = Calendar.getInstance();


        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        initGenderSpinner();
        binding.imgAvatar.setOnClickListener((View.OnClickListener) view -> {
            //Here Set Image
            selectImage();
        });
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Here Save Data To Server

                String firstName = binding.inputFirstName.getText().toString().trim();
                String lastName = binding.inputLastName.getText().toString().trim();
                String email = binding.inputEmail.getText().toString().trim();

                if (firstName.isEmpty()) {
                    Toasty.info(EditProfileActivity.this, getString(R.string.first_name_field_is_required)).show();
                    return;
                }

                if (lastName.isEmpty()) {
                    Toasty.info(EditProfileActivity.this, getString(R.string.last_name_field_is_required)).show();
                    return;
                }


                //TODO Chk Email Patirn
                dialog.show();

                if (path != null) {

                    sendImage(firstName, lastName, email, path);

                } else {
                    updateInfo(firstName, lastName, email, Common.CURRENT_USER.getAvatarImageId());

                }


            }
        });

        binding.inputDob.setOnClickListener(view -> {
            datePickerDialog.show(getSupportFragmentManager(), "Datepickerdialog");
        });
        fetchLocalData();

        try {
            initDatePickerDialog();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initGenderSpinner() {
        spinnerAdapter();
        genders = new ArrayList<>();
        binding.genderSelect.setAdapter(adapterStatus);

        genders.add(getString(R.string.gender));
        genders.add(getString(R.string.male));
        genders.add(getString(R.string.female));
        adapterStatus.clear();
        Log.d("ESLAM", "initGenderSpinner: " + Common.CURRENT_USER.getGender());
        if (Common.CURRENT_USER.getGender().toString().equals("MALE")) {
            binding.genderSelect.setSelection(1);
        } else binding.genderSelect.setSelection(2);


        adapterStatus.addAll(genders);
        adapterStatus.notifyDataSetChanged();
    }

    private void spinnerAdapter() {

        adapterStatus = new ArrayAdapter<Object>(this, R.layout.spinner_text, new ArrayList<>()) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public View getDropDownView(int position, @androidx.annotation.Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(getColor(R.color.colorGray));
                } else {
                    tv.setTextColor(getColor(R.color.black));
                }
                return view;
            }

        };
        adapterStatus.setDropDownViewResource(R.layout.spinner_list);
    }

    private void fetchLocalData() {

        UserModel userData = Common.CURRENT_USER;
        if (userData.getEmail() != null)
            binding.inputEmail.setText(userData.getEmail());

        binding.inputFirstName.setText(userData.getFirstName());
        binding.inputLastName.setText(userData.getLastName());

        if (userData.getAvatarName() != null)
            Picasso.get().load(Common.BASE_URL_IMAGE + userData.getAvatarName())
                    .placeholder(R.drawable.ic_placeholder_profile)
                    .into(binding.imgAvatar);

//        if (userData.getGender()!=null){
//            Gender gender =userData.getGender();
//            if (gender.rawValue().equalsIgnoreCase(Gender.MALE.rawValue()))
//                radioMale.setChecked(true);
//            else if (gender.rawValue().equalsIgnoreCase(Gender.FEMALE.rawValue()))
//                radioFemale.setChecked(true);
//
//        }

        if (userData.getBirthDate() != null) {
            binding.inputDob.setText(userData.getBirthDate().toString());


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            Calendar oldDate = Calendar.getInstance();
            try {
                oldDate.setTime(sdf.parse(userData.getBirthDate().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Year = oldDate.get(Calendar.YEAR);
            Log.d("HAZEM", "HHH : " + Year);
            Month = oldDate.get(Calendar.MONTH);
            Log.d("HAZEM", "HHH : " + Month);

            Day = oldDate.get(Calendar.DAY_OF_MONTH);
            Log.d("HAZEM", "HHH : " + Day);

        }

    }

    private void updateInfo(String firstName, String lastName, String email, String avatarImageId) {

        gender = binding.genderSelect.getSelectedItem().toString();
        Gender gender;
        if (binding.genderSelect.getSelectedItem().equals(getString(R.string.male)))
            gender = Gender.MALE;
        else if (binding.genderSelect.getSelectedItem().equals(getString(R.string.female)))
            gender = Gender.FEMALE;
        else gender = null;

        if (!email.isEmpty()) {
            if (!emailValidator(email)) {
                dialog.dismiss();
                Toasty.info(EditProfileActivity.this, getString(R.string.invalid_email_address)).show();
                return;
            }
        }
        UserInfo userInfo;
        if (!email.isEmpty()) {
            if (gender == null) {
                userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarImageId(avatarImageId)
                        .birthDate(birthDate)
                        .email(email)
                        .build();
            } else {
                userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarImageId(avatarImageId)
                        .birthDate(birthDate)
                        .gender(gender)
                        .email(email)
                        .build();
            }

        } else {

            if (gender == null) {
                userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarImageId(avatarImageId)
                        .birthDate(birthDate)
                        .build();
            } else {
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

                                if (response.data().UserMutation().updateInfo().status() == 200) {
                                    Common.CURRENT_USER.setFirstName(firstName);
                                    Common.CURRENT_USER.setLastName(lastName);
                                    Common.CURRENT_USER.setEmail(email);
                                    Common.CURRENT_USER.setAvatarImageId(avatarImageId);
                                    if (response.data().UserMutation().updateInfo().data().AvatarResponse().status() == 200)
                                        Common.CURRENT_USER.setAvatarName(response.data().UserMutation().updateInfo().data().AvatarResponse().data().name());

                                    if (gender != null)
                                        Common.CURRENT_USER.setGender(response.data().UserMutation().updateInfo().data().gender());
                                    else
                                        Common.CURRENT_USER.setGender(Gender.$UNKNOWN);

                                    Common.CURRENT_USER.setBirthDate(binding.inputDob.getText().toString());

                                    Toasty.success(EditProfileActivity.this, getString(R.string.successful_update)).show();

                                    //Start Again
                                    Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);


                                } else {


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

                path = getFilePathFromURI(EditProfileActivity.this, photoURI);

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
            int result = ActivityCompat.checkSelfPermission(EditProfileActivity.this, mPermission);
            if (result == PackageManager.PERMISSION_DENIED) return false;
        }
        return true;
    }


    @Override
    public void showPermissionDialog() {
        Dexter.withActivity(EditProfileActivity.this).withPermissions(permissions)
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
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES);

    }

    @Override
    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void startCamera(File file) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(EditProfileActivity.this.getPackageManager()) != null) {
            if (file != null) {
                photoURI = FileProvider.getUriForFile(EditProfileActivity.this,
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
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
        Toast.makeText(EditProfileActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void displayImagePreview(String mFilePath) {
        Picasso.get()
                .load(mFilePath)
                .placeholder(R.drawable.ic_logo_app)
                .into(binding.imgAvatar);
    }

    @Override
    public void displayImagePreview(Uri mFileUri) {
        Picasso.get()
                .load(mFileUri)
                .placeholder(R.drawable.ic_logo_app)
                .into(binding.imgAvatar);
    }

    @Override
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
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
            binding.inputDob.setText(dateFormat.format(birthDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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

    private void sendImage(String firstName, String lastName, String email, String filePath) {

        Retrofit retrofit = NetworkClient.getRetrofitClient(EditProfileActivity.this);
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

                        updateInfo(firstName, lastName, email, idImage);
                    } else {

                        Toasty.error(EditProfileActivity.this, response.body().getMessage()).show();
                    }


                } else
                    Toasty.error(EditProfileActivity.this, response.message()).show();
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

    public boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
