// Generated by view binder compiler. Do not edit!
package com.example.foodies.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.denzcoskun.imageslider.ImageSlider;
import com.example.foodies.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final FloatingActionButton fabAdd;

  @NonNull
  public final FloatingActionButton fabFav;

  @NonNull
  public final FloatingActionButton fabProfile;

  @NonNull
  public final FloatingActionButton fabView;

  @NonNull
  public final FrameLayout flFragment;

  @NonNull
  public final ImageSlider imageSlider;

  @NonNull
  public final RelativeLayout main;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final RelativeLayout register;

  @NonNull
  public final ImageView search;

  private ActivityMainBinding(@NonNull RelativeLayout rootView,
      @NonNull FloatingActionButton fabAdd, @NonNull FloatingActionButton fabFav,
      @NonNull FloatingActionButton fabProfile, @NonNull FloatingActionButton fabView,
      @NonNull FrameLayout flFragment, @NonNull ImageSlider imageSlider,
      @NonNull RelativeLayout main, @NonNull RecyclerView recyclerView,
      @NonNull RelativeLayout register, @NonNull ImageView search) {
    this.rootView = rootView;
    this.fabAdd = fabAdd;
    this.fabFav = fabFav;
    this.fabProfile = fabProfile;
    this.fabView = fabView;
    this.flFragment = flFragment;
    this.imageSlider = imageSlider;
    this.main = main;
    this.recyclerView = recyclerView;
    this.register = register;
    this.search = search;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fabAdd;
      FloatingActionButton fabAdd = ViewBindings.findChildViewById(rootView, id);
      if (fabAdd == null) {
        break missingId;
      }

      id = R.id.fabFav;
      FloatingActionButton fabFav = ViewBindings.findChildViewById(rootView, id);
      if (fabFav == null) {
        break missingId;
      }

      id = R.id.fabProfile;
      FloatingActionButton fabProfile = ViewBindings.findChildViewById(rootView, id);
      if (fabProfile == null) {
        break missingId;
      }

      id = R.id.fabView;
      FloatingActionButton fabView = ViewBindings.findChildViewById(rootView, id);
      if (fabView == null) {
        break missingId;
      }

      id = R.id.flFragment;
      FrameLayout flFragment = ViewBindings.findChildViewById(rootView, id);
      if (flFragment == null) {
        break missingId;
      }

      id = R.id.imageSlider;
      ImageSlider imageSlider = ViewBindings.findChildViewById(rootView, id);
      if (imageSlider == null) {
        break missingId;
      }

      RelativeLayout main = (RelativeLayout) rootView;

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.register;
      RelativeLayout register = ViewBindings.findChildViewById(rootView, id);
      if (register == null) {
        break missingId;
      }

      id = R.id.search;
      ImageView search = ViewBindings.findChildViewById(rootView, id);
      if (search == null) {
        break missingId;
      }

      return new ActivityMainBinding((RelativeLayout) rootView, fabAdd, fabFav, fabProfile, fabView,
          flFragment, imageSlider, main, recyclerView, register, search);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
