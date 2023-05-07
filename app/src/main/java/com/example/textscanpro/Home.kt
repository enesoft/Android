package com.example.textscanpro

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.bumptech.glide.Glide

class Home : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: ImageAdapter
    private lateinit var searchQuery: SearchView
    private val DELETE_IMAGE_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        searchQuery = view.findViewById<SearchView>(R.id.searchView)
        searchQuery.clearFocus()
        searchQuery.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.updateList(getImageFiles(newText))
                return true
            }
        })

        layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        adapter = ImageAdapter(getImageFiles())
        recyclerView.adapter = adapter
        return view
    }

    private fun getImageFiles(searchQuery : String = ""): List<Pair<Uri, String>> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val selection = if (searchQuery.isNotEmpty()) {
            "${MediaStore.Images.Media.DISPLAY_NAME} LIKE 'textscanpro_%${searchQuery}%'"
        } else {
            "${MediaStore.Images.Media.DISPLAY_NAME} LIKE 'textscanpro_%'"
        }

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val images = mutableListOf<Pair<Uri, String>>()
        activity?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                images.add(Pair(contentUri, name))
            }
        }
        return images
    }

    class ImageAdapter(private var images: List<Pair<Uri, String>>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageRecycle)
            val titleView: TextView = itemView.findViewById(R.id.title)
        }

        fun updateList(newList: List<Pair<Uri, String>>) {
            images = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val context = holder.imageView.context
            val imageUri = images[position]

            Glide.with(context)
                .load(imageUri.first)
                .centerCrop()
                .placeholder(R.drawable.baseline_person_24)
                .into(holder.imageView)

            holder.titleView.text = imageUri.second.replace("textscanpro_", "").replace(".jpg", "")

            holder.itemView.setOnClickListener {
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("imageUri", imageUri.first.toString())
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return images.size
        }
    }

}