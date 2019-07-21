package com.kikyoung.currency.base

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.kikyoung.currency.R
import com.kikyoung.currency.util.extension.observeChanges
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import kotlin.reflect.KClass

abstract class BaseFragment<ViewModelType : BaseViewModel>(clazz: KClass<ViewModelType>) : Fragment() {

    val viewModel: ViewModelType by sharedViewModel(clazz)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.serverErrorLiveData().observeChanges(this) {
            Timber.e(it.message)
            showSnackBar(R.string.common_error_server)
        }

        viewModel.networkErrorLiveData().observeChanges(this) {
            Timber.e(it.message)
            showSnackBar(R.string.common_error_network)
        }
    }

    private fun showSnackBar(@StringRes messageId: Int) {
        // TODO Don't keep showing duplicated messages.
        Snackbar.make(view!!, getString(messageId), Snackbar.LENGTH_LONG).show()
    }
}
