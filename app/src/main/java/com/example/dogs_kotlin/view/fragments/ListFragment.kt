package com.example.dogs_kotlin.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogs_kotlin.R
import com.example.dogs_kotlin.databinding.FragmentListBinding
import com.example.dogs_kotlin.view.adapters.DogsListAdapter
import com.example.dogs_kotlin.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ListViewModel
    private val dogsListAdapter = DogsListAdapter(arrayListOf())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        binding.dogsListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }

        swipeRefreshLayout.setOnRefreshListener {
            binding.dogsListRecyclerView.visibility = View.GONE
            binding.listErrorTextView.visibility = View.GONE
            binding.loadingDataProgressBar.visibility = View.VISIBLE
            viewModel.refreshByPassCache()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                view?.let {
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionGoToTheSettings())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel(){
        viewModel.dogs.observe(viewLifecycleOwner, { dogs ->
            dogs?.let {
                binding.dogsListRecyclerView.visibility = View.VISIBLE
                dogsListAdapter.updateDogList(it)
            }
        })

        viewModel.dogsLoadError.observe(viewLifecycleOwner, {isError ->
            isError?.let {
                binding.listErrorTextView.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, {isLoading ->
            isLoading?.let {
                binding.loadingDataProgressBar.visibility = if(it) View.VISIBLE else View.GONE
                if(it){
                    binding.listErrorTextView.visibility = View.GONE
                    binding.dogsListRecyclerView.visibility = View.GONE
                }
            }
        })
    }
}