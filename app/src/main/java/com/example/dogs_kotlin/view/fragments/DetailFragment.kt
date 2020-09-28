package com.example.dogs_kotlin.view.fragments

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.dogs_kotlin.R
import com.example.dogs_kotlin.databinding.FragmentDetailBinding
import com.example.dogs_kotlin.databinding.SendSmsDialogBinding
import com.example.dogs_kotlin.model.DogBreed
import com.example.dogs_kotlin.model.DogPalette
import com.example.dogs_kotlin.model.SmsInfo
import com.example.dogs_kotlin.view.MainActivity
import com.example.dogs_kotlin.viewmodel.DetailViewModel

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailViewModel
    private var dogUuid = 0
    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }
        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(dogUuid)
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                view?.let {
                    sendSmsStarted = true
                    (activity as MainActivity).checkSmsPermission()
                }
            }
            R.id.action_share -> {
                view?.let {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed")
                    intent.putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                    intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                    startActivity(Intent.createChooser(intent, "Share with"))
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(viewLifecycleOwner, { dog ->
            currentDog = dog
            dog?.let {
                binding.dog = it
                it.imageUrl?.let { url ->
                    setupBackgroundColor(url)
                }
            }
        })
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = DogPalette(intColor)
                            binding.palette = myPalette
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {
                val smsInfo = SmsInfo(
                    "",
                    "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}",
                    currentDog?.imageUrl
                )

                val dialogBinding = SendSmsDialogBinding.inflate(LayoutInflater.from(it), null, false)

                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS"){_,_ ->
                        if(!dialogBinding.sendDestinationTextInputEditText.text.isNullOrEmpty()){
                            smsInfo.to = dialogBinding.sendDestinationTextInputEditText.text.toString()
                            sendSms(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel"){ _, _ ->}
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSms(smsInfo: SmsInfo){
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(smsInfo.to, null, smsInfo.text, pendingIntent, null)
    }
}