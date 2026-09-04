package com.example.ourbook;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
    @GlideModule
public class GlideModules extends AppGlideModule {
        public GlideModules() {
            super();
        }

        @Override
        public boolean isManifestParsingEnabled() {
            return super.isManifestParsingEnabled();
        }

        @Override
        public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
            super.applyOptions(context, builder);
        }
    }
