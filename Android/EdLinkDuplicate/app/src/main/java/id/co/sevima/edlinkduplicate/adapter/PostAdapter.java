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

import id.co.sevima.edlinkduplicate.Constants;
import id.co.sevima.edlinkduplicate.R;
import id.co.sevima.edlinkduplicate.Util;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    ArrayList<JSONObject> posts;
    Listener listener;

    public PostAdapter(Context context, ArrayList<JSONObject> posts, Listener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.post, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final JSONObject post = posts.get(position);
            Picasso.get().load(Uri.parse(Constants.USERDATA_URL+ Util.getString(post, "img", "")))
                    .resize(512, 0).onlyScaleDown().into(holder.img);
            holder.source.setText(Util.getString(post, "source", ""));
            try {
                holder.date.setText(new PrettyTime().format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                        Util.getString(post, "date", "")
                )));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Picasso.get().load(Uri.parse(Constants.USERDATA_URL+Util.getString(post, "logo", "")))
                    .resize(128, 0).onlyScaleDown().into(holder.logo);
            holder.title.setText(Util.getString(post, "title", ""));
            holder.description.setText(Util.getString(post, "content", ""));
            holder.likes.setText(""+Util.getInt(post, "like_count", 0)+" "+context.getResources().getString(R.string.likes));
            int liked = Util.getInt(post, "is_liked", 0);
            if (liked == 1) {
                holder.likeIconView.setImageResource(R.drawable.like_active);
            } else {
                holder.likeIconView.setImageResource(R.drawable.like);
            }
            holder.select.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSelected(position, post);
                    }
                }
            });
            holder.like.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onLiked(position, post);
                    }
                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCommented(position, post);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout select, like, comment;
        public ImageView logo, img, likeIconView, commentIconView;
        public TextView source, date, title, description, likes;

        public ViewHolder(View view) {
            super(view);
            select = view.findViewById(R.id.select);
            like = view.findViewById(R.id.like);
            comment = view.findViewById(R.id.comment);
            logo = view.findViewById(R.id.logo);
            img = view.findViewById(R.id.img);
            likeIconView = view.findViewById(R.id.like_icon);
            commentIconView = view.findViewById(R.id.comment_icon);
            source = view.findViewById(R.id.source);
            date = view.findViewById(R.id.date);
            title = view.findViewById(R.id.title);
            description = view.findViewById(R.id.description);
            likes = view.findViewById(R.id.likes);
        }
    }

    public interface Listener {

        void onSelected(int position, JSONObject post);
        void onLiked(int position, JSONObject post);
        void onCommented(int position, JSONObject post);
    }
}
