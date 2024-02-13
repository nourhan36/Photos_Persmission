package com.example.photos_persmission

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photos_persmission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val requestPhotosPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openPhotoPicker()
            } else {

            }
        }

    fun requestPhotosPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this, //<--context
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED -> {
                openPhotoPicker()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.READ_MEDIA_IMAGES
            ) -> {
                showPermissionRational()
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPhotosPermissionLauncher.launch(
                    android.Manifest.permission.READ_MEDIA_IMAGES
                )
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPhotosPermissionLauncher
        binding.button.setOnClickListener {
            requestPhotosPermission()
        }
    }

    private fun showDialog(
        message: String,
        posActionName: String? = null,
        posActionCallBack: (() -> Unit)? = null, //function
        negActionName: String? = null,
        negActionCallBack: (() -> Unit)? = null,
        isCancelable: Boolean = true // user can cancel
    ) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(message)

        posActionName?.let {
            alertDialogBuilder.setPositiveButton(it) { dialog, _ ->
                dialog.dismiss()
                posActionCallBack?.invoke()
            }
        }

        negActionName?.let {
            alertDialogBuilder.setNegativeButton(it) { dialog, _ ->
                dialog.dismiss()
                negActionCallBack?.invoke()
            }
        }

        alertDialogBuilder.setCancelable(isCancelable)
        alertDialogBuilder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 456)
            binding.imv.setImageURI(data?.data)
    }

    private fun openPhotoPicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 456)
    }

    fun showPermissionRational() {
        showDialog(
            "we need to access your photos",
            posActionName = "ShowAgain",
            posActionCallBack = {
                requestPhotosPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            },
            negActionName = "cancel",
        )
    }
}