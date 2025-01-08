package com.example.persona_app.firebase

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.lang.Exception
import java.nio.file.FileSystems
import java.nio.file.Path

class StorageActivity {
    private val storage = Firebase.storage
    private val storageReference = storage.reference

    private  val  imagesPath = "news"
    private val imagesReference = storageReference.child(imagesPath)

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveImage(uri: Uri, onSuccess: (Uri) -> Unit, onFailure: (Exception) -> Unit){
        val path: Path = FileSystems.getDefault().getPath(uri.path)
        val name = path.fileName.toString()

        val imageReference = imagesReference.child(name)
        imageReference.putFile(uri)
            .addOnSuccessListener { uploadSnapshot ->
                uploadSnapshot.storage.downloadUrl
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure)
            }
            .addOnFailureListener(onFailure)
    }

    fun loadImage (imagePath: String): StorageReference
    {
        return storageReference.child(imagePath)
    }
}