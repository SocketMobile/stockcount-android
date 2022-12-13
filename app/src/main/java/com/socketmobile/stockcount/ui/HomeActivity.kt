/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.TextView
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.helper.createFile
import com.socketmobile.stockcount.helper.getFiles
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        filesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()

        filesRecyclerView.adapter = FilesAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.addFile -> {
                goEditActivity(createFile(this))
            }
            R.id.showOptions -> {
                val i = Intent(this, OptionsActivity::class.java)
                startActivity(i)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun goEditActivity(fileName: String) {
        val i = Intent(this, EditActivity::class.java)
        i.putExtra("fileName", fileName)
        startActivity(i)
    }

    class FilesAdapter: RecyclerView.Adapter<FilesAdapter.ViewHolder>() {
        private val files = getFiles()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val fileView = inflater.inflate(R.layout.view_file, parent, false)

            return ViewHolder(fileView)
        }

        override fun getItemCount(): Int {
            return files.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = files[position]
            holder.nameTextView?.text = item?.fileTitle
            holder.contentTextView?.text = item?.firstScan

            holder.itemView.setOnClickListener {
                val activity = it.context as? HomeActivity
                if (item != null) {
                    activity?.goEditActivity(item.fileName)
                }
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView = itemView?.findViewById<TextView>(R.id.fileTitleTextView)
            val contentTextView = itemView?.findViewById<TextView>(R.id.scanContentTextView)
        }
    }
}
