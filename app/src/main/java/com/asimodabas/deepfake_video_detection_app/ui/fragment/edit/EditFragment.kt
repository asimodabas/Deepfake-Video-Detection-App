package com.asimodabas.deepfake_video_detection_app.ui.fragment.edit

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.DeleteAccountConfirmDialogBinding
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentEditBinding
import com.asimodabas.deepfake_video_detection_app.model.UserModel
import com.asimodabas.deepfake_video_detection_app.ui.activity.MainActivity
import com.asimodabas.deepfake_video_detection_app.util.Constants.IMAGE_NAME
import com.asimodabas.deepfake_video_detection_app.util.Constants.USERS
import com.asimodabas.deepfake_video_detection_app.util.datePickerCreate
import com.asimodabas.deepfake_video_detection_app.util.toastMessage
import com.asimodabas.deepfake_video_detection_app.util.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

class EditFragment : Fragment(R.layout.fragment_edit) {

    private val binding by viewBinding(FragmentEditBinding::bind)
    private val viewModel: EditViewModel by viewModels()
    private var selectedBitmap: Bitmap? = null
    private var selectedUri: Uri? = null
    private lateinit var currentUserMail: String
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private val neededRuntimePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val databaseReference = FirebaseDatabase.getInstance().getReference(USERS).child(
            FirebaseAuth.getInstance().uid.toString()
        )
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val myConfirmControl = snapshot.getValue(UserModel::class.java)
                    if (myConfirmControl != null) {
                        currentUserMail = myConfirmControl.email
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        binding.dateOfBirthDateText.setOnClickListener {
            datePickerCreate(requireContext(), binding.dateOfBirthDateText)
        }

        registerLauncher()
        getProfileInfo()
        uploadImage()
        uploadButton()
        deleteButton()
        updatePasswordButton()
    }

    private fun updatePasswordButton() {
        binding.updatePasswordButton.setOnClickListener {
            findNavController().navigate(R.id.action_editFragment_to_updatePasswordFragment)
        }
    }

    private fun getProfileInfo() {
        viewModel.pullUserInfo().observe(viewLifecycleOwner) { dataConfirm ->
            dataConfirm?.let {
                binding.myModel = dataConfirm
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                toastMessage(requireContext(), it)
            }
        }
    }

    private fun deleteButton() {
        binding.deleteAccountButton.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(R.string.delete_my_account)
            builder.setMessage(R.string.delete_account_confirm)
            builder.setCancelable(true)
            builder.setNegativeButton(R.string.no) { _, _ ->
            }
            builder.setPositiveButton(R.string.yes) { _, _ ->
                val dialog = Dialog(requireContext())
                val dialogPasswordBinding = DeleteAccountConfirmDialogBinding.inflate(
                    LayoutInflater.from(requireContext())
                )
                dialog.setContentView(dialogPasswordBinding.root)
                dialogPasswordBinding.buttonConfirm.setOnClickListener {
                    if (dialogPasswordBinding.editTextPass.text.isNotEmpty()) {
                        dialog.cancel()
                        val password = dialogPasswordBinding.editTextPass.text.toString().trim()
                        val credential = EmailAuthProvider.getCredential(currentUserMail, password)
                        val user = Firebase.auth.currentUser!!
                        user.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModel.deleteAccount()
                                viewModel.deleteAccountConfirmation.observe(
                                    viewLifecycleOwner
                                ) { confirm ->
                                    confirm?.let {
                                        if (it) {
                                            toastMessage(
                                                requireContext(),
                                                getString(R.string.deletion_success)
                                            )
                                            activity?.let { activity ->
                                                activity.startActivity(
                                                    Intent(
                                                        requireActivity(), MainActivity::class.java
                                                    )
                                                )
                                                activity.finish()
                                            }
                                        }
                                    }
                                }
                                viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
                                    error?.let {
                                        toastMessage(requireContext(), it)
                                    }
                                }
                            } else {
                                toastMessage(requireContext(), getString(R.string.try_again_later))
                            }
                        }
                    } else {
                        toastMessage(requireContext(), getString(R.string.fill_in_the_blanks))
                    }
                }
                dialog.show()
            }
            builder.show()
        }
    }

    private fun uploadImage() {
        binding.uploadImageViewButton.setOnClickListener {
            if ((ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) + ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                )) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(
                        it, R.string.gallery_permission, Snackbar.LENGTH_LONG
                    ).setAction(R.string.give_permission) {
                        permissionLauncher.launch(neededRuntimePermissions)
                    }.show()
                } else {
                    permissionLauncher.launch(neededRuntimePermissions)
                }
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(galleryIntent)
            }
        }
    }

    private fun uploadButton() {
        with(binding) {
            uploadButton.setOnClickListener {
                val newName = editTextTextPersonName.text.toString().trim()
                val newNumber = edittextNewNumber.text.toString().trim()
                val newSurname = editTextTextPersonName7.text.toString().trim()
                val newdateOfBirthDateText = dateOfBirthDateText.text.toString()
                if (dataControl()) {
                    if (selectedBitmap != null) {
                        val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 400)
                        viewModel.updateProfile(
                            newName,
                            newNumber,
                            newSurname,
                            getImageUri(requireContext(), smallBitmap),
                            newdateOfBirthDateText
                        )
                        observeUpdateProfile()
                    } else {
                        viewModel.updateProfile(
                            newName, newNumber, newSurname, null, newdateOfBirthDateText
                        )
                        observeUpdateProfile()
                    }
                } else {
                    toastMessage(requireContext(), getString(R.string.fill_in_the_blanks))
                }
            }
        }
    }

    private fun observeUpdateProfile() {
        viewModel.changesSaved.observe(viewLifecycleOwner) { isSaved ->
            isSaved?.let {
                if (it) {
                    toastMessage(requireContext(), getString(R.string.changes_saved))
                    findNavController().navigate(R.id.action_editFragment_to_profileFragment)
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                toastMessage(requireContext(), it)
            }
        }
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData = intentFromResult.data
                        selectedUri = imageData
                        if (imageData != null) {
                            try {
                                if (Build.VERSION.SDK_INT >= 28) {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver, imageData
                                    )
                                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                                    binding.uploadImageView.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver, imageData
                                    )
                                    binding.uploadImageView.setImageBitmap(selectedBitmap)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                permissions.entries.forEach {
                    if (it.value && it.key == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        val galeriIntent =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        activityResultLauncher.launch(galeriIntent)
                    }
                }
            }
    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            // Landscape
            width = maximumSize
            val scaleHeight = width / bitmapRatio
            height = scaleHeight.toInt()
        } else {
            // Portrait
            height = maximumSize
            val scaleWidth = height * bitmapRatio
            width = scaleWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver, inImage, IMAGE_NAME, null
        )
        return Uri.parse(path)
    }

    private fun dataControl(): Boolean =
        binding.editTextTextPersonName.text.isNotEmpty() && binding.editTextTextPersonName7.text.isNotEmpty() && binding.edittextNewNumber.text.isNotEmpty() && binding.dateOfBirthDateText.text.isNotEmpty()
}