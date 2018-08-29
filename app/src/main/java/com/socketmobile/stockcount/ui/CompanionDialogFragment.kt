package com.socketmobile.stockcount.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.socketmobile.stockcount.R
import kotlinx.android.synthetic.main.fragment_companion_dialog.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CompanionDialogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CompanionDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CompanionDialogFragment : DialogFragment() {
    var companionDialogListener: OnCompanionDialogListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_companion_dialog, container, false)

        return view
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
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.socketmobile.companion"))
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
