package com.asimodabas.deepfake_video_detection_app.ui.fragment.create

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.asimodabas.deepfake_video_detection_app.R
import com.asimodabas.deepfake_video_detection_app.databinding.FragmentCreateBinding
import com.asimodabas.deepfake_video_detection_app.databinding.LayoutBottomKvkkBinding
import com.asimodabas.deepfake_video_detection_app.ui.activity.FlowActivity
import com.asimodabas.deepfake_video_detection_app.util.Constants.IMAGE_NAME
import com.asimodabas.deepfake_video_detection_app.util.datePickerCreate
import com.asimodabas.deepfake_video_detection_app.util.toastMessage
import com.asimodabas.deepfake_video_detection_app.util.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class CreateFragment : Fragment(R.layout.fragment_create) {

    private val binding by viewBinding(FragmentCreateBinding::bind)
    private val viewModel: CreateViewModel by viewModels()
    private var selectedBitmap: Bitmap? = null
    private var selectedUri: Uri? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private val neededRuntimePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        registerLauncher()
        super.onViewCreated(view, savedInstanceState)

        textViewKvkkText.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(), R.style.ThemeOverlay_MaterialComponents_BottomSheetDialog
            )
            val bottomSheetBinding =
                LayoutBottomKvkkBinding.inflate(LayoutInflater.from(requireContext()))
            bottomSheetDialog.setContentView(bottomSheetBinding.root)
            bottomSheetDialog.show()
        }
        dateOfBirthDateText.setOnClickListener {
            datePickerCreate(requireContext(), dateOfBirthDateText)
        }
        loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_createFragment_to_loginFragment)
        }
        createAddImage()
        createButton()
    }

    private fun createAddImage() {
        binding.imageView4.setOnClickListener {
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

    private fun createButton() {
        with(binding) {
            fcreateButton.setOnClickListener {
                val name = nameEditText.text.toString().trim()
                val surname = surnameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val number = numberEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                val dateOfBirth = dateOfBirthDateText.text.toString().trim()
                val userPassw = passwordEditText.text.toString().trim()
                if (dataControl()) {
                    if (selectedBitmap != null) {
                        val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 400)
                        viewModel.registerToApp(
                            email,
                            number,
                            password,
                            name,
                            surname,
                            getImageUri(requireContext(), smallBitmap),
                            dateOfBirth,
                            userPassw,
                        )
                        observeData()
                    } else {
                        viewModel.registerToApp(
                            email,
                            number,
                            password,
                            name,
                            surname,
                            null,
                            dateOfBirth,
                            userPassw,
                        )
                        observeData()
                    }
                } else {
                    if (TextUtils.isEmpty(name)) {
                        nameEditText.error = getString(R.string.please_enter_valid_name)
                    }
                    if (TextUtils.isEmpty(surname)) {
                        surnameEditText.error = getString(R.string.please_enter_valid_surname)
                    }
                    if (TextUtils.isEmpty(email)) {
                        emailEditText.error = getString(R.string.please_enter_valid_mail)
                    }
                    if (TextUtils.isEmpty(number)) {
                        numberEditText.error = getString(R.string.please_enter_valid_number)
                    }
                    if (TextUtils.isEmpty(password)) {
                        passwordEditText.error = getString(R.string.please_enter_valid_password)
                    }
                    toastMessage(requireContext(), getString(R.string.fill_in_the_blanks))
                }
            }
        }
    }

    private fun observeData() {
        viewModel.dataConfirmation.observe(viewLifecycleOwner) { dataConfirm ->
            dataConfirm?.let { confirm ->
                if (confirm) {
                    activity?.let {
                        toastMessage(
                            requireContext(),
                            "${resources.getString(R.string.welcome)} ${binding.nameEditText.text}"
                        )
                        it.startActivity(Intent(it, FlowActivity::class.java))
                        it.finish()
                    }
                }
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
                                    binding.imageView4.setImageBitmap(selectedBitmap)
                                } else {
                                    selectedBitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().contentResolver, imageData
                                    )
                                    binding.imageView4.setImageBitmap(selectedBitmap)
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
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        activityResultLauncher.launch(galleryIntent)
                    }
                }
            }
    }

    private fun makeSmallerBitmap(image: Bitmap, maximumSize: Int): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            width = maximumSize
            val scaleHeight = width / bitmapRatio
            height = scaleHeight.toInt()
        } else {
            height = maximumSize
            val scaleWidth = height * bitmapRatio
            width = scaleWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.contentResolver, inImage, IMAGE_NAME, null
        )
        return Uri.parse(path)
    }

    private fun dataControl(): Boolean =
        binding.nameEditText.text.isNotEmpty() && binding.surnameEditText.text.isNotEmpty() && binding.emailEditText.text.isNotEmpty() && binding.numberEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty() && binding.dateOfBirthDateText.text.isNotEmpty()
}