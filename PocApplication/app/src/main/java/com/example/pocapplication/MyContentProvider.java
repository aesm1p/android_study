package com.example.pocapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;

public class MyContentProvider extends ContentProvider {
    private static  final String AUTHORITY = "com.example.pocapplication";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public MyContentProvider() {
    }

    static {
        uriMatcher.addURI(AUTHORITY, "files/*", 1);
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException{
        // 检查 URI 合法性
        if (uriMatcher.match(uri) != 1) {
            throw new FileNotFoundException("Unsupported URI: " + uri);
        }

        // 从 URI 中提取文件名
        String fileName = uri.getLastPathSegment();
        File file = new File(getContext().getFilesDir(), fileName); // 从应用私有目录获取文件

        // 检查文件是否存在
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + fileName);
        }

        // 返回文件描述符（只读模式）
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