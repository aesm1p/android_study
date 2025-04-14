package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

public class MyFileProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.myapplication.fileprovider";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // 匹配所有路径格式：content://com.example.fileprovider/{file_path}
//        uriMatcher.addURI(AUTHORITY, "*", 1);
        uriMatcher.addURI(AUTHORITY, "*", 1);
        uriMatcher.addURI(AUTHORITY, "data/data/com.example.myapplication/files/flag", 2);
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {

        Log.i("victim", "openFile: " + uri.toString());
//        if (uriMatcher.match(uri) != 1) {
//            throw new FileNotFoundException("Invalid URI: " + uri);
//        }
        Log.i("victim", uriMatcher.match(uri) + " : " + uri.toString());

        String filePath = uri.getPath();
        File targetFile = new File(filePath);
        Log.i("victim", "getpath: " + filePath);

        if (!targetFile.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        // 返回文件描述符（只读模式）
        return ParcelFileDescriptor.open(targetFile, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    public MyFileProvider() {
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
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}