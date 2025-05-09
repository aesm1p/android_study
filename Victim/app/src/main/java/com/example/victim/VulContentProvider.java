package com.example.victim;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

public class VulContentProvider extends ContentProvider {
    private static final String TAG = "victimAppVulProvider";
    public VulContentProvider() {
    }

    @Override  // android.content.ContentProvider
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        Log.i(TAG, "openFile URI: " + uri.toString());
        File root = this.getContext().getExternalFilesDir("sandbox");
        Log.i(TAG, this.getContext().getExternalFilesDir("sandbox").toString());
        Log.i(TAG, "uri.getLastPathSegment(): " + uri.getLastPathSegment());

        File file = new File(this.getContext().getExternalFilesDir("sandbox"), uri.getLastPathSegment());
        try {
            Log.i(TAG, "root.getCanonicalPath()): " + root.getCanonicalPath());
            Log.i(TAG, "file.getCanonicalPath(): " + file.getCanonicalPath());
            Log.i(TAG, "file.getCanonicalPath().startsWith(root.getCanonicalPath()): " + file.getCanonicalPath().startsWith(root.getCanonicalPath()));
            if(!file.getCanonicalPath().startsWith(root.getCanonicalPath())) {
                throw new IllegalArgumentException();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "ParcelFileDescriptor.open");
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}