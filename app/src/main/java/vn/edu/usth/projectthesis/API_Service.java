package vn.edu.usth.projectthesis;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface API_Service {
    @Multipart
    @POST("/uploads/files")
    Call<ResponseBody> uploadFile(
            @Part MultipartBody.Part file
    );
    @GET("/files")
    Call<List<FileData>> getFiles();
    @DELETE("delete/{filename}")
    Call<ResponseBody> deleteFile(@Path("filename") String filename);
}

