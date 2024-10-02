package vn.edu.usth.projectthesis;

import android.app.Dialog;
import android.content.Context;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Files_Adapter extends RecyclerView.Adapter<Files_Adapter.FilesViewHolder> {
    List<FileData> fileDataList;
    Context context;
    private OnFileDeleteListener deleteListener;

    public Files_Adapter(Context context, List<FileData> files, OnFileDeleteListener deleteListener){
        this.context = context;
        this.fileDataList = files;
        this.deleteListener = deleteListener;
    }
    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        return new FilesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
            FileData fileData = fileDataList.get(position);
            holder.title.setText(fileData.getFilename());
            if (position % 2 ==0){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_gray));
            }
            holder.moreOptionsButton.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenu().clear(); // Clear any previous items
                if (fileData.getFilename().toLowerCase().endsWith(".hdr")) {
                    popupMenu.getMenu().add(Menu.NONE, R.id.view_option, Menu.NONE, "View");
                }
                popupMenu.getMenu().add(Menu.NONE, R.id.delete_option, Menu.NONE, "Delete");
                // Handle item clicks
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.view_option) {
                        showImagePopup(fileData.getId());
                        return true;
                    } else if (item.getItemId() == R.id.delete_option) {
                        deleteFile(fileData.getFilename());
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            });
    }

    @Override
    public int getItemCount() {
        return fileDataList.size();
    }

    public static class FilesViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        public ImageButton moreOptionsButton;
        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.files_title);
            moreOptionsButton = itemView.findViewById(R.id.more_options_button);
        }
    }
    private void showImagePopup(int fileId) {
        Log.d("view", "da an nut view");
        // Create a dialog to display the image
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.visualized_png_popup); // Use the custom layout

        // Find the ImageView in the custom layout
        ImageView imageViewPopup = dialog.findViewById(R.id.imageViewPopup);

        // Construct the API endpoint URL using the fileId
        String imageUrl = "http://10.0.2.2:5000/get_image_from_hdr/" + fileId;

        // Use Glide (or Picasso) to load the image from the URL into the ImageView
        Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.error)
                        // Optional error image if load fails
                .into(imageViewPopup);

        // Show the dialog
        dialog.show();
    }

    private void deleteFile(String filename) {
        API_Service apiService = API_Client.getClient().create(API_Service.class);
        Call<ResponseBody> call = apiService.deleteFile(filename);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "File deleted successfully", Toast.LENGTH_SHORT).show();
                    if (deleteListener != null){
                        deleteListener.onFileDeleted();
                    }
                } else {
                    Toast.makeText(context, "Error deleting file: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Failed to delete file: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
