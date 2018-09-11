/**  Copyright © 2018 Socket Mobile, Inc. */
package com.socketmobile.stockCount.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.socketmobile.stockCount.R
import com.socketmobile.stockCount.helper.shownInstruction
import kotlinx.android.synthetic.main.activity_instruction.*

class InstructionActivity : AppCompatActivity() {
    val instructionStrings = listOf(R.string.instruction_1, R.string.instruction_2, R.string.instruction_3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)
        supportActionBar?.hide()

        imageViewPager.adapter = InstructionPagerAdapter(supportFragmentManager)
        imageIndicator.setViewPager(imageViewPager)

        imageViewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                imageIndicator.setSelected(position)
                descTextView.setText(instructionStrings[position])
            }
        })
        descTextView.setText(instructionStrings[0])

        startButton.setOnClickListener {
            if (intent.hasExtra("fromOptions")) {
                finish()
            } else {
                shownInstruction(this)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    class InstructionPageFragment: Fragment() {
        var instructionImageResId: Int = 0

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.view_instruction, container, false)
            if (instructionImageResId != 0) {
                view.findViewById<ImageView>(R.id.instructionImageView).setImageResource(instructionImageResId)
            }
            return view
        }
    }

    class InstructionPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
        private val resArray = listOf(R.drawable.instruction_1, R.drawable.instruction_2, R.drawable.instruction_3)

        override fun getItem(position: Int): Fragment {
            val f = InstructionPageFragment()
            f.instructionImageResId = resArray[position]
            return f
        }

        override fun getCount(): Int {
            return resArray.size
        }

    }
}
