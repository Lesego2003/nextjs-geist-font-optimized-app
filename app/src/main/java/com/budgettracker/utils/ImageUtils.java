package com.budgettracker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    // Create a file to store the image
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = Constants.IMAGE_PREFIX + timeStamp;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
            imageFileName,
            Constants.IMAGE_EXTENSION,
            storageDir
        );
        return imageFile;
    }

    // Get content URI for the image file using FileProvider
    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(
            context,
            Constants.FILE_PROVIDER_AUTHORITY,
            file
        );
    }

    // Process and optimize the captured/selected image
    public static String processImage(Context context, Uri imageUri) {
        try {
            // Create a file to store the processed image
            File processedFile = createImageFile(context);
            
            // Get input stream from URI
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
            
            if (originalBitmap == null) {
                Log.e(TAG, "Failed to decode bitmap from URI: " + imageUri);
                return null;
            }

            // Resize the image if it's too large
            Bitmap resizedBitmap = resizeImage(originalBitmap);
            
            // Rotate the image if needed
            Bitmap rotatedBitmap = rotateImageIfNeeded(context, resizedBitmap, imageUri);
            
            // Save the processed image
            FileOutputStream fos = new FileOutputStream(processedFile);
            rotatedBitmap.compress(
                Bitmap.CompressFormat.JPEG,
                Constants.IMAGE_COMPRESSION_QUALITY,
                fos
            );
            fos.close();

            // Clean up
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle();
            }
            originalBitmap.recycle();

            return processedFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error processing image", e);
            return null;
        }
    }

    // Resize image if dimensions exceed maximum allowed
    private static Bitmap resizeImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        if (width <= Constants.MAX_IMAGE_DIMENSION && height <= Constants.MAX_IMAGE_DIMENSION) {
            return bitmap;
        }

        float ratio = (float) width / height;
        
        if (ratio > 1) {
            // Width is greater than height
            width = Constants.MAX_IMAGE_DIMENSION;
            height = (int) (width / ratio);
        } else {
            // Height is greater than width
            height = Constants.MAX_IMAGE_DIMENSION;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    // Rotate image based on EXIF orientation
    private static Bitmap rotateImageIfNeeded(Context context, Bitmap bitmap, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            ExifInterface ei = new ExifInterface(inputStream);
            int orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            );

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    return bitmap;
            }

            Bitmap rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
            );
            
            if (rotatedBitmap != bitmap) {
                bitmap.recycle();
            }
            
            return rotatedBitmap;
        } catch (IOException e) {
            Log.e(TAG, "Error getting EXIF orientation", e);
            return bitmap;
        }
    }

    // Delete image file
    public static boolean deleteImage(String imagePath) {
        if (imagePath == null) {
            return false;
        }
        
        File file = new File(imagePath);
        return file.exists() && file.delete();
    }

    // Load bitmap from file path with sampling to prevent OOM
    public static Bitmap loadBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    // Calculate optimal sampling size
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
