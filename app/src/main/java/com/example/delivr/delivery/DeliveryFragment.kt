package com.example.delivr.delivery

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.delivr.MainActivity
import com.example.delivr.R
import com.example.delivr.asPriceString
import com.example.delivr.databinding.FragmentDeliveryBinding

class DeliveryFragment : Fragment() {
    private val viewModel: DeliveryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDeliveryBinding.inflate(inflater, container, false)

        binding.deliveryBtnMakeOrder.setOnClickListener {
            val name = binding.deliveryName.editText!!.text.trim().toString()
            val address = binding.deliveryAddress.editText!!.text.trim().toString()
            val tel = binding.deliveryTel.editText!!.text.trim().toString()
            val email = binding.deliveryEmail.editText!!.text.trim().toString()
            viewModel.makeOrder(name, address, tel, email)
            hideKeyboard(binding.root)
        }

        viewModel.user.observe(viewLifecycleOwner) {
            binding.deliveryName.editText!!.setText(it.name)
            binding.deliveryAddress.editText!!.setText(it.address)
            binding.deliveryTel.editText!!.setText(it.tel)
            binding.deliveryEmail.editText!!.setText(it.email)
        }

        viewModel.cartCost.observe(viewLifecycleOwner) {
            binding.deliveryBtnMakeOrder.text = getString(R.string.make_order_for_the_price, it.asPriceString())
        }

        viewModel.nameError.observe(viewLifecycleOwner) {
            if (it) {
                binding.deliveryName.error = getString(R.string.name_is_required)
            } else {
                binding.deliveryName.isErrorEnabled = false
                binding.deliveryName.error = null
            }
        }

        viewModel.addressError.observe(viewLifecycleOwner) {
            if (it) {
                binding.deliveryAddress.error = getString(R.string.address_is_required)
            } else {
                binding.deliveryAddress.isErrorEnabled = false
                binding.deliveryAddress.error = null
            }
        }

        viewModel.telError.observe(viewLifecycleOwner) {
            if (it) {
                binding.deliveryTel.error = getString(R.string.wrong_tel)
            } else {
                binding.deliveryTel.isErrorEnabled = false
                binding.deliveryTel.error = null
            }
        }

        viewModel.emailError.observe(viewLifecycleOwner) {
            if (it) {
                binding.deliveryEmail.error = getString(R.string.wrong_email)
            } else {
                binding.deliveryEmail.isErrorEnabled = false
                binding.deliveryEmail.error = null
            }
        }

        viewModel.orderStatus.observe(viewLifecycleOwner) {
            when (it) {
                OrderStatus.IN_PROGRESS -> {

                    binding.delivery.visibility = View.GONE
                    binding.deliveryLoading.visibility = View.VISIBLE
                }
                OrderStatus.SUCCESS -> {
                    binding.deliveryLoading.visibility = View.GONE
                    binding.deliveryOrderResult.visibility = View.VISIBLE
                    binding.deliveryOrderResultText.text = getString(R.string.order_accepted)
                    binding.deliveryOrderResultImg.setImageResource(R.drawable.ic_baseline_done_48)
                    clearCartSizeBadge()
                }
                OrderStatus.ERROR -> {
                    binding.deliveryLoading.visibility = View.GONE
                    binding.deliveryOrderResult.visibility = View.VISIBLE
                    binding.deliveryOrderResultText.text = getString(R.string.failed_to_make_the_order)
                    binding.deliveryOrderResultImg.setImageResource(R.drawable.ic_baseline_close_48)
                }
            }
        }

        viewModel.eNavigateToMainScreen.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(
                    DeliveryFragmentDirections.actionDeliveryFragmentToCategorySelectorFragment()
                )
                viewModel.eventNavigateToMainScreenHandled()
            }
        }

        return binding.root
    }

    private fun hideKeyboard(view: View) {
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)!! as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clearCartSizeBadge() {
        (requireActivity() as MainActivity).setCartSizeBadge(0)
    }
}
