package id.co.sevima.edlinkduplicate.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.sevima.edlinkduplicate.ArticleActivity;
import id.co.sevima.edlinkduplicate.BaseActivity;
import id.co.sevima.edlinkduplicate.Constants;
import id.co.sevima.edlinkduplicate.HomeActivity;
import id.co.sevima.edlinkduplicate.R;
import id.co.sevima.edlinkduplicate.Util;
import id.co.sevima.edlinkduplicate.adapter.PostAdapter;

public class PostFragment extends Fragment {
    public static PostFragment instance;
    View view;
    HomeActivity activity;
    RecyclerView postList;
    ArrayList<JSONObject> posts;
    PostAdapter adapter;
    LinearLayout progress;
    SwipeRefreshLayout swipe;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post, container, false);
        progress = view.findViewById(R.id.progress);
        postList = view.findViewById(R.id.posts);
        swipe = view.findViewById(R.id.swipe);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (HomeActivity)getActivity();
        postList.setLayoutManager(new LinearLayoutManager(activity));
        postList.setItemAnimator(new DefaultItemAnimator());
        posts = new ArrayList<>();
        adapter = new PostAdapter(activity, posts, new PostAdapter.Listener() {

            @Override
            public void onSelected(int position, JSONObject post) {
                startActivity(new Intent(activity, ArticleActivity.class)
                        .putExtra("post", post.toString())
                        .putExtra("like_count", Util.getInt(post, "like_count", 0))
                        .putExtra("comment_count", Util.getInt(post, "comment_count", 0)));
            }

            @Override
            public void onLiked(int position, JSONObject post) {
                int liked = Util.getInt(post, "is_liked", 0);
                if (liked == 0) {
                    liked = 1;
                } else {
                    liked = 0;
                }
                activity.post(new BaseActivity.Listener() {

                    @Override
                    public void onResponse(String response) {
                        activity.log(response);
                    }
                }, Constants.API_URL+"/user/update_post_like", "user_id", ""+Constants.USER_ID,
                        "post_id", ""+Util.getInt(post, "id", 0), "liked", ""+liked);
                try {
                    post.put("is_liked", liked);
                    int likeCount = Util.getInt(post, "like_count", 0);
                    if (liked == 1) {
                        likeCount++;
                    } else {
                        if (likeCount > 0) {
                            likeCount--;
                        }
                    }
                    post.put("like_count", likeCount);
                    adapter.notifyItemChanged(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCommented(int position, JSONObject post) {

            }
        });
        postList.setAdapter(adapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getPosts();
            }
        });
        getPosts();
    }

    public void updateLikeStatus(int postID, int liked, int likeCount) {
        for (int i=0; i<posts.size(); i++) {
            JSONObject post = posts.get(i);
            if (Util.getInt(post, "id", 0) == postID) {
                try {
                    posts.get(i).put("is_liked", liked);
                    posts.get(i).put("like_count", likeCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    public void updateCommentCount(int postID, int commentCount) {
        for (int i=0; i<posts.size(); i++) {
            JSONObject post = posts.get(i);
            if (Util.getInt(post, "id", 0) == postID) {
                try {
                    posts.get(i).put("comment_count", commentCount);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    public void getPosts() {
        progress.setVisibility(View.VISIBLE);
        posts.clear();
        adapter.notifyDataSetChanged();
        activity.post(new BaseActivity.Listener() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONArray postsJSON = new JSONArray(response);
                    for (int i=0; i<postsJSON.length(); i++) {
                        JSONObject postJSON = postsJSON.getJSONObject(i);
                        posts.add(postJSON);
                    }
                    adapter.notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                    swipe.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Constants.API_URL+"/user/get_posts", "user_id", ""+Constants.USER_ID);
    }
}
