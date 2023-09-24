package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class PhotoFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(inflater, container, false)

        binding.imageAtt.visibility = View.VISIBLE
        Glide.with(binding.imageAtt)
            .load("${BuildConfig.MEDIA_URL}${arguments?.textArg}")
            .timeout(10_000)
            .into(binding.imageAtt)


        return binding.root
    }
}