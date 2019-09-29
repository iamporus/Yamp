package com.prush.justanotherplayer.ui.walkthrough

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.ui.main.MainActivity
import com.prush.justanotherplayer.ui.walkthrough.WalkThroughFragment.Companion.RESOURCE_ID
import kotlinx.android.synthetic.main.walkthrough_intro.*
import kotlinx.android.synthetic.main.walkthrough_layout.*
import kotlinx.android.synthetic.main.walkthrough_navigate.*

private const val PAGE_COUNT = 4
private const val ANIMATE_DURATION = 100000L
private const val SENTENCE_PAUSE_DURATION = 100L

class WalkThroughActivity : AppCompatActivity(),
    WalkThroughFragment.OnNextArrowClickedListener {

    private lateinit var animationUpdateListener: ValueAnimator.AnimatorUpdateListener
    private lateinit var animator: ValueAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.walkthrough_layout)

        setupViewPager()

        animateBackground()

        setupSummary()
    }

    private fun setupViewPager() {
        viewPager.adapter = object :
            FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getItem(position: Int): Fragment {
                when (position) {
                    0 -> {
                        return WalkThroughFragment.newInstance(Bundle().apply {
                            putInt(
                                RESOURCE_ID,
                                R.layout.walkthrough_intro
                            )
                        }, this@WalkThroughActivity)
                    }
                    1 -> {
                        return WalkThroughFragment.newInstance(Bundle().apply {
                            putInt(
                                RESOURCE_ID,
                                R.layout.walkthrough_control
                            )
                        }, this@WalkThroughActivity)
                    }
                    2 -> {
                        return WalkThroughFragment.newInstance(Bundle().apply {
                            putInt(
                                RESOURCE_ID,
                                R.layout.walkthrough_contribute
                            )
                        }, this@WalkThroughActivity)
                    }
                    else -> {
                        return WalkThroughFragment.newInstance(Bundle().apply {
                            putInt(
                                RESOURCE_ID,
                                R.layout.walkthrough_navigate
                            )
                        }, this@WalkThroughActivity)
                    }
                }
            }

            override fun getCount(): Int {
                return PAGE_COUNT
            }

        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        summaryTextView.setTypedText(R.string.app_name_expanded_typed)
                    }
                    1 -> {
                        summaryTextView.setTypedText(R.string.intro_google_assistant_typed)
                    }
                    2 -> {
                        summaryTextView.setTypedText(R.string.intro_open_source_typed)
                    }
                    3 -> {
                        summaryTextView.setTypedText(R.string.intro_share_typed)
                    }
                }
            }
        })

    }

    private fun animateBackground() {
        animator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.duration = ANIMATE_DURATION

        animationUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            val progress = animation?.animatedValue as Float
            val width = imageView.width / PAGE_COUNT
            val translationX = width * progress * -1
            imageView.translationX = translationX
        }
        animator.addUpdateListener(animationUpdateListener)
        animator.start()
    }

    private fun setupSummary() {

        lifecycle.addObserver(summaryTextView.lifecycleObserver)

        summaryTextView.apply {

            splitSentences(false)
            setSentencePause(SENTENCE_PAUSE_DURATION)
            setTypedText(R.string.app_name_expanded_typed)
        }
    }

    override fun onNextArrowClicked() {

        viewPager.currentItem = viewPager.currentItem + 1
    }

    override fun onStop() {
        super.onStop()
        animator.cancel()
        animator.removeUpdateListener(animationUpdateListener)
        lifecycle.removeObserver(summaryTextView.lifecycleObserver)
    }
}

class WalkThroughFragment : Fragment() {

    interface OnNextArrowClickedListener {

        fun onNextArrowClicked()
    }

    private var layoutResourceId: Int = R.layout.walkthrough_intro
    lateinit var arrowClickedListener: OnNextArrowClickedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        layoutResourceId = arguments?.getInt(RESOURCE_ID, layoutResourceId)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(layoutResourceId, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (gotoLibraryButton != null) {
            gotoLibraryButton.setOnClickListener {
                startMainActivity()
            }
        }
        if (nextArrowImageView != null) {
            nextArrowImageView.setOnClickListener {
                arrowClickedListener.onNextArrowClicked()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object {

        const val RESOURCE_ID = "layoutResourceID"

        @JvmStatic
        fun newInstance(
            args: Bundle,
            listener: OnNextArrowClickedListener
        ): WalkThroughFragment {
            return WalkThroughFragment().apply {
                arguments = args
                arrowClickedListener = listener
            }
        }
    }
}
