package id.co.sevima.edlinkduplicate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import id.co.sevima.edlinkduplicate.adapter.CommentAdapter;
import id.co.sevima.edlinkduplicate.adapter.PostAdapter;
import id.co.sevima.edlinkduplicate.fragments.PostFragment;

public class ArticleActivity extends BaseActivity {
    private final int SELECT_IMAGE = 1;
    JSONObject post = new JSONObject();
    int likeCount = 0;
    int commentCount = 0;
    ImageView logoView, imgView;
    TextView sourceView, dateView, titleView, contentView, likeCountView, likeCount2View, commentCountView, commentCount2View;
    BottomSheetBehavior bottomSheetBehavior;
    View dimmer;
    LinearLayout progress;
    RecyclerView commentList;
    ArrayList<JSONObject> comments;
    CommentAdapter adapter;
    EditText commentField;
    ImageView sendIconView;
    ImageView likeIconView, likeIcon2View;
    boolean isEditing = false;
    int currentCommentID = 0;
    int currentCommentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        try {
            post = new JSONObject(getIntent().getStringExtra("post"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        likeCount = getIntent().getIntExtra("like_count", 0);
        commentCount = getIntent().getIntExtra("comment_count", 0);
        setTitle(R.string.news);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logoView = findViewById(R.id.logo);
        imgView = findViewById(R.id.img);
        sourceView = findViewById(R.id.source);
        dateView = findViewById(R.id.date);
        titleView = findViewById(R.id.title);
        contentView = findViewById(R.id.content);
        likeCountView = findViewById(R.id.like_count);
        likeCount2View = findViewById(R.id.like_count_2);
        commentCountView = findViewById(R.id.comment_count);
        commentCount2View = findViewById(R.id.comment_count_2);
        progress = findViewById(R.id.progress);
        commentField = findViewById(R.id.comment);
        dimmer = findViewById(R.id.dimmer);
        commentList = findViewById(R.id.comments);
        sendIconView = findViewById(R.id.send_icon);
        likeIconView = findViewById(R.id.like_icon);
        likeIcon2View = findViewById(R.id.like_icon_2);
        commentList.setLayoutManager(new LinearLayoutManager(this));
        commentList.setItemAnimator(new DefaultItemAnimator());
        comments = new ArrayList<>();
        adapter = new CommentAdapter(this, comments, new CommentAdapter.Listener() {

            @Override
            public void onSelected(int position, JSONObject comment) {
            }

            @Override
            public void onEdited(int position, JSONObject comment) {
                currentCommentID = Util.getInt(comment, "id", 0);
                currentCommentPosition = position;
                isEditing = true;
                commentField.setText(Util.getString(comment, "comment", ""));
                commentField.requestFocus();
                Util.showKeyboard(ArticleActivity.this);
            }

            @Override
            public void onDeleted(int position, JSONObject comment) {
                new AlertDialog.Builder(ArticleActivity.this)
                        .setMessage(R.string.confirmation_1)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                final ProgressDialog dialog = createDialog(R.string.deleting_comment);
                                dialog.show();
                                post(new Listener() {

                                    @Override
                                    public void onResponse(String response) {
                                        dialog.dismiss();
                                        comments.remove(comment);
                                        adapter.notifyItemRemoved(position);
                                        commentCountView.setText("" + comments.size() + " " + getResources().getString(R.string.comment_count));
                                        commentCount2View.setText("" + comments.size() + " " + getResources().getString(R.string.comment_count));
                                        PostFragment.instance.updateCommentCount(Util.getInt(post, "id", 0), comments.size());
                                    }
                                }, Constants.API_URL + "/user/delete_comment", "id", "" + Util.getInt(comment, "id", 0));
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .create()
                        .show();
            }
        });
        commentList.setAdapter(adapter);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                dimmer.setAlpha(slideOffset);
            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Picasso.get().load(Uri.parse(Constants.USERDATA_URL + Util.getString(post, "logo", "")))
                .resize(128, 0).onlyScaleDown().into(logoView);
        Picasso.get().load(Uri.parse(Constants.USERDATA_URL + Util.getString(post, "img", "")))
                .resize(512, 0).onlyScaleDown().into(imgView);
        sourceView.setText(Util.getString(post, "source", ""));
        try {
            dateView.setText(new PrettyTime().format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    Util.getString(post, "date", "")
            )));
        } catch (Exception e) {
            e.printStackTrace();
        }
        titleView.setText(Util.getString(post, "title", ""));
        contentView.setText(Html.fromHtml(Util.getString(post, "content", "")));
        likeCountView.setText("" + likeCount + " " + getResources().getString(R.string.like_count));
        likeCount2View.setText("" + likeCount + " " + getResources().getString(R.string.like_count));
        commentCountView.setText("" + commentCount + " " + getResources().getString(R.string.comment_count));
        commentCount2View.setText("" + commentCount + " " + getResources().getString(R.string.comment_count));
        commentField.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String comment = commentField.getText().toString().trim();
                if (comment.equals("")) {
                    sendIconView.setImageResource(R.drawable.send);
                } else {
                    sendIconView.setImageResource(R.drawable.send_active);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        int liked = Util.getInt(post, "is_liked", 0);
        if (liked == 0) {
            likeIconView.setImageResource(R.drawable.like);
            likeIcon2View.setImageResource(R.drawable.like);
        } else {
            likeIconView.setImageResource(R.drawable.like_active);
            likeIcon2View.setImageResource(R.drawable.like_active);
        }
    }

    public void like(View view) {
        int liked = Util.getInt(post, "is_liked", 0);
        if (liked == 0) {
            liked = 1;
            likeIconView.setImageResource(R.drawable.like_active);
            likeIcon2View.setImageResource(R.drawable.like_active);
        } else {
            liked = 0;
            likeIconView.setImageResource(R.drawable.like);
            likeIcon2View.setImageResource(R.drawable.like);
        }
        int likeCount = Util.getInt(post, "like_count", 0);
        if (liked == 1) {
            likeCount++;
        } else {
            if (likeCount > 0) {
                likeCount--;
            }
        }
        likeCountView.setText("" + likeCount + " " + getResources().getString(R.string.like_count));
        likeCount2View.setText("" + likeCount + " " + getResources().getString(R.string.like_count));
        try {
            post.put("is_liked", liked);
            post.put("like_count", likeCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            PostFragment.instance.updateLikeStatus(Util.getInt(post, "id", 0), liked, likeCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        post(new BaseActivity.Listener() {

                 @Override
                 public void onResponse(String response) {
                     log(response);
                 }
             }, Constants.API_URL + "/user/update_post_like", "user_id", "" + Constants.USER_ID,
                "post_id", "" + Util.getInt(post, "id", 0), "liked", "" + liked);
    }

    public void sendComment(View view) {
        final String comment = commentField.getText().toString().trim();
        if (comment.equals("")) {
            return;
        }
        commentField.setText("");
        Util.hideKeyboard(this);
        if (isEditing) {
            isEditing = false;
            post(new Listener() {

                     @Override
                     public void onResponse(String response) {
                         try {
                             comments.get(currentCommentPosition).put("comment", comment);
                             adapter.notifyItemChanged(currentCommentPosition);
                             commentCountView.setText("" + comments.size() + " " + getResources().getString(R.string.comment_count));
                             commentCount2View.setText("" + comments.size() + " " + getResources().getString(R.string.comment_count));
                             PostFragment.instance.updateCommentCount(Util.getInt(post, "id", 0), comments.size());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                 }, Constants.API_URL + "/user/update_comment", "comment_id", "" + currentCommentID,
                    "comment", comment, "date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        } else {
            post(new Listener() {

                     @Override
                     public void onResponse(String response) {
                         try {
                             JSONObject comment = new JSONObject(response);
                             comments.add(comment);
                             adapter.notifyDataSetChanged();
                             commentCountView.setText("" + comments.size() + " " + getResources().getString(R.string.comment_count));
                             commentCount2View.setText("" + comments.size() + " " + getResources().getString(R.string.comment_count));
                             PostFragment.instance.updateCommentCount(Util.getInt(post, "id", 0), comments.size());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                 }, Constants.API_URL + "/user/send_comment", "user_id", "" + Constants.USER_ID,
                    "post_id", "" + Util.getInt(post, "id", 0),
                    "comment", comment,
                    "date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        }
    }

    public void showComments(View view) {
        dimmer.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        comments.clear();
        adapter.notifyDataSetChanged();
        commentList.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        getComments();
    }

    public void getComments() {
        post(new Listener() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONArray commentsJSON = new JSONArray(response);
                    for (int i = 0; i < commentsJSON.length(); i++) {
                        JSONObject commentJSON = commentsJSON.getJSONObject(i);
                        comments.add(commentJSON);
                    }
                    adapter.notifyDataSetChanged();
                    progress.setVisibility(View.GONE);
                    commentList.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, Constants.API_URL + "/user/get_comments", "post_id", "" + Util.getInt(post, "id", 0));
    }

    public void selectImage(View view) {
        Util.hideKeyboard(this);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), SELECT_IMAGE);
    }

    public void hideComments(View view) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            finish();
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                try {
                    InputStream stream = getContentResolver().openInputStream(data.getData());
                    File f = new File(getFilesDir(), "tmp.jpg");
                    FileOutputStream fos = new FileOutputStream(f);
                    int read;
                    byte[] buffer = new byte[8192];
                    while ((read = stream.read(buffer)) != -1) {
                        fos.write(buffer, 0, read);
                    }
                    fos.flush();
                    fos.close();
                    stream.close();
                    post(new Listener() {

                             @Override
                             public void onResponse(String response) {
                                 try {
                                     JSONObject comment = new JSONObject(response);
                                     comments.add(comment);
                                     adapter.notifyDataSetChanged();
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                             }
                         }, Constants.API_URL + "/user/send_comment_image", "user_id", "" + Constants.USER_ID,
                            "post_id", "" + Util.getInt(post, "id", 0),
                            "date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                            "file", UUID.randomUUID().toString(), "image/jpeg", f.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}