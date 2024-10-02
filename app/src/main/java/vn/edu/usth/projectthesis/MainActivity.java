package vn.edu.usth.projectthesis;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.OpenableColumns;
import android.widget.Toast;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnFileDeleteListener{
    private static final int PICK_IMAGE_REQUEST = 2;
    public DrawerLayout drawerLayout;
    private RecyclerView filesRecyclerView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarss);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        filesRecyclerView = findViewById(R.id.files_RecycleView);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.nav_open,R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Button buttonSelectFile = findViewById(R.id.file_upload_btn);
        buttonSelectFile.setOnClickListener(v -> {
            // Open file picker
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // This allows all file types
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), PICK_IMAGE_REQUEST);
        });
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.tab_files){

            }else if (id == R.id.tab_images){
                Intent intent = new Intent(MainActivity.this, Images_Tab_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }else if (id == R.id.tab_statistic){
                Intent intent = new Intent(MainActivity.this, Statistic_Tab_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }else if(id == R.id.tab_about){
                Intent intent = new Intent(MainActivity.this, About_Tab_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.tab_logout) {
                Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            return true;
        });

        fetchFiles(filesRecyclerView);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String filename = getFileName(imageUri);
            if (filename != null && filename.endsWith(".hdr")){
                uploadImageToServer(imageUri,filesRecyclerView);
            }else {
                Toast.makeText(this, "Only support .hdr file!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToServer(Uri imageUri,RecyclerView filesRecyclerView) {
        try {
            // Open an InputStream to read the content of the file
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = getBytesFromInputStream(inputStream); // Convert InputStream to byte array

            // Get the original filename (including the extension)
            String originalFilename = getFileName(imageUri);

            // Create RequestBody with the appropriate MediaType based on the original filename
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(imageUri)), bytes);

            // Use the original filename when creating the MultipartBody.Part
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", originalFilename, requestFile);

            API_Service apiService = API_Client.getClient().create(API_Service.class);
            Call<ResponseBody> call = apiService.uploadFile(body);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                        fetchFiles(filesRecyclerView);
                    } else {
                        Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Failed to upload file: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private String getFileName(Uri uri) {
        String result = null;

        // Check if the URI scheme is content
        if (uri.getScheme().equals("content")) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    // Get the index of the DISPLAY_NAME column
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    // Ensure the index is valid
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close(); // Always close the cursor to avoid memory leaks
                }
            }
        }

        // Fallback: If the name is still null, extract it from the URI path
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    private void fetchFiles(RecyclerView filesRecyclerView) {
        API_Service apiService = API_Client.getClient().create(API_Service.class);
        Call<List<FileData>> call = apiService.getFiles();

        call.enqueue(new Callback<List<FileData>>() {
            @Override
            public void onResponse(Call<List<FileData>> call, Response<List<FileData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FileData> files = response.body();
                    // Set the adapter
                    Files_Adapter adapter = new Files_Adapter(MainActivity.this, files,MainActivity.this);
                    filesRecyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch files", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FileData>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFileDeleted() {
        fetchFiles(filesRecyclerView);
    }
}