/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.socketmobile.stockcount.R
import kotlinx.android.synthetic.main.fragment_companion_dialog.*


class CompanionDialogFragment : DialogFragment() {
    var companionDialogListener: OnCompanionDialogListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_companion_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()

        closeImageButton.setOnClickListener {
            dismiss()
        }
        tryAgainButton.setOnClickListener {
            dismiss()
        }
        launchCompanionTextView.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.companion_store_url)))
            startActivity(i)
            dismiss()
        }
        useCameraTextView.setOnClickListener {
            dismiss()
            companionDialogListener?.onUseCamera()
        }
    }
}

interface OnCompanionDialogListener {
    fun onUseCamera()
}
