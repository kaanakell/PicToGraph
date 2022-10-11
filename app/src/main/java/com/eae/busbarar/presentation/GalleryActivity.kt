package com.eae.busbarar.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.eae.busbarar.databinding.ActivityGalleryBinding
import com.eae.busbarar.data.model.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class GalleryActivity : AppCompatActivity(), IGallery {
    private lateinit var binding: ActivityGalleryBinding
    private val images: ArrayList<Image> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(1600)
            binding.progressBar.visibility = View.GONE
            val adapter = GalleryAdapter(this@GalleryActivity)
            binding.recyclerView.layoutManager = GridLayoutManager(this@GalleryActivity, 3)
            binding.recyclerView.adapter = adapter
            images.reverse()
            adapter.list = images
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        getImages()
    }

    override fun onItemClick(item: Image) {
        PreviewActivity.path = item.path
        startActivity(Intent(this, PreviewActivity::class.java))
    }

    private fun getImages() {
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        path?.toURI()?.let { safeUri ->
            val file = File(safeUri)
            val files = file.listFiles()
            files?.let { safeFiles ->
                for (i in safeFiles.indices) {
                    val image = Image(safeFiles[i].name, safeFiles[i].path)
                    images.add(image)
                }
            }
        }
    }
}