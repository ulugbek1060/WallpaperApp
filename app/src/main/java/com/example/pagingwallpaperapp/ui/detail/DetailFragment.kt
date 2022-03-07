package com.example.pagingwallpaperapp.ui.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pagingwallpaperapp.R
import com.example.pagingwallpaperapp.databinding.FragmentDetailBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File


private const val PERMISSION_REQUEST_CODE = 1

class DetailFragment : Fragment(R.layout.fragment_detail) {

  private val binding by viewBinding(FragmentDetailBinding::bind)
  private val args by navArgs<DetailFragmentArgs>()
  private var isShowed = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    hideSystemUI()
  }

  @SuppressLint("SetTextI18n")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.apply {
      val photo = args.unsplashPhoto
      Glide.with(this@DetailFragment)
        .load(photo.urls.full)
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
          ): Boolean {
            progressBar.isVisible = false
            return false
          }

          override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            progressBar.isVisible = false
            textViewAuthor.isVisible = true
            textViewDescription.isVisible = photo.description != ""
            return false
          }
        })
        .error(R.drawable.ic_error)
        .into(imageView)

      val uri = Uri.parse(photo.user.attributes)
      val intent = Intent(Intent.ACTION_VIEW, uri)

      textViewDescription.text = photo.description
      textViewAuthor.apply {
        text = "Photo by ${photo.user.name} on Unsplash"
        paint.isUnderlineText = true
        setOnClickListener {
          context.startActivity(intent)
        }
      }

      buttonBack.setOnClickListener {
        findNavController().popBackStack()
      }

      buttonDownload.setOnClickListener {
        if (checkPermission()) {
          downloadImageNew("${System.currentTimeMillis()}", photo.urls.full)
        } else {
          ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
          )
        }
      }

      viewClick.setOnClickListener {
        if (isShowed) {
          isShowed = false
          relativeLayoutContainer.isVisible = isShowed
          linearLayoutContainer.isVisible = isShowed
        } else {
          isShowed = true
          relativeLayoutContainer.isVisible = isShowed
          linearLayoutContainer.isVisible = isShowed
        }
      }

      buttonShare.setOnClickListener {
        shareSubject(requireContext(), Bundle(), photo.urls.full)
      }
    }

    setHasOptionsMenu(true)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        findNavController().popBackStack()
        true
      }
      else -> {
        super.onOptionsItemSelected(item)
      }
    }
  }

  private fun downloadImageNew(filename: String, downloadUrlOfImage: String) {
    try {
      val dm = activity?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
      val downloadUri = Uri.parse(downloadUrlOfImage)
      val request = DownloadManager.Request(downloadUri)
      request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        .setAllowedOverRoaming(false)
        .setTitle(filename)
        .setMimeType("image/jpeg")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(
          Environment.DIRECTORY_PICTURES,
          File.separator.toString() + filename + ".jpg"
        )
      dm!!.enqueue(request)
      Snackbar.make(binding.root, "Image download started.", Snackbar.LENGTH_LONG).show()
    } catch (e: Exception) {
      Toast.makeText(requireContext(), "Image download failed.", Toast.LENGTH_SHORT).show()
    }
  }

  override fun onDetach() {
    super.onDetach()
    showSystemUI()
  }

//  fun setWallpaper() {
//    val context: Context = activity?.getBaseContext()
//    val mBitmap = BitmapFactory.decodeResource(resources, mImageIds.get(pos))
//    context.setWallpaper(mBitmap)
//  }

  private fun checkPermission(): Boolean {
    return if (ContextCompat.checkSelfPermission(
        requireActivity(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      true
    } else {
      ActivityCompat.requestPermissions(
        requireActivity(),
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        PERMISSION_REQUEST_CODE
      )
      false
    }
  }

  private fun showSystemUI() {
    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    (activity as AppCompatActivity?)!!.supportActionBar!!.show()
  }

  private fun hideSystemUI() {
    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
  }

  private fun shareSubject(context: Context, bundle: Bundle, text: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    Intent.EXTRA_SUBJECT
    intent.putExtra(Intent.EXTRA_TEXT, text)
    ContextCompat.startActivity(context, intent, bundle)
  }
}