/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * Copyright (c) 2025 Rve <rve27github@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.about

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import me.zhanghai.android.files.databinding.AboutFragmentBinding
import me.zhanghai.android.files.ui.LicensesDialogFragment
import me.zhanghai.android.files.util.createViewIntent
import me.zhanghai.android.files.util.startActivitySafe

class AboutFragment : Fragment() {
    private lateinit var binding: AboutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        AboutFragmentBinding.inflate(inflater, container, false)
            .also { binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.gitHubLayout.setOnClickListener { startActivitySafe(GITHUB_URI.createViewIntent()) }
        binding.licensesLayout.setOnClickListener { LicensesDialogFragment.show(this) }
        binding.authorNameLayout.setOnClickListener {
            startActivitySafe(AUTHOR_RESUME_URI.createViewIntent())
        }
        binding.authorRvFilesNameLayout.setOnClickListener {
            startActivitySafe(AUTHOR_RVFILES_TELEGRAM_URI.createViewIntent())
        }
        binding.authorGitHubLayout.setOnClickListener {
            startActivitySafe(AUTHOR_GITHUB_URI.createViewIntent())
        }
        binding.authorRvFilesGitHubLayout.setOnClickListener {
            startActivitySafe(AUTHOR_RVFILES_GITHUB_URI.createViewIntent())
        }
        binding.authorTwitterLayout.setOnClickListener {
            startActivitySafe(AUTHOR_TWITTER_URI.createViewIntent())
        }
    }

    companion object {
        private val GITHUB_URI = Uri.parse("https://github.com/Rve27/RvFiles")
        private val PRIVACY_POLICY_URI = Uri.parse("https://github.com/Rve27/RvFiles/blob/master/PRIVACY.md")
        private val AUTHOR_RESUME_URI = Uri.parse("https://resume.zhanghai.me/")
        private val AUTHOR_GITHUB_URI = Uri.parse("https://github.com/zhanghai")
        private val AUTHOR_TWITTER_URI = Uri.parse("https://twitter.com/zhanghai95")
        private val AUTHOR_RVFILES_GITHUB_URI = Uri.parse("https://github.com/Rve27")
        private val AUTHOR_RVFILES_TELEGRAM_URI = Uri.parse("https://t.me/rve270")
    }
}
