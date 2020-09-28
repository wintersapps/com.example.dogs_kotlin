package com.example.dogs_kotlin.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.dogs_kotlin.databinding.ItemDogBinding
import com.example.dogs_kotlin.model.DogBreed
import com.example.dogs_kotlin.view.fragments.ListFragmentDirections
import com.example.dogs_kotlin.view.listeners.DogClickListener
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(private val dogsList: ArrayList<DogBreed>): RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(), DogClickListener {

    fun updateDogList(newDogList: List<DogBreed>){
        dogsList.clear()
        dogsList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = ItemDogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog = dogsList[position]
        holder.view.listener = this
    }

    override fun getItemCount() = dogsList.size

    override fun onDogClicked(view: View) {
        val uuid = view.dogUuid.text.toString().toInt()
        val action = ListFragmentDirections.actionGoToDetails()
        action.dogUuid = uuid
        Navigation.findNavController(view).navigate(action)
    }

    class DogViewHolder(var view: ItemDogBinding): RecyclerView.ViewHolder(view.root)
}