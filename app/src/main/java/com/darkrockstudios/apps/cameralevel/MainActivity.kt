package com.darkrockstudios.apps.cameralevel

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Rational
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), Orientation.Listener {
    companion object {
        private const val KEY_AUTO_CAMERA = "auto_camera"
    }

    private lateinit var mOrientation: Orientation

    private var firstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        pip_button.setOnClickListener {
            enterPipMode()
        }

        camera_button.setOnClickListener {
            launchCamera()
        }

        should_launch_camera_checkbox.isChecked = getAutoCamera()
        should_launch_camera_checkbox.setOnCheckedChangeListener { _, isChecked ->
            setAutoCamera(isChecked)
        }

        mOrientation = Orientation(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_close -> {
                finishAndRemoveTask()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mOrientation.startListening(this)

        if (firstLaunch) {
            firstLaunch = false
            if (getAutoCamera()) {
                launchCamera()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mOrientation.stopListening()
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        if (intent.resolveActivity(packageManager) != null) {

            enterPipMode()
            startActivity(intent)
        }
        // Error
        else {
            Toast.makeText(this, R.string.error_no_camera, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enterPipMode() {
        val rect = Rect()
        level_view.getDrawingRect(rect)

        val ration = Rational(3, 2)

        val pipParams = PictureInPictureParams.Builder()
            .setAspectRatio(ration)
            .setSourceRectHint(rect)
            .build()
        enterPictureInPictureMode(pipParams)
    }

    override fun onUserLeaveHint() {
        enterPipMode()
    }

    override fun onOrientationChanged(pitch: Float, roll: Float) {
        level_view?.updateOrientation(roll = roll, pitch = pitch, yaw = 0f)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (isInPictureInPictureMode) {
            showUI(false)
        } else {
            showUI(true)
        }
    }

    private fun showUI(show: Boolean) {
        val visible = if (show) View.VISIBLE else View.GONE
        toolbar?.visibility = visible
        pip_button?.visibility = visible
        camera_button?.visibility = visible
        should_launch_camera_checkbox?.visibility = visible
    }

    private fun setAutoCamera(shouldAutoCamera: Boolean) {
        // If we are toggling it on, the user will expect it to work without restarting the app
        if (shouldAutoCamera) {
            firstLaunch = true
        }

        val prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        prefs.edit().putBoolean(KEY_AUTO_CAMERA, shouldAutoCamera).apply()
    }

    private fun getAutoCamera(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
        return prefs.getBoolean(KEY_AUTO_CAMERA, false)
    }
}
