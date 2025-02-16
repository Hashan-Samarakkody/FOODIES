// Generated by view binder compiler. Do not edit!
package com.example.foodies.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.foodies.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityProfileBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final ImageView backIcon;

  @NonNull
  public final Button cancelButton;

  @NonNull
  public final Button deleteButton;

  @NonNull
  public final EditText editName;

  @NonNull
  public final FloatingActionButton fabHome;

  @NonNull
  public final Button logOutButton;

  @NonNull
  public final Button saveButton;

  @NonNull
  public final TextView userEmail;

  @NonNull
  public final ImageView userIcon;

  @NonNull
  public final TextView userName;

  private ActivityProfileBinding(@NonNull RelativeLayout rootView, @NonNull ImageView backIcon,
      @NonNull Button cancelButton, @NonNull Button deleteButton, @NonNull EditText editName,
      @NonNull FloatingActionButton fabHome, @NonNull Button logOutButton,
      @NonNull Button saveButton, @NonNull TextView userEmail, @NonNull ImageView userIcon,
      @NonNull TextView userName) {
    this.rootView = rootView;
    this.backIcon = backIcon;
    this.cancelButton = cancelButton;
    this.deleteButton = deleteButton;
    this.editName = editName;
    this.fabHome = fabHome;
    this.logOutButton = logOutButton;
    this.saveButton = saveButton;
    this.userEmail = userEmail;
    this.userIcon = userIcon;
    this.userName = userName;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityProfileBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityProfileBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_profile, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityProfileBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.backIcon;
      ImageView backIcon = ViewBindings.findChildViewById(rootView, id);
      if (backIcon == null) {
        break missingId;
      }

      id = R.id.cancelButton;
      Button cancelButton = ViewBindings.findChildViewById(rootView, id);
      if (cancelButton == null) {
        break missingId;
      }

      id = R.id.deleteButton;
      Button deleteButton = ViewBindings.findChildViewById(rootView, id);
      if (deleteButton == null) {
        break missingId;
      }

      id = R.id.editName;
      EditText editName = ViewBindings.findChildViewById(rootView, id);
      if (editName == null) {
        break missingId;
      }

      id = R.id.fabHome;
      FloatingActionButton fabHome = ViewBindings.findChildViewById(rootView, id);
      if (fabHome == null) {
        break missingId;
      }

      id = R.id.logOutButton;
      Button logOutButton = ViewBindings.findChildViewById(rootView, id);
      if (logOutButton == null) {
        break missingId;
      }

      id = R.id.saveButton;
      Button saveButton = ViewBindings.findChildViewById(rootView, id);
      if (saveButton == null) {
        break missingId;
      }

      id = R.id.userEmail;
      TextView userEmail = ViewBindings.findChildViewById(rootView, id);
      if (userEmail == null) {
        break missingId;
      }

      id = R.id.userIcon;
      ImageView userIcon = ViewBindings.findChildViewById(rootView, id);
      if (userIcon == null) {
        break missingId;
      }

      id = R.id.userName;
      TextView userName = ViewBindings.findChildViewById(rootView, id);
      if (userName == null) {
        break missingId;
      }

      return new ActivityProfileBinding((RelativeLayout) rootView, backIcon, cancelButton,
          deleteButton, editName, fabHome, logOutButton, saveButton, userEmail, userIcon, userName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
