package id.co.sevima.edlinkduplicate.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import id.co.sevima.edlinkduplicate.Constants;
import id.co.sevima.edlinkduplicate.R;
import id.co.sevima.edlinkduplicate.Util;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    Context context;
    ArrayList<JSONObject> comments;
    Listener listener;

    public CommentAdapter(Context context, ArrayList<JSONObject> comments, Listener listener) {
        this.context = context;
        this.comments = comments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final JSONObject comment = comments.get(position);
            String type = Util.getString(comment, "type", "").trim();
            if (type.equals("text")) {
                holder.commentView.setVisibility(View.VISIBLE);
                holder.imgView.setVisibility(View.GONE);
            } else {
                holder.commentView.setVisibility(View.GONE);
                holder.imgView.setVisibility(View.VISIBLE);
                Picasso.get().load(Uri.parse(Constants.USERDATA_URL+Util.getString(comment, "image", "")))
                        .resize(256, 0).onlyScaleDown().into(holder.imgView);
            }
            try {
                Picasso.get().load(Uri.parse(Constants.USERDATA_URL + Util.getString(comment.getJSONObject("user"), "profile_picture", "")))
                        .resize(128, 0).onlyScaleDown().into(holder.profilePictureView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.nameView.setText(Util.getString(comment.getJSONObject("user"), "name", ""));
            holder.commentView.setText(Util.getString(comment, "comment", ""));
            try {
                holder.dateView.setText(new PrettyTime().format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                        Util.getString(comment, "date", "")
                )));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Util.getInt(comment, "user_id", 0) == Constants.USER_ID) {
                holder.commentOptions.setVisibility(View.VISIBLE);
            } else {
                holder.commentOptions.setVisibility(View.GONE);
            }
            holder.edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onEdited(position, comment);
                    }
                }
            });
            holder.delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onDeleted(position, comment);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profilePictureView;
        public TextView nameView, commentView, dateView;
        public ImageView imgView;
        public LinearLayout commentOptions, edit, delete;

        public ViewHolder(View view) {
            super(view);
            profilePictureView = view.findViewById(R.id.profile_picture);
            nameView = view.findViewById(R.id.name);
            commentView = view.findViewById(R.id.comment);
            dateView = view.findViewById(R.id.date);
            imgView = view.findViewById(R.id.img);
            commentOptions = view.findViewById(R.id.comment_options);
            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);
        }
    }

    public interface Listener {

        void onSelected(int position, JSONObject comment);
        void onEdited(int position, JSONObject comment);
        void onDeleted(int position, JSONObject comment);
    }
}
