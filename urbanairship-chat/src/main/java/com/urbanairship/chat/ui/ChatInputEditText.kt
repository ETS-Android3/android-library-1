package com.urbanairship.chat.ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import com.urbanairship.chat.R

/** Custom `EditText` that supports image input via URI. */
internal class ChatInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    interface ChatInputListener {
        fun onImageSelected(imageUri: String)
        fun onActionDone()
    }

    private var listener: ChatInputListener? = null

    init {
        setOnEditorActionListener { _, actionId, event ->
            if (isEditorActionDone(actionId, event)) {
                listener?.onActionDone()
                true
            } else {
                false
            }
        }
    }

    override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection {
        val ic: InputConnection = super.onCreateInputConnection(editorInfo)
        EditorInfoCompat.setContentMimeTypes(editorInfo, arrayOf("image/png", "image/gif", "image/jpeg"))

        val callback =
                InputConnectionCompat.OnCommitContentListener { inputContentInfo, flags, _ ->
                    val lacksPermission = (flags and
                            InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0
                    // read and display inputContentInfo asynchronously
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && lacksPermission) {
                        try {
                            inputContentInfo.requestPermission()
                        } catch (e: Exception) {
                            return@OnCommitContentListener false // return false if failed
                        }
                    }

                    val result = imageSelected(inputContentInfo.linkUri)
                    inputContentInfo.releasePermission()
                    result
                }
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback)
    }

    /**
     * Sets a `ChatInputListener` on this `View`, overwriting any previously registered listener.
     */
    internal fun setListener(listener: ChatInputListener) {
        this.listener = listener
    }

    private fun imageSelected(imageUri: Uri?): Boolean {
        if (imageUri == null) {
            return false
        }
        return listener?.let {
            it.onImageSelected(imageUri.toString())
            true
        } ?: false
    }

    private fun isEditorActionDone(actionId: Int, event: KeyEvent?): Boolean =
        when (actionId) {
            // Return true if the user pressed the done or send action on the on-screen keyboard.
            EditorInfo.IME_ACTION_DONE,
            EditorInfo.IME_ACTION_SEND -> true
            // Return true if the user pressed the 'Enter' key on a physical keyboard.
            EditorInfo.IME_ACTION_UNSPECIFIED ->
                event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER
            else -> false
        }
}