package com.task.newapp.ui.activities.post

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.luck.picture.lib.entity.LocalMedia
import com.task.newapp.R
import com.task.newapp.adapter.post.PostPagerAdapter
import com.task.newapp.databinding.ActivityPostPagerBinding
import com.task.newapp.utils.*
import com.task.newapp.utils.photoediting.EditImageActivity
import java.lang.reflect.Type


class PostPagerActivity : AppCompatActivity() {

    lateinit var binding: ActivityPostPagerBinding
    private lateinit var postPagerAdapter: PostPagerAdapter
    private lateinit var arrayListMedia: ArrayList<LocalMedia>
    private var menuPost: Menu? = null
    var currPosition = 0
    private val ACTION_REQUEST_EDITIMAGE = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_pager)

//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if (!intent.extras?.isEmpty!!) {

            currPosition = intent.getIntExtra("position", 0)

            val type: Type = object : TypeToken<ArrayList<LocalMedia>>() {}.type
            arrayListMedia = Gson().fromJson(intent.getStringExtra("arraylist"), type)

            postPagerAdapter = PostPagerAdapter(this, arrayListMedia)
            binding.viewPagerMain.adapter = postPagerAdapter

            binding.viewPagerMain.currentItem = currPosition

            pagerListener()
        }
    }

    private fun pagerListener() {
        binding.viewPagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                showLog("viewPagerMain", position.toString())
                currPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.post_edit_menu, menu)
        menuPost = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                //Open Edit Activity
                openEditImageActivity(arrayListMedia[currPosition].path, currPosition/*, arrayListMedia[currPosition].type*/)
            }
            R.id.action_delete -> {
                //Show Delete Dialog
                showDeleteDialog()
            }
        }

        return super.onOptionsItemSelected(item)

    }

    private fun showDeleteDialog() {
        DialogUtils().showConfirmationYesNoDialog(this, "", getString(R.string.delete_post_item), object : DialogUtils.DialogCallbacks {
            override fun onPositiveButtonClick() {
                try {
                    if (arrayListMedia.isNotEmpty()) {
                        arrayListMedia.removeAt(currPosition)
                        postPagerAdapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onNegativeButtonClick() {

            }

            override fun onDefaultButtonClick(actionName: String) {
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        var intent = Intent().putExtra("arraylist", Gson().toJson(arrayListMedia))
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

    private fun openEditImageActivity(path: String?, position: Int/*, type: String*/) {
//        flag_pos = position
//        flag_type = type
//        val outputFile: File = FileUtils.genEditFile()

        var intent = Intent(this, EditImageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        intent.putExtra("filepath", path)
        startActivityForResult(intent, ACTION_REQUEST_EDITIMAGE)
    }
}
